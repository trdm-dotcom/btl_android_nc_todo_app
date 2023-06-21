package com.example.todo.common.validator;

import com.example.todo.common.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CombineValidator<T> extends Validator<T> {
    private List<Pair<Validator<T>, Consumer<T>>> validators;

    public CombineValidator() {
        super(null, null);
        this.validators = new ArrayList<>();
    }

    public CombineValidator<T> add(Validator<T> validator) {
        return add(validator, null);
    }

    public CombineValidator<T> add(Validator<T> validator, Consumer<T> consumer) {
        validator.setException(this.exception);
        validator.notThrowOnFail();
        this.validators.add(new Pair<>(validator, consumer));
        return this;
    }

    @Override
    protected T doCheck() {
        this.validators.forEach(p -> {
            T result = p.getLeft().check();
            if (p.getRight() != null) {
                p.getRight().accept(result);
            }
            if (p.getLeft().isError()) {
                this.addError(p.getLeft().getException().getErrors().get(0));
            }
        });
        if (this.isError()) {
            doThrow();
        }
        return null;
    }
}
