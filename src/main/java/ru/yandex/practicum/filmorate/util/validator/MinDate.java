package ru.yandex.practicum.filmorate.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = MinDateValidator.class)
@Documented

public @interface MinDate {
    String message() default "Дата не может быть ранее 28.12.1895";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
