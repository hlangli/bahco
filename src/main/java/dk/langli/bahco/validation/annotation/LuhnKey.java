package dk.langli.bahco.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LuhnKeyValidator.class)
@Documented
public @interface LuhnKey {
    public String message() default "{energy.barry.base.validation.LuhnKey.message}";
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
}
