package com.example.todo.controllers;

import com.example.todo.models.request.LoginRequest;
import com.example.todo.models.request.RefreshTokenRequest;
import com.example.todo.models.request.RegisterRequest;
import com.example.todo.models.response.AuthenticationResponse;
import com.example.todo.models.response.RefreshTokenResponse;
import com.example.todo.servies.AuthenticationService;
import com.example.todo.servies.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private TokenService tokenService;

    @PostMapping(path = "/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        try {
            AuthenticationResponse response = this.authenticationService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        try {
            this.authenticationService.register(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Register error: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/refreshToken")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshTokenResponse response = this.tokenService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Refresh token error: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
