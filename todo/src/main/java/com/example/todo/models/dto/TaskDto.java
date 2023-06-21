package com.example.todo.models.dto;

import com.example.todo.constants.enums.Priority;
import com.example.todo.constants.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private String colorCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private TaskStatus status;
    private Priority priority;
    private Set<UserData> assignees;
}
