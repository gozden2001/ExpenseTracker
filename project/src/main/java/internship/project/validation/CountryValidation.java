package internship.project.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryValidator.class)
public @interface CountryValidation {
    String message() default "Please provide a valid country";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
