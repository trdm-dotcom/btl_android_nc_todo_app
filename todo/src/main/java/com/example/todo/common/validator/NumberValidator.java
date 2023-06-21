package com.example.todo.common.validator;

import com.example.todo.common.exception.ErrorCodeEnums;
import lombok.Data;

import java.util.Arrays;
@Data
public class NumberValidator<T extends Number> extends Validator<T> {
    private T min;
    private T max;
    private T extract;
    private T[] in;
    private boolean eq;
    private boolean notNull = true;

    public NumberValidator(String fieldName, T fieldValue) {
        super(fieldName, fieldValue);
    }

    public NumberValidator<T> eq() {
        this.eq = true;
        return this;
    }

    public NumberValidator<T> notEmpty() {
        this.notNull = false;
        return this;
    }

    public NumberValidator<T> in(T... items) {
        in = items;
        return this;
    }

    public NumberValidator<T> min(T min) {
        this.min = min;
        return this;
    }

    public NumberValidator<T> max(T max) {
        this.max = max;
        return this;
    }

    public NumberValidator<T> extract(T extract) {
        this.extract = extract;
        return this;
    }

    private boolean isEmpty() {
        return this.fieldValue == null;
    }

    @Override
    protected T doCheck() {
        if (this.notNull && this.isEmpty()) {
            return this.addError(ErrorCodeEnums.EMPTY_VALUE.name(), this.fieldName);
        }
        if (!this.isEmpty() && this.extract != null && !this.fieldValue.equals(this.extract)) {
            return this.addError(ErrorCodeEnums.VALUE_MUST_EQUAL.name()
                    , this.fieldName, this.fieldName, String.valueOf(this.extract));
        }
        if (!this.isEmpty() && this.in != null) {
            boolean found = false;
            for (Number item : in) {
                if (item.equals(this.fieldValue)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return this.addError(ErrorCodeEnums.VALUE_MUST_IN.name()
                        , this.fieldName, this.fieldName, Arrays.toString(this.in));
            }
        }
        if (!this.isEmpty() && this.min != null) {
            if (this.eq && this.min.doubleValue() > this.fieldValue.doubleValue()) {
                return this.addError(ErrorCodeEnums.VALUE_MUST_LESS.name()
                        , this.fieldName, this.fieldName, String.valueOf(this.min));
            }
            if (!this.eq && this.fieldValue.doubleValue() <= this.min.doubleValue()) {
                return this.addError(ErrorCodeEnums.VALUE_MUST_LESS_OR_EQUAL.name()
                        , this.fieldName, this.fieldName, String.valueOf(this.min));
            }
        }
        if (!this.isEmpty() && this.max != null) {
            if (this.eq && this.fieldValue.doubleValue() > this.max.doubleValue()) {
                return this.addError(ErrorCodeEnums.VALUE_MUST_GREATER.name()
                        , this.fieldName, this.fieldName, String.valueOf(this.min));
            }
            if (!this.eq && this.fieldValue.doubleValue() >= this.max.doubleValue()) {
                return this.addError(ErrorCodeEnums.VALUE_MUST_GREATER_OR_EQUAL.name()
                        , this.fieldName, this.fieldName, String.valueOf(this.min));
            }
        }
        return null;
    }
}
