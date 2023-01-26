package dk.langli.bahco.validation;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public abstract class BaseValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {
	private A annotation;
	
	@Override
	public void initialize(A constraintAnnotation) {
		annotation = constraintAnnotation;
	}

	public A getAnnotation() {
		return annotation;
	}

	protected boolean violation(ConstraintValidatorContext context, String label) {
		if(context != null) {
			context.disableDefaultConstraintViolation();
			String messageTemplate = String.format("{%s.%s}", getClass().getName(), label);
			context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation();
		}
		return false;
	}
}
