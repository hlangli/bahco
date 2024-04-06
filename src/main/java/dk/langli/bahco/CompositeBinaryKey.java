package dk.langli.bahco;

import static dk.langli.bahco.Bahco.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.text.StringSubstitutor;

import lombok.Builder;

@Builder
public class CompositeBinaryKey {
	private final ArrayList<KeyDef> keyDefs;
	
	public String decode(String code) {
		return format(code, keyDefs.stream()
				.map(keyDef -> subst("${%s}", keyDef.getName()))
				.collect(Collectors.joining()));
	}

	public Map<String, Integer> decodeToMap(String code) {
		byte[] src = decodeToBytes(code);
		String binaryString = new BigInteger(src).toString(2);
		return decodeBinaryToMap(binaryString);
	}
	
	private Map<String, Integer> decodeBinaryToMap(String binaryString) {
		int totalBinaryLength = keyDefs.stream()
				.mapToInt(def -> def.getBinaryLength())
				.sum();
		String paddedBinaryString = StringUtils.leftPad(binaryString, totalBinaryLength, '0');
		return keyDefs.stream()
				.collect(Collectors.toMap(KeyDef::getName, def -> decodeKey(paddedBinaryString, def)));
	}
	
	private Integer decodeKey(String binaryString, KeyDef keyDef) {
		int start = keyDefs.subList(0, keyDefs.indexOf(keyDef)).stream()
				.mapToInt(KeyDef::getBinaryLength)
				.sum();
		String bits = binaryString.substring(start, start + keyDef.getBinaryLength());
		Integer value = Integer.parseUnsignedInt(bits, 2) + keyDef.getFloor();
		if(keyDef.getCeil() < value) {
			throw new CompositeBinaryKeyException(keyDefs, subst("Key fragment %s value %s is outside defined scope [%s-%s]", keyDef.getName(), value, keyDef.getFloor(), keyDef.getCeil()));
		}
		return value;
	}
	
	private byte[] decodeToBytes(String code) {
		Base32 base32 = new Base32();
		int encodedLength = Long.valueOf(base32.getEncodedLength(keyDefs.stream()
				.map(KeyDef::getDecimalSpace)
				.map(space -> space.toString())
				.collect(Collectors.joining())
				.getBytes(StandardCharsets.US_ASCII)))
				.intValue();
		String paddedCode = StringUtils.rightPad(code, encodedLength, '=');
		return base32.decode(paddedCode);
	}
	
	public String encode(String compositeKey) {
		List<Integer> keys = new ArrayList<>();
		Integer start = 0;
		for(int i=0; i<keyDefs.size(); i++) {
			KeyDef def = keyDefs.get(i);
			Integer end = start + def.getDecimalLength();
			String keyStr = compositeKey.substring(start, end);
			start = end;
			keys.add(Integer.parseInt(keyStr));
		}
		return encode(keys);
	}

	public String encode(List<Integer> keys) {
		StringBuilder binaryString = new StringBuilder();
		for(int i=0; i<keyDefs.size(); i++) {
			KeyDef def = keyDefs.get(i);
			Integer key = keys.get(i) - def.getFloor();
			binaryString.append(leftPad(Integer.toBinaryString(key), def.getBinaryLength(), '0'));
		}
		byte[] b = new BigInteger(binaryString.toString(), 2).toByteArray();
		return encode(b);
	}
	
	protected String encode(byte[] b) {
		return new Base32().encodeAsString(b).replaceAll("=*$", "");
	}

	public static class KeyDef {
		private final String name;
		private final Integer ceil;
		private final Integer floor;
		private final Integer binaryLength;
		private final Integer decimalLength;
		
		public KeyDef(String name, Integer floor, Integer ceil) {
			this.name = name;
			this.floor = floor;
			this.ceil = ceil;
			decimalLength = getDecimalSpace().toString().length();
			binaryLength = Integer.toBinaryString(getDecimalSpace()).length();
		}
		
		public Integer getDecimalSpace() {
			return ceil - floor;
		}

		public String getName() {
			return name;
		}

		public Integer getFloor() {
			return floor;
		}

		public Integer getCeil() {
			return ceil;
		}

		public Integer getBinaryLength() {
			return binaryLength;
		}

		public Integer getDecimalLength() {
			return decimalLength;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == null || !(obj instanceof KeyDef)) {
				return false;
			}
			return new EqualsBuilder().append(name, ((KeyDef) obj).getName()).isEquals();
		}
	}
	
	public String format(String code, String format) {
		Map<String, Integer> fieldLength = keyDefs.stream()
				.collect(Collectors.toMap(KeyDef::getName, KeyDef::getDecimalLength));
		Map<String, String> values = decodeToMap(code).entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> leftPad(entry.getValue().toString(), fieldLength.get(entry.getKey()), '0')));
		return format(values, format);
	}

	private String format(Map<String, String> values, String format) {
		return StringSubstitutor.replace(format, values);
	}
}
