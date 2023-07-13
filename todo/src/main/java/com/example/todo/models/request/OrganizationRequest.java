package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.StringValidator;
import lombok.Data;

import java.util.Set;

@Data
public class OrganizationRequest {
    private String name;
    private Set<Long> members;
    public void validate() {
        new CombineValidator()
                .add(new StringValidator("name", this.getName()).empty()).check();
    }
}
