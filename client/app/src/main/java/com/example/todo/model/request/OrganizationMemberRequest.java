package com.example.todo.model.request;

public class OrganizationMemberRequest {
    private Long organizationId;
    private Long userId;

    public OrganizationMemberRequest() {

    }

    public OrganizationMemberRequest(Long organizationId, Long userId) {
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
