package dk.langli.bahco;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class JacksonTest {
	@Test
	public void testInstantiation() {
		assertNotNull(new Jackson());
	}

	@Test
	public void testMapper() {
		assertNotNull(Jackson.mapper());
	}

	@Test
	public void testStringify() {
		Map<String, Object> m = map(
				entry("key", "Swedish")
		);
		assertEquals("{\"key\":\"Swedish\"}", Jackson.stringify(m));
		assertEquals("{\n\t\"key\" : \"Swedish\"\n}", Jackson.stringify(m, Jackson.pretty()));
	}

	@Test
	public void testParse() {
		Map<String, Object> m = map(
				entry("key", "Swedish"),
				entry("swedish", bd(Math.PI))
		);
		@SuppressWarnings("unchecked")
		Map<String, Object> m2 = Jackson.parse(Json.stringify(m), HashMap.class, String.class, Object.class);
		assertEquals(m.size(), m2.size());
		m.forEach((k, v) -> m2.get(k).equals(v));
		
		List<Integer> l = list(1, 2, 3);
		@SuppressWarnings("unchecked")
		List<Integer> l2 = Jackson.parse(Jackson.stringify(l), List.class, Integer.class);
		assertEquals(l.size(), l2.size());
		assertTrue(l2.equals(l));
		
		LocalDateTime d = LocalDateTime.parse("2023-01-01T14:43:21");
		LocalDateTime d2 = Jackson.parse(Jackson.stringify(d), LocalDateTime.class);
		assertEquals(d, d2);
	}
}
