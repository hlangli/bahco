package dk.langli.bahco.validation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

public class SerializableViolation {
	private final String beanClass;
	private final String message;
	private final String propertyPath;
	private final Object invalidValue;

	public SerializableViolation(Class<?> beanClass, String message, String propertyPath, Object invalidValue) {
		this.beanClass = beanClass.getName();
		this.message = message;
		this.propertyPath = propertyPath;
		this.invalidValue = invalidValue;
	}

	public String getBeanClass() {
		return beanClass;
	}

	public String getMessage() {
		return message;
	}

	public String getPropertyPath() {
		return propertyPath;
	}

	public Object getInvalidValue() {
		return invalidValue;
	}

	public static SerializableViolation from(ConstraintViolation<? extends Object> v) {
		return new SerializableViolation(v.getRootBeanClass(), v.getMessage(), v.getPropertyPath().toString(), v.getInvalidValue());
	}

	public static List<SerializableViolation> from(Collection<ConstraintViolation<? extends Object>> violations) {
		return violations.stream()
				.map(v -> from(v))
				.collect(Collectors.toList());
	}
}
