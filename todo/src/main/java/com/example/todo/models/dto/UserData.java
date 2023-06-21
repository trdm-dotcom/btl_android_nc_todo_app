package com.example.todo.models.dto;

import com.example.todo.constants.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
}
