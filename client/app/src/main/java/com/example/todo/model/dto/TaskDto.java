package com.example.todo.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Set;

public class TaskDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("colorCode")
    private String colorCode;
    @SerializedName("startDate")
    private Date startDate;
    @SerializedName("endDate")
    private Date endDate;
    @SerializedName("status")
    private String status;
    @SerializedName("priority")
    private String priority;
    @SerializedName("assignees")
    private Set<UserData> assignees;
    @SerializedName("reminder")
    private Boolean reminder;
    @SerializedName("organization")
    private OrganizationDto organization;

    public TaskDto() {
    }

    public TaskDto(Long id, String title, String description, String colorCode, Date startDate, Date endDate, String status, String priority, Set<UserData> assignees, OrganizationDto organization) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.colorCode = colorCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.priority = priority;
        this.assignees = assignees;
        this.organization = organization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Set<UserData> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<UserData> assignees) {
        this.assignees = assignees;
    }

    public Boolean getReminder() {
        return reminder;
    }

    public void setReminder(Boolean reminder) {
        this.reminder = reminder;
    }

    public OrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDto organization) {
        this.organization = organization;
    }
}
