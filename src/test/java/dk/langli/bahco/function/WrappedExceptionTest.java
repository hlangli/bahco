package dk.langli.bahco.function;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class WrappedExceptionTest {
	@Test
	public void testConstructors() {
		{
			WrappedException e = new WrappedException();
			assertNull(e.getMessage());
			assertNull(e.getCause());
		}
		{
			WrappedException e = new WrappedException("msg");
			assertEquals("msg", e.getMessage());
			assertNull(e.getCause());
		}
		{
			Exception c = new Exception();
			WrappedException e = new WrappedException(c);
			assertEquals(c.getClass().getName(), e.getMessage());
			assertEquals(c, e.getCause());
		}
		{
			Exception c = new Exception();
			WrappedException e = new WrappedException("msg", c);
			assertEquals("msg", e.getMessage());
			assertEquals(c, e.getCause());
		}
	}
}
