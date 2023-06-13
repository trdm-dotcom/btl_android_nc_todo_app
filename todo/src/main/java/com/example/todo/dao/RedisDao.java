package com.example.todo.dao;

import com.example.todo.constants.Constants;
import com.example.todo.models.dto.LoginValidate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisDao {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void addLoginValidate(LoginValidate loginValidate) throws JsonProcessingException {
        log.info("add Login Information for Validate {}", loginValidate);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(String.format("%s_%s", Constants.LOGIN_VALIDATE, loginValidate.getUsername()), objectMapper.writeValueAsString(loginValidate), 1, TimeUnit.DAYS);
    }

    public LoginValidate findLoginValidate(String username) throws JsonProcessingException {
        log.info("find Login Information by username {}", username);
        ValueOperations<String, String> setOperations = redisTemplate.opsForValue();
        String loginValidate = setOperations.get(String.format("%s_%s", Constants.LOGIN_VALIDATE, username));
        if (loginValidate != null) {
            return this.objectMapper.readValue(loginValidate, LoginValidate.class);
        } else {
            throw new RuntimeException(Constants.OBJECT_NOT_FOUND);
        }
    }
}
