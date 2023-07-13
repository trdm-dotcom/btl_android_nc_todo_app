package com.example.todo.model.request;

import java.util.Set;

public class OrganizationRequest {
    private String name;
    private Set<Long> members;

    public OrganizationRequest() {
    }

    public OrganizationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getMembers() {
        return members;
    }

    public void setMembers(Set<Long> members) {
        this.members = members;
    }
}
