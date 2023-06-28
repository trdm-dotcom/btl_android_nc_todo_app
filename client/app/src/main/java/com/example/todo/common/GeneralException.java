package com.example.todo.common;

import java.util.Arrays;
import java.util.List;

public class GeneralException extends RuntimeException {
    protected String code = "INTERNAL_SERVER_ERROR";
    protected List<String> messageParams;
    protected Throwable source;

    public GeneralException() {
    }

    public GeneralException(String code, String... params) {
        this.code = code;
        this.messageParams = params == null ? null : Arrays.asList(params);
    }

    public GeneralException(String code, List<String> messageParams) {
        this.code = code;
        this.messageParams = messageParams;
    }

    @Override
    public String getMessage() {
        return this.code;
    }

    public GeneralException source(Throwable source) {
        this.source = source;
        return this;
    }

    @Override
    public synchronized Throwable getCause() {
        return this.source;
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

    public Throwable getSource() {
        return source;
    }

    public void setSource(Throwable source) {
        this.source = source;
    }
}
