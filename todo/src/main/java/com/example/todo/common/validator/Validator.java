package com.example.todo.common.validator;

import com.example.todo.common.exception.FieldError;
import com.example.todo.common.exception.InvalidParameterException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Data
@NoArgsConstructor
public abstract class Validator<T> {
    protected Validator<T> checkIfPassed;
    protected InvalidParameterException exception;
    protected String fieldName;
    protected T fieldValue;
    protected boolean throwOnFail = true;
    protected Consumer<Object> consumer;

    public Validator<T> consume(Consumer<Object> consumer) {
        this.consumer = consumer;
        return this;
    }

    public Validator<T> notThrowOnFail() {
        this.throwOnFail = false;
        return this;
    }

    public Validator(String fieldName, T fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public boolean isError() {
        return this.exception != null && !this.exception.getErrors().isEmpty();
    }

    public void doThrow() {
        if (!this.isError()) {
            return;
        }
        throw this.exception;
    }

    public T addError(FieldError error) {
        this.exception.add(error);
        return null;
    }

    public T addError(String code, String param) {
        this.exception.add(new FieldError(code, param));
        return null;
    }

    public T addError(String code, String param, List<String> messageParams) {
        this.exception.add(new FieldError(code, param, messageParams));
        return null;
    }

    public T addError(String code, String param, String... messageParams) {
        this.exception.add(new FieldError(code, param, Arrays.asList(messageParams)));
        return null;
    }

    public void dependOn(Validator<T> other) {
        this.checkIfPassed = other;
    }

    public T check() {
        this.exception = new InvalidParameterException();
        if (this.checkIfPassed != null && this.checkIfPassed.check() == null) {
            return null;
        }

        T transformed = this.doCheck();
        if (this.throwOnFail) {
            this.doThrow();
        }
        if (!this.isError() && this.consumer != null && transformed != null) {
            this.consumer.accept(transformed);
        }
        return transformed;
    }

    protected abstract T doCheck();
}
