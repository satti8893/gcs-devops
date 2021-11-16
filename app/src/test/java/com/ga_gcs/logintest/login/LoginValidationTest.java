package com.ga_gcs.logintest.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.widget.Button;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.ga_gcs.logintest.R;
import com.ga_gcs.logintest.view.login.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
public class LoginValidationTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private LoginActivity loginActivity;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private Button btnSignIn;

    @Before
    public void setup() throws IOException {

        loginActivity = Robolectric.buildActivity(LoginActivity.class).create().resume().get();
        tilEmail = loginActivity.findViewById(R.id.til_login_id);
        tilPassword = loginActivity.findViewById(R.id.til_password);
        btnSignIn = loginActivity.findViewById(R.id.btn_sign_in);

    }

    @Test
    public void test_activity_not_null() throws Exception {
        assertNotNull(loginActivity);
    }

    @Test
    public void test_email_entry_field() throws Exception {
        String expected = "email@gmail.com";
        loginActivity.setTILText(tilEmail, expected);
        assertEquals(expected, loginActivity.getTILText(tilEmail));
    }

    @Test
    public void test_password_entry_field() throws Exception {
        String expected = "password";
        loginActivity.setTILText(tilPassword, expected);
        assertEquals(expected, loginActivity.getTILText(tilPassword));
    }

    @Test
    public void test_email_without_domain() {
        loginActivity.getLoginViewModel().loginDataChanged("email", "");
        assertEquals(loginActivity.getString(R.string.invalid_username), tilEmail.getError());

    }

    @Test
    public void test_email_invalid_domain() {
        loginActivity.getLoginViewModel().loginDataChanged("email.com", "");
        assertEquals(loginActivity.getString(R.string.invalid_username), tilEmail.getError());
    }

    @Test
    public void test_email_without_dot_com() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail", "");
        assertEquals(loginActivity.getString(R.string.invalid_username), tilEmail.getError());
    }

    @Test
    public void test_email_valid() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "");
        assertNull(tilEmail.getError());
    }

    @Test
    public void test_email_empty() {
        loginActivity.getLoginViewModel().loginDataChanged("", "");
        assertFalse(loginActivity.getLoginViewModel().getLoginFormState().getValue().isDataValid());
    }

    @Test
    public void test_password_length_invalid() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "pass");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_empty() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_invalid_length() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "passw");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_no_special_character() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "Password1");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_no_capital_letter() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "@password1");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_no_number() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "@Passwordpassword");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_valid() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "@Password123");
        assertNull(tilPassword.getError());
    }

    @Test
    public void test_email_password_valid() {
        loginActivity.getLoginViewModel().loginDataChanged("email@gmail.com", "@Password123");
        assertTrue(loginActivity.getLoginViewModel().isDataValid());
    }
}
