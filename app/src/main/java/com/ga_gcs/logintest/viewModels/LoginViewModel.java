package com.ga_gcs.logintest.viewModels;

import androidx.core.util.PatternsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.text.TextUtils;

import com.ga_gcs.logintest.R;
import com.ga_gcs.logintest.constants.GAConstants;
import com.ga_gcs.logintest.models.Login.User;
import com.ga_gcs.logintest.models.Responses.Error;
import com.ga_gcs.logintest.models.Responses.ServerResponse;
import com.ga_gcs.logintest.repository.LoginCallback;
import com.ga_gcs.logintest.repository.LoginRepository;
import com.ga_gcs.logintest.view.login.LoginFormState;

import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {

    private LoginRepository loginRepository;
    private MutableLiveData<User> successLoginData = new MutableLiveData<>();
    private MutableLiveData<Error> errorLoginData = new MutableLiveData<>();
    private MutableLiveData<ServerResponse> failedLoginData = new MutableLiveData<>();
    private MutableLiveData<String> serverError = new MutableLiveData<>();

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
//     If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<User> getLoginSuccessResult() {
        return successLoginData;
    }

    public LiveData<Error> getLoginInvalidResult() {
        return errorLoginData;
    }

    public LiveData<ServerResponse> getLoginFailedResult() {
        return failedLoginData;
    }

    public LiveData<String> getServerErrorResult() {
        return serverError;
    }

    public boolean isLoggedIn() {
        return successLoginData != null;
    }

    public boolean isDataValid() {
        if (loginFormState.getValue() == null) {
            return false;
        }
        return loginFormState.getValue().isDataValid();
    }

    public void login(String username, String password) {
        loginRepository.requestLoginDetails(username, password, new LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                successLoginData.setValue(user);
            }

            @Override
            public void onLoginInvalid(Error error) {
                errorLoginData.setValue(error);
            }

            @Override
            public void onLoginFailed(ServerResponse serverResponse) {
                failedLoginData.setValue(serverResponse);
            }

            @Override
            public void onLoginException(String exception) {
                serverError.setValue(exception);
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return !TextUtils.isEmpty(username) && PatternsCompat.EMAIL_ADDRESS.matcher(username).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) &&
                Pattern.compile(GAConstants.PATTERN_PASSWORD).matcher(password).matches();
    }
}
