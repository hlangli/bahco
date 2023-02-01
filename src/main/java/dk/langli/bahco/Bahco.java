package dk.langli.bahco;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.langli.bahco.function.ThrowableRunnable;
import dk.langli.bahco.function.ThrowableSupplier;
import dk.langli.bahco.function.WrappedException;

public class Bahco {
	public static final Charset PLATFORM_ENCODING = Charset.defaultCharset();
	private static final Logger log = LoggerFactory.getLogger(Bahco.class);

	// -- FAIL FAST -----------------------------------------------------------

	@SuppressWarnings("unchecked")
	public static <R, E extends Exception> R unwrap(Class<E> exceptionType, Supplier<R> f) throws E {
		try {
			return f.get();
		}
		catch(WrappedException e) {
			throw (E) e.getCause();
		}
	}

	public static <E extends Exception> void unwrap(Class<E> exceptionType, Runnable f) throws E {
		unwrap(exceptionType, () -> {
			f.run();
			return null;
		});
	}

	public static <R> R wrap(ThrowableSupplier<R, Exception> f) {
		try {
			return f.get();
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e) {
			throw new WrappedException(e);
		}
	}

	public static void wrap(ThrowableRunnable<Exception> f) {
		wrap(() -> {
			f.run();
			return null;
		});
	}

	public static <E extends Exception> void swallow(ThrowableRunnable<E> f) {
		swallow(() -> {
			f.run();
			return null;
		}, false, null);
	}

	public static <E extends Exception> void swallowSilent(ThrowableRunnable<E> f) {
		swallow(() -> {
			f.run();
			return null;
		}, false, null);
	}

	public static <T, E extends Exception> T swallow(ThrowableSupplier<T, E> f) {
		return swallow(f, false, null);
	}

	public static <T, E extends Exception> T swallowSilent(ThrowableSupplier<T, E> f) {
		return swallow(f, true, null);
	}

	public static <T, E extends Exception> T swallow(ThrowableSupplier<T, E> f, T nvl) {
		return swallow(f, false, nvl);
	}

	public static <T, E extends Exception> T swallowSilent(ThrowableSupplier<T, E> f, T nvl) {
		return swallow(f, true, nvl);
	}

	private static <T, E extends Exception> T swallow(ThrowableSupplier<T, E> f, boolean suppressWarning, T nvl) {
		T t = null;
		try {
			t = f.get();
		}
		catch(Exception e) {
			if(!suppressWarning) {
				List<StackTraceElement> stackTrace = list(e.getStackTrace());
				StackTraceElement traceElement = first(stackTrace);
				String traceline = traceElement != null ? traceElement.toString() : null;
				log.warn(s("Swallowing %s[%s]", e.getClass().getSimpleName(), traceline));
			}
			t = nvl;
		}
		return t;
	}

	// -- COLLECTIONS ---------------------------------------------------------

	@SafeVarargs
	public static <E> List<E> list(E... es) {
		return new ArrayList<>(Arrays.asList(es));
	}

	@SafeVarargs
	public static <E> Set<E> set(E... es) {
		return new HashSet<>(list(es));
	}

	@SafeVarargs
	public static <E> SortedSet<E> sortedSet(E... es) {
		return new TreeSet<>(list(es));
	}

	@SafeVarargs
	public static <E> SortedSet<E> sortedSet(Comparator<E> comp, E... es) {
		TreeSet<E> set = new TreeSet<>(comp);
		set.addAll(list(es));
		return set;
	}

	public static <E> E first(Collection<E> l) {
		return l != null ? l.stream().findFirst().orElse(null) : null;
	}
	
	// -- STRINGS -------------------------------------------------------------

	public static String s(String format, Object... args) {
		return subst(format, args);
	}

	public static String subst(String format, Object... args) {
		return String.format(format, args);
	}

	public static String getResourceAsString(String classpath) {
		try {
			return IOUtils.toString(Bahco.class.getClassLoader().getResourceAsStream(classpath), PLATFORM_ENCODING);
		}
		catch(Exception e) {
			return null;
		}
	}

	// -- MAPS ----------------------------------------------------------------

	public static <K, V extends Object> Map<K, V> map() {
		return new HashMap<>();
	}

	public static <K, V extends Object> Map<K, V> entry(K key, V value) {
		Map<K, V> map = map();
		map.put(key, value);
		return map;
	}

	@SafeVarargs
	public static <K, V> Map<K, V> map(Map<K, V>... maps) {
		return map(list(maps));
	}

	public static <K, V> Map<K, V> map(List<Map<K, V>> maps) {
		return maps.stream()
				.filter(map -> map != null)
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.collect(toNvlMap(Entry::getKey, Entry::getValue, (a, b) -> b));
	}

	public static <T, K, U> Collector<T, ?, Map<K, U>> toNvlMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
		return toNvlMap(keyMapper, valueMapper, (a, b) -> b);
	}

	public static <T, K, U> Collector<T, ?, Map<K, U>> toNvlMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {
		return Collectors.collectingAndThen(Collectors.toList(), list -> {
			Map<K, U> result = new HashMap<>();
			for(T item : list) {
				K key = keyMapper.apply(item);
				U newValue = valueMapper.apply(item);
				U value = result.containsKey(key) ? mergeFunction.apply(result.get(key), newValue) : newValue;
				result.put(key, value);
			}
			return result;
		});
	}

	public static Map<String, Object> flatten(Map<String, Object> h) {
		return flatten(null, h);
	}

	public static Map<String, Object> flatten(List<?> h) {
		return flatten(null, h);
	}

	private static Map<String, Object> flatten(String key, Object o) {
		Map<String, Object> map = new LinkedHashMap<>();
		if (o instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> m = (Map<String, Object>) o;
			for (String k : m.keySet()) {
				String fqk = key != null ? subst("%s.%s", key, k) : k;
				map.putAll(flatten(fqk, m.get(k)));
			}
		} else if (o instanceof Collection) {
			@SuppressWarnings("unchecked")
			List<Object> c = (List<Object>) o;
			for (int i = 0; i < c.size(); i++) {
				String fqk = key != null ? subst("%s[%s]", key, i) : subst("[%s]", i);
				map.putAll(flatten(fqk, c.get(i)));
			}
		} else {
			map.put(key != null ? key : "null", o);
		}
		return map;
	}

	// -- NUMBERS -------------------------------------------------------------

  public static BigDecimal bd(String doubleValue) {
		return normalize(new BigDecimal(doubleValue));
	}

	public static BigDecimal bd(Double doubleValue) {
		return bd(doubleValue.toString());
	}

	/**
	 * Normalizes BigDecimal to have equals working more easily
	 * (Strips useless trailing 0 but force the scale to be at least 0 to avoid scientific display such as "1E+2")
	 *
	 * @param n
	 * @return
	 */
	public static BigDecimal normalize(BigDecimal n) {
		Objects.requireNonNull(n);
		// remove useless 0
		BigDecimal normalizedBigDecimal = n.stripTrailingZeros();
		// but force the scale to be at least 0 to avoid scientific display such as
		// "1E+2"
		normalizedBigDecimal = normalizedBigDecimal.scale() < 0 ? normalizedBigDecimal.setScale(0) : normalizedBigDecimal;
		return normalizedBigDecimal;
	}
}
