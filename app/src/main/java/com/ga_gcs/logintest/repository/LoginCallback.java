package com.ga_gcs.logintest.repository;

import com.ga_gcs.logintest.models.Login.User;
import com.ga_gcs.logintest.models.Responses.Error;
import com.ga_gcs.logintest.models.Responses.ServerResponse;

public interface LoginCallback {
    void onLoginSuccess(User user);
    void onLoginInvalid(Error error);
    void onLoginFailed(ServerResponse serverResponse);
    void onLoginException(String exception);
}
