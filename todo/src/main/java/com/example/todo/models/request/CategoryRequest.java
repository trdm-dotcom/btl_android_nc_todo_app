package com.example.todo.models.request;

import lombok.Data;

@Data
public class CategoryRequest extends DataRequest {
    private Long id;
    private String title;
    private String description;
    private String colorCode;
}
