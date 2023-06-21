package com.example.todo.common.validator;

import com.example.todo.common.exception.ErrorCodeEnums;

import java.util.Collection;

public class ListValidator<T> extends Validator<Collection<T>> {
    public ListValidator(String fieldName, Collection<T> fieldValue) {
        super(fieldName, fieldValue);
    }

    private boolean empty = false;

    public ListValidator<T> empty() {
        this.empty = true;
        return this;
    }

    @Override
    protected Collection<T> doCheck() {
        Collection<T> result = null;
        if (this.empty) {
            if (this.fieldValue == null || this.fieldValue.isEmpty()) {
                return this.addError(ErrorCodeEnums.EMPTY_VALUE.name(), this.fieldName);
            }
        }
        return result;
    }
}
