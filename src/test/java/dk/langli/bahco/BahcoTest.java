package dk.langli.bahco;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import dk.langli.bahco.function.ThrowableRunnable;
import dk.langli.bahco.function.WrappedException;

public class BahcoTest {
	private static final String SWEDISH_KEY = "Swedish key";
	private static final ThrowableRunnable<Exception> NOOP = () -> {
		Integer i = 0; i += 1;
	};
	
	@Test
	public void testInstantiation() {
		assertDoesNotThrow(Bahco::new);
	}

	@Test
	public void testWrap() {
		try {
			assertDoesNotThrow(() -> wrap(NOOP));
			wrap(() -> {
				throw new Exception(SWEDISH_KEY);
			});
		}
		catch(RuntimeException e) {
			assertEquals(WrappedException.class, e.getClass());
		}
		try {
			wrap(() -> {
				throw new IllegalStateException(SWEDISH_KEY);
			});
		}
		catch(RuntimeException e) {
			assertEquals(IllegalStateException.class, e.getClass());
		}
	}

	@Test
	public void testUnwrap() {
		assertDoesNotThrow(() -> {
			unwrap(Exception.class, () -> {
				wrap(NOOP);
			});
		});
		
		try {
			unwrap(Exception.class, () -> {
				wrap(() -> {
					throw new Exception(SWEDISH_KEY);
				});
			});
		}
		catch(Exception e) {
			assertEquals(Exception.class, e.getClass());
		}
	}
	
	@Test
	public void testSwallow() {
		assertDoesNotThrow(() -> swallow(NOOP));
		assertDoesNotThrow(() -> swallow(() -> {
			throw new Exception(SWEDISH_KEY);
		}));
		assertDoesNotThrow(() -> swallowSilent(NOOP));
		assertDoesNotThrow(() -> swallowSilent(() -> {
			throw new Exception(SWEDISH_KEY);
		}));

		assertEquals(1, swallow(() -> 1, 0));
		assertEquals(0, swallow(() -> {
			throw new Exception(SWEDISH_KEY);
		}, 0));
		assertEquals(1, swallowSilent(() -> 1, 0));
		assertEquals(0, swallowSilent(() -> {
			throw new Exception(SWEDISH_KEY);
		}, 0));
		assertDoesNotThrow(() ->swallow(() -> {
			throw new NoTraceException();
		}));
	}
	
	@SuppressWarnings("serial")
	private static class NoTraceException extends Exception {
		@Override
		public StackTraceElement[] getStackTrace() {
			return new StackTraceElement[0];
		}
	}

	@Test
	public void testGetResourceAsString() {
		assertEquals(SWEDISH_KEY, getResourceAsString("resource.txt").trim());
		assertEquals("SWEDISH_KEY", getResourceAsString("dir/resource.txt").trim());
		assertNull(getResourceAsString("dir/noResource.txt"));
	}

	@Test
	public void testFirst() {
		assertEquals(1, first(list(1, 2, 3)));
		assertNull(first((Collection<Integer>) null));
	}

	@Test
	public void testList() {
		assertEquals(Arrays.asList("1", "2", "3"), list("1", "2", "3"));
	}

	@Test
	public void testSet() {
		assertTrue(set("1", "2", "3").containsAll(list("1", "2", "3")));
	}

	@Test
	public void testSortedSet() {
		assertEquals(list("1", "2", "5"), sortedSet("1", "5", "2").stream().collect(Collectors.toList()));
		assertEquals(list("5", "2", "1"), sortedSet((a, b) -> b.compareTo(a), "1", "5", "2").stream().collect(Collectors.toList()));
	}

	@Test
	public void testMap() {
		Map<String, Integer> m = map(
				null,
				entry("one", 1),
				entry("two", 2),
				entry("three", 3),
				entry("four", 4),
				entry("four", 5),
				entry(null, null),
				entry("five", null),
				entry(null, 0)
		);
		assertEquals(6, m.size());
		assertEquals(0, m.get(null));
		assertEquals(5, m.get("four"));
	}
	
	@Test
	public void testToNvlMap() {
		BiFunction<String, Integer, Pair<String, Integer>> pair = Pair::of;
		Map<String, Integer> m = list(
				pair.apply("one", 1),
				pair.apply("two", 2),
				pair.apply("three", 3),
				pair.apply("four", 4),
				pair.apply("four", 5),
				pair.apply(null, null),
				pair.apply("five", null),
				pair.apply(null, 0)
		).stream()
				.collect(toNvlMap(Pair::getKey, Pair::getValue));
		assertEquals(6, m.size());
		assertEquals(0, m.get(null));
		assertEquals(5, m.get("four"));
	}
	
	@Test
	public void testNvl() {
		assertEquals(1, nvl(1, 2));
		assertEquals(2, nvl(null, 2));
		AtomicInteger i = new AtomicInteger(1);
		assertEquals(1, nvl(i.get(), i.getAndSet(2)));
		assertEquals(2, i.get());
		assertEquals(2, nvl(i.get(), () -> i.getAndSet(1)));
		assertEquals(2, nvl(null, () -> i.getAndSet(3)));
		assertEquals(3, i.get());
	}
	
	@Test
	public void testFlatten() {
		//Given
		List<Integer> list = list(1, 2, 3);
		Map<String, Object> map = map(
				entry("energy", map(
						entry("barry", map(
								entry("BarryUtilTest", map(
										entry("a", list)
								))
						))
				)),
				entry(null, 4),
				entry("subjects", list(
						Subject.builder()
						.name("x")
						.children(
								list(
										Subject.builder().name("y").build()
								)
						)
						.relatives(
								map(
										entry("z", Subject.builder()
												.name("æ")
												.children(
														list(
																Subject.builder().name("ø").build()
														)
												)
												.build()
										)
								)
						)
						.build()
				))
		);
		
		//When
		Map<String, Object> flattenedMap = flatten(simplify(map, "dk.langli.bahco"));
		Map<String, Object> flattenedList = flatten(simplify(list, "dk.langli.bahco"));
		
		//Then
		assertEquals(1, (int) flattenedList.get("[0]"));
		assertEquals(2, (int) flattenedList.get("[1]"));
		assertEquals(3, (int) flattenedList.get("[2]"));
		assertEquals(1, (int) flattenedMap.get("energy.barry.BarryUtilTest.a[0]"));
		assertEquals(2, (int) flattenedMap.get("energy.barry.BarryUtilTest.a[1]"));
		assertEquals(3, (int) flattenedMap.get("energy.barry.BarryUtilTest.a[2]"));
		assertEquals(4, (int) flattenedMap.get("null"));
	}
	
	@Test
	public void testBd() {
		assertEquals(1.5, bd(1.5).doubleValue());
		assertEquals(1.5, bd("1.5").doubleValue());
		assertEquals(1.5, normalize(bd("1.50")).doubleValue());
		assertThrows(NullPointerException.class, () -> normalize(null));
		assertEquals("1E-20", bd("1E-20").toString());
		assertEquals("1E-20", normalize(bd("1E-20")).toString());
		assertEquals("60000", normalize(BigDecimal.valueOf(600, -2)).toString());
	}
	
	@Test
	public void testSubst() {
		assertEquals(s("abc${var}ghi", entry("var", "def")), "abcdefghi");
	}
}
