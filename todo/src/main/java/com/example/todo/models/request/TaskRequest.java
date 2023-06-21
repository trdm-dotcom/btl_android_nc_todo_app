package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.EnumValidator;
import com.example.todo.common.validator.NumberValidator;
import com.example.todo.common.validator.StringValidator;
import com.example.todo.constants.enums.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskRequest {
    private String title;
    private String description;
    private String priority;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private LocalDate startDate = LocalDate.now();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private LocalDate endDate = LocalDate.now().plus(7, ChronoUnit.DAYS);
    private Boolean remind = false;
    private List<Long> assignees;
    private Long organizationId;
    public void validate() {
        new CombineValidator()
                .add(new StringValidator("title", this.getTitle()).empty())
                .add(new StringValidator("description", this.getDescription()).empty())
                .add(new EnumValidator("priority", this.getPriority(), Priority.class).validate())
                .add(new NumberValidator("organizationId", this.getOrganizationId()).notEmpty())
                .check();
    }
}
