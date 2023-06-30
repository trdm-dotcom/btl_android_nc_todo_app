package com.example.todo.model.dto;

import java.util.Set;

public class OrganizationDto {
    private Long id;
    private String name;
    private Set<UserData> users;
    private Integer done;
    private Integer left;

    public OrganizationDto() {
    }

    public OrganizationDto(Long id, String name, Set<UserData> users, Integer done, Integer left) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.done = done;
        this.left = left;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserData> getUsers() {
        return users;
    }

    public void setUsers(Set<UserData> users) {
        this.users = users;
    }

    public Integer getDone() {
        return done;
    }

    public void setDone(Integer done) {
        this.done = done;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }
}
