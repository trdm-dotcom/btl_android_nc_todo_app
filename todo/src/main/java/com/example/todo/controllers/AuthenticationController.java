package com.example.todo.controllers;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.models.request.LoginRequest;
import com.example.todo.models.request.RefreshTokenRequest;
import com.example.todo.models.request.RegisterRequest;
import com.example.todo.models.response.Status;
import com.example.todo.servies.AuthenticationService;
import com.example.todo.servies.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.example.todo.common.exception.ErrorCodeEnums.INTERNAL_SERVER_ERROR;

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
            return ResponseEntity.ok(this.authenticationService.login(request));
        } catch (Exception e) {
            log.error("Error: ", e);
            if(e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            }
            else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(this.authenticationService.register(request));
        } catch (Exception e) {
            log.error("Error: ", e);
            if(e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            }
            else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @PostMapping(path = "/refreshToken")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            return ResponseEntity.ok(this.tokenService.refreshToken(request.getRefreshToken()));
        } catch (Exception e) {
            log.error("Error: ", e);
            if(e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            }
            else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }
}
