package com.example.todo.models.response;

import com.example.todo.models.dto.UserData;
import lombok.Data;

import java.util.Set;

@Data
public class ListUserResponse {
    Set<UserData> chooseUser;
    Set<UserData> listUser;
}
