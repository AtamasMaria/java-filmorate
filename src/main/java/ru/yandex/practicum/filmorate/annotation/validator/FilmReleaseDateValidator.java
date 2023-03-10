package ru.yandex.practicum.filmorate.annotation.validator;

import ru.yandex.practicum.filmorate.annotation.CorrectReleaseDay;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<CorrectReleaseDay, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate != null) {
            return localDate.isAfter(LocalDate.of(1895, 12, 28));
        }
        return true;
    }

    @Override
    public void initialize(CorrectReleaseDay constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
