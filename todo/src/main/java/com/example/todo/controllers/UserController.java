package com.example.todo.controllers;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.models.request.ConfirmRequest;
import com.example.todo.models.request.DataRequest;
import com.example.todo.models.request.UpdatePasswordRequest;
import com.example.todo.models.request.UserRequest;
import com.example.todo.models.response.Status;
import com.example.todo.servies.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static com.example.todo.common.exception.ErrorCodeEnums.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<Object> getUser(HttpServletRequest request) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.userService.getUser(dataRequest.getUserData().getId()));
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

    @GetMapping("/{user}")
    public ResponseEntity<Object> getUser(@PathVariable("user") Long id) {
        try {
            return ResponseEntity.ok(this.userService.getUser(id));
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

    @PutMapping
    public ResponseEntity<Object> updateUser(HttpServletRequest request, @RequestBody UserRequest userRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.userService.updateUser(dataRequest, userRequest));
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

    @PostMapping("/confirm")
    public ResponseEntity<Object> confirm(HttpServletRequest request, @RequestBody ConfirmRequest confirmRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.userService.confirm(dataRequest, confirmRequest));
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

    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(HttpServletRequest request, @RequestBody UpdatePasswordRequest passwordRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.userService.changePassword(dataRequest, passwordRequest));
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

    @GetMapping("/find")
    public ResponseEntity<Object> findUser(HttpServletRequest request, @RequestParam("search") String search) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.userService.findUser(dataRequest, search));
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
