package com.ga_gcs.logintest.models.Login;

public class UserData {
    private long id;
    private String full_name;
    private String email;
    private String mobile;
    private String designation;
    private String photo;
    private String address;
    private String status;
    private Role role;
    private Customer customer;

    public String getFull_name() {
        return full_name;
    }
}
