package com.ga_gcs.logintest.view.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ga_gcs.logintest.R;
import com.ga_gcs.logintest.application.GAApplication;
import com.ga_gcs.logintest.databinding.ActivityLoginBinding;
import com.ga_gcs.logintest.models.Login.User;
import com.ga_gcs.logintest.models.Responses.Error;
import com.ga_gcs.logintest.models.Responses.ServerResponse;
import com.ga_gcs.logintest.viewModels.LoginViewModel;
import com.ga_gcs.logintest.view.GAActivity;

import org.jetbrains.annotations.TestOnly;

public class LoginActivity extends GAActivity  {

    ActivityLoginBinding binding;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(LoginActivity.this,
                new LoginViewModelFactory()).get(LoginViewModel.class);

        initListeners();
    }

    private void initListeners() {
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            binding.btnSignIn.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                binding.tilLoginId.setError(getString(loginFormState.getUsernameError()));
            } else {
                binding.tilLoginId.setError(null);
            }
            if (loginFormState.getPasswordError() != null) {
                binding.tilPassword.setError(getString(loginFormState.getPasswordError()));
            } else {
                binding.tilPassword.setError(null);
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(getTILText(binding.tilLoginId),
                        getTILText(binding.tilPassword));
            }
        };
        binding.tilLoginId.getEditText().addTextChangedListener(afterTextChangedListener);
        binding.tilPassword.getEditText().addTextChangedListener(afterTextChangedListener);

        binding.tilPassword.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginUser();
            }
            return false;
        });

        binding.btnSignIn.setOnClickListener(v -> {
            loginUser();
        });

        initLoginObservers();
    }

    private void loginUser() {
        closeKeyboard();
        if (!loginViewModel.isDataValid()) {
            showSnackBar(getString(R.string.invalid_credentials));
            return;
        }
        if (!GAApplication.getInstance().isNetworkAvailable()) {
            showSnackBar(getString(R.string.no_internet));
            return;
        }
        binding.loading.setVisibility(View.VISIBLE);
        binding.btnSignIn.setEnabled(false);
        loginViewModel.login(getTILText(binding.tilLoginId),
                getTILText(binding.tilPassword));
    }

    private void initLoginObservers() {
        loginViewModel.getLoginSuccessResult().observe(this, user -> {
            if (user == null) {
                return;
            }
            updateUiWithUser(user);
        });
        loginViewModel.getLoginInvalidResult().observe(this, new Observer<Error>() {
            @Override
            public void onChanged(Error error) {
                showLoginFailed(error.getError_description());
            }
        });
        loginViewModel.getLoginFailedResult().observe(this, new Observer<ServerResponse>() {
            @Override
            public void onChanged(ServerResponse serverResponse) {
                showLoginFailed(serverResponse.getError());
            }
        });
        loginViewModel.getServerErrorResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showLoginFailed(s);
            }
        });
    }

    private void updateUiWithUser(User user) {
        binding.loading.setVisibility(View.GONE);
        binding.btnSignIn.setEnabled(true);
        String welcome = getString(R.string.welcome) + user.getUser_data().getFull_name();
        showSnackBar(welcome);
    }

    private void showLoginFailed(String errorString) {
        binding.loading.setVisibility(View.GONE);
        binding.btnSignIn.setEnabled(true);
        showSnackBar(errorString);
    }

    @TestOnly
    public LoginViewModel getLoginViewModel() {
        return loginViewModel;
    }
}
