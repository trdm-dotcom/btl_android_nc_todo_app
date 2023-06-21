package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.StringValidator;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;

    public void validate() {
        new CombineValidator()
                .add(new StringValidator("oldPassword", this.getOldPassword()).empty())
                .add(new StringValidator("newPassword", this.getNewPassword()).empty())
                .check();
    }
}
