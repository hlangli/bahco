package dk.langli.bahco.validation;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotBlank;

import org.junit.jupiter.api.Test;

import lombok.Builder;
import lombok.Getter;

public class SerializableViolationTest {
	@Test
	public void testSeriablizableViolation() {
		SerializableViolation v = new SerializableViolation(SerializableViolationTest.class, "message", "fieldname", "value");
		assertEquals("message", v.getMessage());
		assertEquals("value", v.getInvalidValue());
		assertEquals(SerializableViolationTest.class.getName(), v.getBeanClass());
		assertEquals("fieldname", v.getPropertyPath());
		Set<ConstraintViolation<Subject>> violations = BahcoValidation.getValidator().validate(Subject.builder().value("").build());
		assertNotNull(SerializableViolation.from(violations.stream()
				.collect(Collectors.toList())));
		assertNotNull(SerializableViolation.from(first(violations)));
	}
	
	@Builder
	@Getter
	public static class Subject {
		@NotBlank
		private String value;
	}
}
