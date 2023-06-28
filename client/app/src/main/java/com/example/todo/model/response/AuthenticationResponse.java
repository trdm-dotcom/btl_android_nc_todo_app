package com.example.todo.model.response;

import com.example.todo.model.dto.UserData;
import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("userData")
    private UserData userData;
    @SerializedName("accExpiredTime")
    private Long accExpiredTime;
    @SerializedName("refExpiredTime")
    private Long refExpiredTime;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String accessToken, String refreshToken, UserData userData, Long accExpiredTime, Long refExpiredTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userData = userData;
        this.accExpiredTime = accExpiredTime;
        this.refExpiredTime = refExpiredTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Long getAccExpiredTime() {
        return accExpiredTime;
    }

    public void setAccExpiredTime(Long accExpiredTime) {
        this.accExpiredTime = accExpiredTime;
    }

    public Long getRefExpiredTime() {
        return refExpiredTime;
    }

    public void setRefExpiredTime(Long refExpiredTime) {
        this.refExpiredTime = refExpiredTime;
    }
}
