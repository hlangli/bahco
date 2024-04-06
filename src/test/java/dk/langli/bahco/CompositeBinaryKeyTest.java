package dk.langli.bahco;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dk.langli.bahco.CompositeBinaryKey.KeyDef;

public class CompositeBinaryKeyTest {
	@Test
	public void testCompositeKeyException() {
		{
			CompositeBinaryKeyException e = new CompositeBinaryKeyException(list(new KeyDef("a", 1, 10)));
			assertEquals(1, e.getKeyDefs().size());
		}
		{
			CompositeBinaryKeyException e = new CompositeBinaryKeyException(list(new KeyDef("a", 1, 10)), new Throwable("b"));
			assertEquals(1, e.getKeyDefs().size());
			assertEquals("b", e.getCause().getMessage());
		}
	}
	
	@Test
	public void testKeyDefEquals() {
		assertNotEquals(new KeyDef("a", 0, 1), null);
		assertNotEquals(new KeyDef("a", 0, 1), "b");
	}
	
	private static Stream<Arguments> testCpr() {
		return list(
				Arguments.of("28", "01", "75", "2279", "GYJMRZY", list(28, 1, 75, 2279), null),
				Arguments.of("27", "02", "11", "5569", "GQRNLQI", list(27, 2, 11, 5569), null),
				Arguments.of("08", "09", "12", "5149", "B4BRIHI", list(8, 9, 12, 5149), null),
				Arguments.of("32", "01", "00", "0001", "B4BRIHI", list(32, 1, 0, 1), new CompositeBinaryKeyException(null, "Key fragment day value 32 is outside defined scope \\[1-31\\]")),
				Arguments.of("28", "00", "00", "0001", "B4BRIHI", list(28, 1, 0, 1), new CompositeBinaryKeyException(null, "Key fragment month value 16 is outside defined scope \\[1-12\\]"))
		).stream();
	}
	
	@ParameterizedTest
	@MethodSource("testCpr")
	public void testCpr(String day, String month, String year, String control, String expectedCode, List<Integer> expectation, Throwable expectedException) {
		ArrayList<KeyDef> keyDefs = list(
				new CompositeBinaryKey.KeyDef("day", 1, 31),
				new CompositeBinaryKey.KeyDef("month", 1, 12),
				new CompositeBinaryKey.KeyDef("year", 0, 99),
				new CompositeBinaryKey.KeyDef("control", 0, 9999)

		);
		CompositeBinaryKey key = CompositeBinaryKey.builder()
				.keyDefs(keyDefs)
				.build();
		Map<String, String> values = map(
				entry("day", day),
				entry("month", month),
				entry("year", year),
				entry("control", control)
		);
		try {
			String str = key.encode(subst("${day}${month}${year}${control}", values));
			assertEquals(
					keyDefs.stream()
							.map(KeyDef::getName)
							.map(values::get)
							.collect(Collectors.joining()),
					key.decode(str)
			);
			assertEquals(expectedCode, str);
			String format = "${day};${month};${year};${control}";
			assertEquals(subst(format, values), key.format(str, format));
			Map<String, Integer> parsedKey = key.decodeToMap(str);
			assertEquals(4, parsedKey.size());
			assertEquals(expectation.get(0), parsedKey.get("day"));
			assertEquals(expectation.get(1), parsedKey.get("month"));
			assertEquals(expectation.get(2), parsedKey.get("year"));
			assertEquals(expectation.get(3), parsedKey.get("control"));
		}
		catch(Throwable e) {
			assertNotNull(expectedException);
			assertEquals(expectedException.getClass(), e.getClass());
			assertTrue(
					e.getMessage().matches(expectedException.getMessage()),
					subst("\"%s\" does not match regex \"%s\"", e.getMessage(), expectedException.getMessage())
			);
		}
	}
}
