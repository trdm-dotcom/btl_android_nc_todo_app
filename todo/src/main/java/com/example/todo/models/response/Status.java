package com.example.todo.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {
    private String code;
    private List<String> messageParams;
    private List<Error> params = new ArrayList<>();

    public Status(String code, List<String> messageParams) {
        this.code = code;
        this.messageParams = messageParams;
    }
}
