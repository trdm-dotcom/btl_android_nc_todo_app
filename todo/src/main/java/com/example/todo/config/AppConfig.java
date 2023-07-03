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
    private Template template;
    private Mail mail;

    @Data
    public static class Template {
        private String dir;
    }

    @Data
    public static class Mail {
        private String endpoint;
        private int port;
        private String username;
        private String smtpUsername;
        private String smtpPassword;
        private String sender;
        private String support;
        private String subject;
    }
}
