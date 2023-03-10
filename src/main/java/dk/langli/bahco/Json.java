package dk.langli.bahco;

import static dk.langli.bahco.Bahco.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Json {
	private static ThreadLocal<ObjectMapper> objectMapper = ThreadLocal.withInitial(() -> createObjectMapper());

	private static ObjectMapper createObjectMapper() {
		ObjectMapper m = new ObjectMapper();
	    m = m.registerModule(new JavaTimeModule());
	    return m;
	}
	
	public static ObjectMapper mapper() {
		return objectMapper.get();
	}
	
	public static String stringify(Object o) {
		return wrap(() -> mapper().writeValueAsString(o));
	}
	
	public static String stringify(Object o, PrettyPrinter pp) {
		return wrap(() -> mapper()
				.writer(pp)
				.writeValueAsString(o));
	}
	
	public static PrettyPrinter pretty() {
		Indenter tabIndenter = new DefaultIndenter("	", DefaultIndenter.SYS_LF);
		return new DefaultPrettyPrinter()
				.withArrayIndenter(tabIndenter)
				.withObjectIndenter(tabIndenter);
	}
	
	public static <T> T parse(String json, Class<T> type) {
		return parse(stream(json), type);
	}

	public static <T> T parse(InputStream json, Class<T> type) {
		return wrap(() -> mapper().readValue(json, type));
	}

	public static <T extends Map<K, V>, K, V> T parse(String json, Class<T> mapType, Class<K> keyType, Class<V> valueType) {
		return parse(stream(json), mapType, keyType, valueType);
	}

	public static <T extends Map<K, V>, K, V> T parse(InputStream json, Class<T> mapType, Class<K> keyType, Class<V> valueType) {
		JavaType t = TypeFactory.defaultInstance().constructMapLikeType(mapType, keyType, valueType);
		return wrap(() -> mapper().readValue(json, t));
	}

	public static <T extends Collection<E>, E> T parse(String json, Class<T> collectionType, Class<E> elementType) {
		return parse(stream(json), collectionType, elementType);
	}

	public static <T extends Collection<E>, E> T parse(InputStream json, Class<T> collectionType, Class<E> elementType) {
		JavaType t = TypeFactory.defaultInstance().constructCollectionLikeType(collectionType, elementType);
		return wrap(() -> mapper().readValue(json, t));
	}
	
	private static InputStream stream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}
}
