package com.ga_gcs.logintest.retrofit;

import com.ga_gcs.logintest.models.Requests.LoginRequest;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GAApiInterface {

    @POST("User/v1/login")
    Call<JsonElement> getLoginUser(@Body LoginRequest loginRequest);
}
