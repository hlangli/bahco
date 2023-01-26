package dk.langli.bahco.validation;

import javax.validation.Validation;
import javax.validation.Validator;

public class BahcoValidation {
	private static ThreadLocal<Validator> VALIDATORS = ThreadLocal.withInitial(BahcoValidation::newValidator);
	
	private static Validator newValidator() {
		return new BahcoValidator(Validation.byDefaultProvider()
				.configure()
				.buildValidatorFactory()
				.getValidator());
	}
	
	public static Validator getValidator() {
		return VALIDATORS.get();
	}
}
