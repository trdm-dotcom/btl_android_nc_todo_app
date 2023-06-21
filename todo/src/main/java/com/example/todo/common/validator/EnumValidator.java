package com.example.todo.common.validator;


import com.example.todo.common.exception.ErrorCodeEnums;

public class EnumValidator<T extends Enum<T>> extends Validator<String> {
    private final Class<T> enumClass;
    private boolean valid = false;

    public EnumValidator(String fieldName, String fieldValue, Class<T> enumClass) {
        super(fieldName, fieldValue);
        this.enumClass = enumClass;
    }

    public EnumValidator<T> validate() {
        this.valid = true;
        return this;
    }

    @Override
    protected String doCheck() {
        if (valid && !isValidEnum(enumClass, fieldValue)) {
            return this.addError(ErrorCodeEnums.INVALID_VALUE.name(), this.fieldName);
        }
        return null;
    }


    private static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
        if (enumName == null) {
            return false;
        } else {
            try {
                Enum.valueOf(enumClass, enumName);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
