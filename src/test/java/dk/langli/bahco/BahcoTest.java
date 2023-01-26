package dk.langli.bahco;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dk.langli.bahco.Bahco;
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
	public void testToNvlMap() {
		Map<String, Integer> m = map(
				null,
				entry("one", 1),
				entry("two", 2),
				entry("three", 3),
				entry("four", 4),
				entry("four", 5),
				entry(null, null),
				entry(null, 0)
		).entrySet().stream()
				.collect(toNvlMap(Entry::getKey, Entry::getValue));
		assertEquals(5, m.size());
		assertEquals(0, m.get(null));
		assertEquals(5, m.get("four"));
	}
}
