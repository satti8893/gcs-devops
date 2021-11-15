package com.ga_gcs.logintest.models.Responses;

public class ServerResponse {
    private long statusCode;
    private String error;
    private String message;
    private Validation validation;

    public String getError() {
        return error;
    }

}
