package dk.langli.bahco.validation;

import static dk.langli.bahco.Bahco.*;

import java.lang.reflect.Constructor;

import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.Builder;
import lombok.Getter;

public class BarryValidatorTest {
	@Test
	public void testBarryValidator() throws NoSuchMethodException, SecurityException {
		Assertions.assertNotNull(new BahcoValidation());
		Validator validator = BahcoValidation.getValidator();
		Assertions.assertNotNull(validator);
		BahcoValidator v = (BahcoValidator) validator;
		Assertions.assertEquals(BahcoValidator.class, validator.getClass());
		Constructor<Subject> c = Subject.class.getConstructor(String.class);
		Assertions.assertNotNull(c);
		Assertions.assertDoesNotThrow(() -> v.validateConstructorParameters(c, new Object[] { " " }));
		Assertions.assertDoesNotThrow(() -> v.validateConstructorReturnValue(c, new Subject("a")));
		Assertions.assertEquals(1, v.getConstraintsForClass(Subject.class).getConstrainedConstructors().size());
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> v.unwrap(Subject.class));
		Assertions.assertEquals(0, v.validateProperty(map(entry("subject", new Subject(null))), "subject.value").size());
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> v.validateValue(Subject.class, "value", new Subject(null)).size());
	}

	@Builder
	@Getter
	public static class Subject {
		@NotBlank
		private String value;
		
		public Subject(@NotEmpty String value) {
			this.value = value;
		}
	}
}
