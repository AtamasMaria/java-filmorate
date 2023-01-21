package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.annotation.validator.LoginValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;;
import javax.validation.Constraint;
import javax.validation.Payload;


@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)

public @interface CorrectLogin {
    String message() default "must not have space";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

