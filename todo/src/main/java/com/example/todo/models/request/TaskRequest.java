package com.example.todo.models.request;

import com.example.todo.constants.enums.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskRequest extends DataRequest {
    private Long id;
    private String title;
    private String description;
    private Long category;
    private Priority priority;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyy-MM-dd")
    private LocalDate date = LocalDate.now();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime start = LocalTime.now();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime end = LocalTime.now();
}
