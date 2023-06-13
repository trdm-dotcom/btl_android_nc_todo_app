package com.example.todo.models.request;

import com.example.todo.models.dto.UserData;
import lombok.Data;

@Data
public class DataRequest {
    private String rid;
    private UserData userData;
}
