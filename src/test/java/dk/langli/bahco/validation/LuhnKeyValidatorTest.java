package dk.langli.bahco.validation;

import static dk.langli.bahco.Bahco.*;

import java.util.List;

import javax.validation.Validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dk.langli.bahco.validation.annotation.LuhnKey;
import dk.langli.bahco.validation.annotation.LuhnKeyValidator;
import lombok.Builder;

public class LuhnKeyValidatorTest {
	private static List<String> creditCards = list(null, "374245455400126", "378282246310005", "6250941006528599",
			"60115564485789458", "6011000991300009", "3566000020000410", "3530111333300000", "5425233430109903",
			"5425233430109903", "2222420000001113", "2223000048410010", "4263982640269299", "4263982640269299",
			"4263982640269299", "4917484589897107", "4001919257537193", "4007702835532454", "6362970000457013",
			"6062826786276634", "5011054488597827", "6271701225979642", "6034932528973614", "5895626746595650",
			"5200533989557118", "6034883265619896");
	private static List<String> invalid = list("6034883265619895");

	@Test
	public void testLuhnKeyValidation() {
		Validator validator = BahcoValidation.getValidator();
		Assertions.assertNull(new LuhnKeyValidator().getAnnotation());
		Assertions.assertNotNull(validator);
		creditCards.forEach(key -> {
			Assertions.assertEquals(0, validator.validate(Subject.builder().creditCardNumber(key).build()).size());
		});
		invalid.forEach(key -> {
			Assertions.assertNotEquals(0, validator.validate(Subject.builder().creditCardNumber(key).build()).size());
		});
	}

	@Builder
	public static class Subject {
		@LuhnKey
		private String creditCardNumber;
	}
}
