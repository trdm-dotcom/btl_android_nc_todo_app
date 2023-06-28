package com.example.todo.common;

import java.util.List;

public class Error {
    private String code;
    private String param;
    private List<String> messageParams;

    public Error() {
    }

    public Error(String code, String param, List<String> messageParams) {
        this.code = code;
        this.param = param;
        this.messageParams = messageParams;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public List<String> getMessageParams() {
        return messageParams;
    }

    public void setMessageParams(List<String> messageParams) {
        this.messageParams = messageParams;
    }
}
