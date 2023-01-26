package dk.langli.bahco.validation;

import java.util.Set;

public interface IDynamicGroupsValidation {
	public Set<Class<?>> determineValidationGroups();
}
