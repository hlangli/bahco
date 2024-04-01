package dk.langli.bahco;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public class Json {
	private static JsonbConfig DEFAULT_CONFIG = defaultJsonbConfig();
	private static ThreadLocal<Map<JsonbConfig, Jsonb>> objectMapper = ThreadLocal.withInitial(HashMap::new);

	private static JsonbConfig defaultJsonbConfig() {
		return new JsonbConfig();
	}

	private static Jsonb mapper() {
	    return mapper(DEFAULT_CONFIG);
	}
	
	private static Jsonb mapper(JsonbConfig config) {
		return objectMapper.get().computeIfAbsent(config, c -> JsonbBuilder.create(config));
	}
	
	public static String stringify(Object o) {
		return mapper().toJson(o);
	}
	
	public static String stringify(Object o, JsonbConfig config) {
		return mapper(config).toJson(o);
	}
	
	public static void toJson(Object o, Writer w) {
		mapper().toJson(o, w);
	}
	
	public static void toJson(Object o, JsonbConfig config, Writer w) {
		mapper(config).toJson(o, w);
	}
	
	public static JsonbConfig pretty() {
		return defaultJsonbConfig().withFormatting(true);
	}
	
	public static <T> T parse(String json, Class<T> type) {
		return parse(stream(json), type);
	}

	public static <T> T parse(InputStream json, Class<T> type) {
		return mapper().fromJson(json, type);
	}

	public static <T extends Map<K, V>, K, V> T parse(String json, Class<T> mapType, Class<K> keyType, Class<V> valueType) {
		return parse(stream(json), mapType, keyType, valueType);
	}

	public static <T extends Map<K, V>, K, V> T parse(InputStream json, Class<T> mapType, Class<K> keyType, Class<V> valueType) {
		return mapper().fromJson(json, mapType);
	}

	public static <T extends Collection<E>, E> T parse(String json, Class<T> collectionType, Class<E> elementType) {
		return parse(stream(json), collectionType, elementType);
	}

	public static <T extends Collection<E>, E> T parse(InputStream json, Class<T> collectionType, Class<E> elementType) {
		return mapper().fromJson(json, collectionType);
	}
	
	private static InputStream stream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}
}
