package dk.langli.bahco.validation;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

@SuppressWarnings("serial")
public class ValidationException extends Exception {
	private final List<SerializableViolation> violations;

	public ValidationException(Set<ConstraintViolation<?>> violations, Class<?> rootBeanType) {
		super(String.format("Validation of %s failed", rootBeanType.getSimpleName()));
		this.violations = SerializableViolation.from(violations);
	}

	public ValidationException(Set<ConstraintViolation<?>> violations, String message, Throwable cause) {
		super(message, cause);
		this.violations = SerializableViolation.from(violations);
	}

	public ValidationException(Set<ConstraintViolation<?>> violations, String message) {
		super(message);
		this.violations = SerializableViolation.from(violations);
	}

	public ValidationException(Set<ConstraintViolation<?>> violations, Throwable cause) {
		super(cause);
		this.violations = SerializableViolation.from(violations);
	}

	public List<SerializableViolation> getViolations() {
		return violations;
	}
}
