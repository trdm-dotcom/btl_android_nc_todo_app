package com.example.todo.common.validator;

public interface IValidator<T> {
    T valid(T t);
}
