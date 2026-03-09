package com.inari.usercenter.dto;

public class LoginResult {
    private String token;
    private UserDTO userInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }
}