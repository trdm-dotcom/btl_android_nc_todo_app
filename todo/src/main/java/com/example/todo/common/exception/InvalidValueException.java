package com.example.todo.common.exception;


import java.util.Collections;

public class InvalidValueException extends InvalidParameterException {
    public InvalidValueException(String fieldName) {
        super();
        this.add(new FieldError(
                ErrorCodeEnums.INVALID_VALUE.name(),
                fieldName,
                Collections.singletonList(fieldName)
        ));
    }
}
