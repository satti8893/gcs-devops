package com.ga_gcs.logintest.repository;

import android.text.TextUtils;

import com.ga_gcs.logintest.models.Login.User;
import com.ga_gcs.logintest.models.Requests.LoginRequest;
import com.ga_gcs.logintest.models.Responses.Error;
import com.ga_gcs.logintest.models.Responses.ServerResponse;
import com.ga_gcs.logintest.retrofit.GAApiInterface;
import com.ga_gcs.logintest.retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {

    private static final String TAG = LoginRepository.class.getSimpleName();

    private static LoginRepository loginRepository;

    public static LoginRepository getInstance() {
        if (loginRepository == null) {
            loginRepository = new LoginRepository();
        }
        return loginRepository;
    }

    public LoginRepository() {}

    public void requestLoginDetails(String username, String password, LoginCallback loginCallback) {
        RetrofitService.getRetrofit();
        RetrofitService.createService(GAApiInterface.class)
                .getLoginUser(new LoginRequest(username, password)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    String strUser = jsonObject.toString();
                    User user = new Gson().fromJson(strUser, User.class);
                    loginCallback.onLoginSuccess(user);
                } else {
                    int statusCode = response.code();
                    ResponseBody responseBody = response.errorBody();
                    String strUser = null;
                    try {
                        if (responseBody != null) {
                            strUser = responseBody.string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (statusCode == 401) {
                        if (!TextUtils.isEmpty(strUser)) {
                            Error error = new Gson().fromJson(strUser, Error.class);
                            loginCallback.onLoginInvalid(error);
                        }
                    } else if (statusCode == 400) {
                        if (!TextUtils.isEmpty(strUser)) {
                            ServerResponse serverResponse = new Gson().fromJson(strUser, ServerResponse.class);
                            loginCallback.onLoginFailed(serverResponse);
                        }
                    } else {
                        String responseBody1 = response.raw().message();
                        loginCallback.onLoginException(responseBody1);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                t.printStackTrace();
                loginCallback.onLoginException(t.getMessage());
            }
        });
    }
}
