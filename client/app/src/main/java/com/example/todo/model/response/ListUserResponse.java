package com.example.todo.model.response;

import com.example.todo.model.dto.UserData;

import java.util.List;

public class ListUserResponse {
    List<UserData> chooseUser;
    List<UserData> listUser;

    public ListUserResponse() {
    }

    public ListUserResponse(List<UserData> chooseUser, List<UserData> listUser) {
        this.chooseUser = chooseUser;
        this.listUser = listUser;
    }

    public List<UserData> getChooseUser() {
        return chooseUser;
    }

    public void setChooseUser(List<UserData> chooseUser) {
        this.chooseUser = chooseUser;
    }

    public List<UserData> getListUser() {
        return listUser;
    }

    public void setListUser(List<UserData> listUser) {
        this.listUser = listUser;
    }
}
