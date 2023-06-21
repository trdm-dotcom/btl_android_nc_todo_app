package com.example.todo.common.validator;

import com.example.todo.common.exception.ErrorCodeEnums;
import com.example.todo.constants.Constants;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
@Data
public class StringValidator extends Validator<String> {
    private boolean empty = false;
    private IValidator<String> format;

    public StringValidator(String fieldName, String fieldValue) {
        super(fieldName, fieldValue);
    }

    public StringValidator empty() {
        this.empty = true;
        return this;
    }

    public StringValidator format(IValidator<String> validate) {
        this.format = validate;
        return this;
    }

    private boolean passedEmpty() {
        return this.exception.getErrors().isEmpty() && !StringUtils.isEmpty(this.fieldValue);
    }

    protected String doCheck() {
        String result = null;
        if (this.empty && StringUtils.isEmpty(this.fieldValue)) {
            this.addError(ErrorCodeEnums.EMPTY_VALUE.name(), this.fieldName);
            return null;
        }
        if (this.format != null && this.passedEmpty()) {
            result = this.format.valid(this.fieldValue);
            if (result == null) {
                this.addError(ErrorCodeEnums.INVALID_FORMAT.name(), this.fieldName);
                return null;
            }
        }
        return result;
    }
}
