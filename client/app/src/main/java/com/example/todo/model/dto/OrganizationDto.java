package com.example.todo.model.dto;

import java.util.Set;

public class OrganizationDto {
    private Long id;
    private String name;
    private Set<UserData> users;

    public OrganizationDto() {
    }

    public OrganizationDto(Long id, String name, Set<UserData> users) {
        this.id = id;
        this.name = name;
        this.users = users;
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
}
