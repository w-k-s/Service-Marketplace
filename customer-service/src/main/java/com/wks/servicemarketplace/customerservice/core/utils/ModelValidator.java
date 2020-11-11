package com.wks.servicemarketplace.customerservice.core.utils;

import com.wks.servicemarketplace.customerservice.core.exceptions.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelValidator {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static <T> T validate(T instance, Class<?>... clazz) {
        final Set<ConstraintViolation<T>> violations = validator.validate(instance, clazz);
        if (!violations.isEmpty()) {
            final Map<String, String> fields = violations.stream()
                    .collect(Collectors.toMap(it -> it.getPropertyPath().toString(), ConstraintViolation::getMessage));
            throw new ValidationException(fields);
        }
        return instance;
    }
}
