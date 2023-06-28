package com.example.todo.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Status {
    private String code;
    private List<String> messageParams;
    private List<Error> params = new ArrayList<>();

    public Status() {
    }

    public Status(String code, List<String> messageParams) {
        this.code = code;
        this.messageParams = messageParams;
    }

    public Status add(Error error) {
        this.params.add(error);
        return this;
    }

    public GeneralException create() {
        if (this.params == null || this.params.isEmpty()) {
            return new GeneralException(
                    this.code,
                    messageParams
            );
        }
        SubErrorsException err = new SubErrorsException(this.code, this.messageParams);
        err.getErrors().addAll(
                this.params.stream().map(error
                        -> new Error(error.getCode(), error.getParam(), error.getMessageParams())
                ).collect(Collectors.toList())
        );
        return err;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getMessageParams() {
        return messageParams;
    }

    public void setMessageParams(List<String> messageParams) {
        this.messageParams = messageParams;
    }

    public List<Error> getParams() {
        return params;
    }

    public void setParams(List<Error> params) {
        this.params = params;
    }
}
