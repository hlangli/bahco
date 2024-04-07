package dk.langli.bahco;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class JsonTest {
	@Test
	public void testInstantiation() {
		assertNotNull(new Json());
	}

	@Test
	public void testStringify() {
		Map<String, Object> m = map(
				entry("key", "Swedish")
		);
		assertEquals("{\"key\":\"Swedish\"}", Json.stringify(m));
		assertEquals("{\n    \"key\": \"Swedish\"\n}", Json.stringify(m, Json.pretty()));
	}

	@Test
	public void testToJson() {
		Map<String, Object> m = map(
				entry("key", "Swedish")
		);
		{
			StringWriter sw = new StringWriter();
			Json.toJson(m, sw);
			assertEquals("{\"key\":\"Swedish\"}", sw.toString());
		}
		{
			StringWriter sw = new StringWriter();
			Json.toJson(m, Json.pretty(), sw);
			assertEquals("{\n    \"key\": \"Swedish\"\n}", sw.toString());
		}
	}

	@Test
	public void testParse() {
		Map<String, Object> m = map(
				entry("key", "Swedish"),
				entry("swedish", bd(Math.PI))
		);
		@SuppressWarnings("unchecked")
		Map<String, Object> m2 = Json.parse(Json.stringify(m), HashMap.class, String.class, Object.class);
		assertEquals(m.size(), m2.size());
		m.forEach((k, v) -> m2.get(k).equals(v));
		
		List<BigDecimal> l = list(bd(1), bd(2), bd(3));
		@SuppressWarnings("unchecked")
		List<BigDecimal> l2 = Json.parse(Json.stringify(l), List.class, Integer.class);
		assertEquals(l.size(), l2.size());
		assertTrue(l2.equals(l));
		
		LocalDateTime d = LocalDateTime.parse("2023-01-01T14:43:21");
		LocalDateTime d2 = Json.parse(Json.stringify(d), LocalDateTime.class);
		assertEquals(d, d2);
	}
}
