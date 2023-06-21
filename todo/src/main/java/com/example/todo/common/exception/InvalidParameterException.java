package com.example.todo.common.exception;

import lombok.Data;

@Data
public class InvalidParameterException extends SubErrorsException {
    public InvalidParameterException() {
        super(ErrorCodeEnums.INVALID_PARAMETER.name());
    }
}
