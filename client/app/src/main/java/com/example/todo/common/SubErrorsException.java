package com.example.todo.common;

import java.util.ArrayList;
import java.util.List;

public class SubErrorsException extends GeneralException {
    private List<Error> errors = new ArrayList<>();

    public SubErrorsException add(Error error) {
        this.errors.add(error);
        return this;
    }

    public SubErrorsException add(String code, String param, List<String> messageParams) {
        this.errors.add(new Error(code, param, messageParams));
        return this;
    }

    public SubErrorsException(String code) {
        super(code);
    }

    public SubErrorsException(String code, List<String> messageParams) {
        super(code, messageParams);
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
