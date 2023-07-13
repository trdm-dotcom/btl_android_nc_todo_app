package com.example.todo.model.request;

import java.util.Set;

public class TaskRequest {
    private String title;
    private String description;
    private String priority;
    private String startDate;
    private String endDate;
    private Boolean remind;
    private Set<Long> assignees;
    private Long organizationId;

    public TaskRequest() {
    }

    public TaskRequest(String title, String description, String priority, String startDate, String endDate, Boolean remind, Set<Long> assignees, Long organizationId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remind = remind;
        this.assignees = assignees;
        this.organizationId = organizationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }

    public Set<Long> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<Long> assignees) {
        this.assignees = assignees;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
