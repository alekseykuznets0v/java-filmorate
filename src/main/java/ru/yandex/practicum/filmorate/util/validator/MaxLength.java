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
@Constraint(validatedBy = MaxLengthValidator.class)
@Documented
public @interface MaxLength {
    String message() default "Превышено ограничение максимальной длины текста в 200 символов";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
