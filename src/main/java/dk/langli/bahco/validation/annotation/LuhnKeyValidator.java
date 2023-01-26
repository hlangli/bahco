package dk.langli.bahco.validation.annotation;

import javax.validation.ConstraintValidatorContext;

import dk.langli.bahco.validation.BaseValidator;

public class LuhnKeyValidator extends BaseValidator<LuhnKey, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value != null) {
			String violationLabel = validate(value);
			if (violationLabel != null) {
				return violation(context, violationLabel);
			}
		}
		return true;
	}

	protected static String validate(String luhnKey) {
		int sum = 0;
		boolean alternate = false;
		for (int i = luhnKey.length() - 1; i >= 0; i--) {
			int n = Integer.parseInt(luhnKey.substring(i, i + 1));
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n = (n % 10) + 1;
				}
			}
			sum += n;
			alternate = !alternate;
		}
		if (sum % 10 == 0) {
			return null;
		} else {
			return "checksum";
		}
	}
}
