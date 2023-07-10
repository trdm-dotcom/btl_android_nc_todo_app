package com.example.todo.model.request;

import java.util.List;

public class OrganizationMemberRequest {
    private Long organizationId;
    private List<Long> userId;

    public OrganizationMemberRequest() {

    }

    public OrganizationMemberRequest(Long organizationId, List<Long> userId) {
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getUserId() {
        return userId;
    }

    public void setUserId(List<Long> userId) {
        this.userId = userId;
    }
}
