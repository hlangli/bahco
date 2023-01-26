package dk.langli.bahco.validation;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ConstraintViolation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationExceptionTest {
	@Test
	public void testTechnicalException() {
		Set<ConstraintViolation<?>> violations = toSet(list());
		Assertions.assertDoesNotThrow(() -> new ValidationException(violations, ValidationExceptionTest.class));
		ValidationException e = new ValidationException(violations, "TEST");
		assertEquals("TEST", e.getMessage());
		Throwable cause = new Throwable();
		e = new ValidationException(violations, "TEST", cause);
		assertEquals(cause, e.getCause());
		e = new ValidationException(violations, cause);
		assertEquals(cause, e.getCause());
		assertEquals(0, e.getViolations().size());
	}
	
	private static <T> Set<T> toSet(List<T> list) {
		return new TreeSet<>(list);
	}
}
