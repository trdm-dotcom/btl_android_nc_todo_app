package com.example.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private String privateKey;
    private String privateJwtKey;
    private Integer accessTokenExpirationInMs;
    private Integer refreshTokenExpirationInMs;
    private String clientSecretLogin;
    private Boolean encryptPassword;
    private Integer loginTemporarilyLocked;
    private Integer loginTemporarilyLockedTime;
}
