package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.NumberValidator;
import lombok.Data;

@Data
public class OrganizationMemberRequest {
    private Long organizationId;
    private Long userId;

    public void validate() {
        new CombineValidator()
                .add(new NumberValidator("organizationId", this.getOrganizationId()).notEmpty())
                .add(new NumberValidator("userId", this.getUserId()).notEmpty())
                .check();
    }
}
