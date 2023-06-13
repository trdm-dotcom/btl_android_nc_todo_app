package com.example.todo.models.response;

import com.example.todo.models.dto.UserData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private UserData userData;
    private Long accExpiredTime;
    private Long refExpiredTime;
}
