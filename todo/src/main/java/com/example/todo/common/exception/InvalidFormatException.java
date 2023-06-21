package com.example.todo.common.exception;


import java.util.Collections;

public class InvalidFormatException extends InvalidParameterException {
    public InvalidFormatException(String fieldName) {
        super();
        this.add(new FieldError(
                ErrorCodeEnums.INVALID_FORMAT.name(),
                fieldName,
                Collections.singletonList(fieldName)
        ));
    }
}
