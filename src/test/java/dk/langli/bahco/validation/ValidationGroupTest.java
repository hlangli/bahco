package dk.langli.bahco.validation;

import static dk.langli.bahco.Bahco.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ValidationGroupTest {
	@Builder
	@AllArgsConstructor
	@Getter
	private static class Subject {
		@NotNull(groups = GroupA.class)
		private Integer a;
		@NotNull(groups = GroupB.class)
		private Integer b;
		@NotNull
		private Integer c;
		@NotNull(groups = {GroupA.class, GroupB.class})
		private Integer d;
	}
	
	private interface GroupA {
	}
	
	private interface GroupB {
	}

	@ParameterizedTest
	@MethodSource("testVectors")
	public void testValidationGroups(Subject s, List<Class<?>> groups, List<String> expectedViolatedFields) {
		Validator validator = BahcoValidation.getValidator();
		List<String> violatedFields = validator.validate(s, groups.toArray(new Class<?>[groups.size()])).stream()
				.map(ConstraintViolation::getPropertyPath)
				.map(Path::toString)
				.collect(Collectors.toList());
		Assertions.assertThat(violatedFields).asList().containsExactlyElementsOf(expectedViolatedFields);
	}
	
	private static Stream<Arguments> testVectors() {
		return list(
				list(Subject.builder().a(0).d(0).build(), list(GroupA.class), list()),
				list(Subject.builder().b(0).d(0).build(), list(GroupB.class), list()),
				list(Subject.builder().c(0).build(), list(), list()),
				list(Subject.builder().b(0).d(0).build(), list(GroupA.class), list("a")),
				list(Subject.builder().a(0).d(0).build(), list(GroupB.class), list("b")),
				list(Subject.builder().a(0).b(0).d(0).build(), list(GroupA.class, GroupB.class), list())
		).stream()
				.map(args -> args.toArray(new Object[args.size()]))
				.map(Arguments::of);
	}
}
