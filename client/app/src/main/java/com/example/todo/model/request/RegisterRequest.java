package com.example.todo.model.request;

public class RegisterRequest {
    private String password;
    private String name;
    private String email;

    public RegisterRequest(String password, String name, String email) {
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public RegisterRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
