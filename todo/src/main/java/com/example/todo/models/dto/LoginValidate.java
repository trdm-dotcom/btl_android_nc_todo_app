package com.example.todo.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginValidate {
    private String username;
    private Integer failCount = 0;
    private LocalDateTime latestRequest;
}
