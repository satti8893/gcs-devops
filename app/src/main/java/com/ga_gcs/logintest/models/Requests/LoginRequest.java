package com.ga_gcs.logintest.models.Requests;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String userName, String password) {
        this.username = userName;
        this.password = password;
    }
}
