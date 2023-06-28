package com.example.todo.model.response;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenResponse {
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("accExpiredTime")
    private Long accExpiredTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getAccExpiredTime() {
        return accExpiredTime;
    }

    public void setAccExpiredTime(Long accExpiredTime) {
        this.accExpiredTime = accExpiredTime;
    }
}
