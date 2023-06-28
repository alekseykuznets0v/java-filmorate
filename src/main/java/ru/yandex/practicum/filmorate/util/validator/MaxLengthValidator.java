package ru.yandex.practicum.filmorate.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxLengthValidator implements ConstraintValidator<MaxLength, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isEmpty()) {
            return value.length() <= 200;
        }
        return true;
    }
}
