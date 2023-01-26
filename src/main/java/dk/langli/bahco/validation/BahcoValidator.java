package dk.langli.bahco.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import dk.langli.bahco.Bahco;

public class BahcoValidator implements Validator, ExecutableValidator {
	private Validator validatorImpl;

	public BahcoValidator(Validator validatorImpl) {
		this.validatorImpl = validatorImpl;
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
		return validatorImpl.validate(t, retrieveFullGroups(t, classes));
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes) {
		return validatorImpl.validateProperty(t, s, retrieveFullGroups(t, classes));
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateValue(Class<T> aClass, String s, Object o, Class<?>... classes) {
		return validatorImpl.validateValue(aClass, s, o, retrieveFullGroups(o, classes));
	}

	@Override
	public BeanDescriptor getConstraintsForClass(Class<?> aClass) {
		return validatorImpl.getConstraintsForClass(aClass);
	}

	@Override
	public <T> T unwrap(Class<T> aClass) {
		return validatorImpl.unwrap(aClass);
	}

	@Override
	public ExecutableValidator forExecutables() {
		return this;
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateParameters(T t, Method method, Object[] objects, Class<?>... classes) {
		return validatorImpl.forExecutables().validateParameters(t, method, objects, retrieveFullGroups(objects, classes));
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateReturnValue(T t, Method method, Object o, Class<?>... classes) {
		return validatorImpl.forExecutables().validateReturnValue(t, method, o, retrieveFullGroups(o, classes));
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateConstructorParameters(Constructor<? extends T> constructor, Object[] objects, Class<?>... classes) {
		return validatorImpl.forExecutables().validateConstructorParameters(constructor, objects, retrieveFullGroups(classes));
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateConstructorReturnValue(Constructor<? extends T> constructor, T t, Class<?>... classes) {
		return validatorImpl.forExecutables().validateConstructorReturnValue(constructor, t, retrieveFullGroups(t, classes));
	}

	private static Class<?>[] retrieveFullGroups(Object[] objects, Class<?>... groups) {
		Set<Class<?>> fullGroups = Bahco.set(groups);
		for(Object object : objects) {
			addDynamicGroups(object, fullGroups);
		}
		return fullGroups.toArray(new Class<?>[fullGroups.size()]);
	}

	private static Class<?>[] retrieveFullGroups(Object object, Class<?>... groups) {
		Set<Class<?>> fullGroups = addDynamicGroups(object, Bahco.set(groups));
		return fullGroups.toArray(new Class<?>[fullGroups.size()]);
	}

	private static Set<Class<?>> addDynamicGroups(Object object, Set<Class<?>> groups) {
		if(object instanceof IDynamicGroupsValidation) {
			groups.addAll(((IDynamicGroupsValidation) object).determineValidationGroups());
		}
		return groups;
	}
}
