package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.NumberValidator;
import lombok.Data;

import java.util.List;

@Data
public class OrganizationMemberRequest {
    private Long organizationId;
    private List<Long> userId;

    public void validate() {
        new CombineValidator()
                .add(new NumberValidator("organizationId", this.getOrganizationId()).notEmpty())
                .check();
    }
}
