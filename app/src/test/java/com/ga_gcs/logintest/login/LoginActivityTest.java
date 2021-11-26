package com.ga_gcs.logintest.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.widget.Button;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.ga_gcs.logintest.R;
import com.ga_gcs.logintest.constants.GAConstants;
import com.ga_gcs.logintest.models.Login.User;
import com.ga_gcs.logintest.models.Responses.Error;
import com.ga_gcs.logintest.models.Responses.ServerResponse;
import com.ga_gcs.logintest.retrofit.RestServiceTestHelper;
import com.ga_gcs.logintest.view.login.LoginActivity;
import com.ga_gcs.logintest.viewModels.LoginViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private LoginActivity loginActivity;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private Button btnSignIn;

    private LoginViewModel loginViewModel;
//    private LoginRepository loginRepository;

    private Observer<User> successObserver;
    private Observer<Error> invalidObserver;
    private Observer<ServerResponse> failedObserver;
    private Observer<String> serverErrorObserver;

    private MockWebServer server;

    private String login_success_expected = "{\"auth_data\":{\"access_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI5cGZ1QU5hc0dlbUJLb2J1OW9Tay14eEFXV0xmRFFuQ0hQSmF5OU8wbVZFIn0.eyJleHAiOjE2MzU3NTk5NzMsImlhdCI6MTYzNTc1NjM3MywianRpIjoiYWVmNzJlOWYtMjlhMi00ZDA0LTliNTYtMTFlOGEwY2QzZmE2IiwiaXNzIjoiaHR0cDovLzY1LjAuMjAuNTE6ODA4MC9hdXRoL3JlYWxtcy9HQSIsImF1ZCI6WyJnYS1odWIiLCJhY2NvdW50Il0sInN1YiI6ImE4NTdjN2RkLTYwYTQtNDhiOS05YjUwLTdkYTVkMTRmZTNkMSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdhLWh1Yi1zZXJ2ZXIiLCJzZXNzaW9uX3N0YXRlIjoiOWY0ZjFkNjUtYjMwYy00MWU2LThkNzktYWJjZTlhMjUwZDc1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTUuMjA2LjE0OC4yNDQ6MzAwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1nYSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJnYS1odWIiOnsicm9sZXMiOlsiU3VwZXJ2aXNvciJdfSwiZ2EtaHViLXNlcnZlciI6eyJyb2xlcyI6WyJTdXBlcnZpc29yIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiI5ZjRmMWQ2NS1iMzBjLTQxZTYtOGQ3OS1hYmNlOWEyNTBkNzUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlJhaHVsIFMiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJyYWh1bC5zdXJlc2hAZ2VuZXJhbGFlcm9uYXV0aWNzLmNvbSIsImdpdmVuX25hbWUiOiJSYWh1bCIsImZhbWlseV9uYW1lIjoiUyIsImVtYWlsIjoicmFodWwuc3VyZXNoQGdlbmVyYWxhZXJvbmF1dGljcy5jb20ifQ.bSjZGqFVinxW_sJ7sE5j4JoVX8xhhGiCTRF9sdL_X7mUZ5kKi55fku5INlLwhIFloMd_j-s2YQzx2MzZEj8-pX4xXVsPNs-yOsjXm2zX0POD-Lr2J7k09JoXqpcLAReNckQJHbOA8eKFmizObUSRkNWrLZ-JGtTiowPdvBvPU53MLmJjwM-GxmxFyzpXRvUAk3rWwpaIdaeq-dLJuBuXdiRfOyNcxTYB-MAWf-rKZ3s4ZEICjdNRPNVpV-Z4nGHkuuhMfw4FkSuhLXL9_AQbpC9UTijP-JnQXFXnoa-OBFTwegvBr9GjK3mAceSfKN2dI1du__Heu9eAAxV5g43i9A\",\"expires_in\":3600,\"refresh_expires_in\":7200,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlZTZlY2RkMC04N2YxLTQ4MjktOTE5Ny1iZDViMDM3ZTdjMmYifQ.eyJleHAiOjE2MzU3NjM1NzMsImlhdCI6MTYzNTc1NjM3MywianRpIjoiOGU5NGZiMTctZjMyZC00ZTZlLThhYzEtMWE5NjIxNjM0MWQ2IiwiaXNzIjoiaHR0cDovLzY1LjAuMjAuNTE6ODA4MC9hdXRoL3JlYWxtcy9HQSIsImF1ZCI6Imh0dHA6Ly82NS4wLjIwLjUxOjgwODAvYXV0aC9yZWFsbXMvR0EiLCJzdWIiOiJhODU3YzdkZC02MGE0LTQ4YjktOWI1MC03ZGE1ZDE0ZmUzZDEiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiZ2EtaHViLXNlcnZlciIsInNlc3Npb25fc3RhdGUiOiI5ZjRmMWQ2NS1iMzBjLTQxZTYtOGQ3OS1hYmNlOWEyNTBkNzUiLCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiI5ZjRmMWQ2NS1iMzBjLTQxZTYtOGQ3OS1hYmNlOWEyNTBkNzUifQ.EFnc8EETnUSkVmWIrk6pEF9g6QJiVExls7oB3Z1O52A\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"9f4f1d65-b30c-41e6-8d79-abce9a250d75\",\"scope\":\"email profile\"},\"user_data\":{\"id\":31,\"full_name\":\"Rahul S\",\"email\":\"rahul.suresh@generalaeronautics.com\",\"mobile\":null,\"designation\":null,\"photo\":\"photo\",\"address\":null,\"status\":\"Active\",\"role\":{\"role_name\":\"Supervisor\"},\"customer\":{\"name\":\"General Aeronautics\"}}}";
    private String invalid_credentials_expected = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid user credentials\"}";
    private String bad_response_expected = "{\"statusCode\":400,\"error\":\"Bad Request\",\"message\":\"\\\"password\\\" with value \\\"Demo1234\\\" fails to match the required pattern: /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/\",\"validation\":{\"source\":\"payload\",\"keys\":[\"password\"]}}";

    @Before
    public void setup() throws IOException {

        server = new MockWebServer();
        server.start();
        GAConstants.BASE_URL = server.url("/").toString();

        loginActivity = Robolectric.buildActivity(LoginActivity.class).create().resume().get();
        tilEmail = loginActivity.findViewById(R.id.til_login_id);
        tilPassword = loginActivity.findViewById(R.id.til_password);
        btnSignIn = loginActivity.findViewById(R.id.btn_sign_in);

//        loginRepository = new LoginRepository();
//        loginViewModel = new LoginViewModel(loginRepository);

        loginViewModel = loginActivity.getLoginViewModel();

        loginViewModel.getLoginSuccessResult().observeForever(successObserver);
        loginViewModel.getLoginInvalidResult().observeForever(invalidObserver);
        loginViewModel.getLoginFailedResult().observeForever(failedObserver);
        loginViewModel.getServerErrorResult().observeForever(serverErrorObserver);
    }

    @Test
    public void test_activity_not_null() throws Exception {
        assertNotNull(loginActivity);
    }

    @Test
    public void test_set_text_entry_field() throws Exception {
        String expected = "settext";
        loginActivity.setTILText(tilEmail, expected);
        assertEquals(expected, loginActivity.getTILText(tilEmail));
    }

    @Test
    public void test_get_text_entry_field() throws Exception {
        String expected = "gettext";
        loginActivity.setTILText(tilPassword, expected);
        assertEquals(expected, loginActivity.getTILText(tilPassword));
    }

    @Test
    public void test_email_without_domain() {
        loginViewModel.loginDataChanged("email", "");
        assertEquals(loginActivity.getString(R.string.invalid_username), tilEmail.getError());

    }

    @Test
    public void test_email_invalid_domain() {
        loginViewModel.loginDataChanged("email.com", "");
        assertEquals(loginActivity.getString(R.string.invalid_username), tilEmail.getError());
    }

    @Test
    public void test_email_without_dot_com() {
        loginViewModel.loginDataChanged("email@gmail", "");
        assertEquals(loginActivity.getString(R.string.invalid_username), tilEmail.getError());
    }

    @Test
    public void test_email_valid() {
        loginViewModel.loginDataChanged("email@gmail.com", "");
        assertNull(tilEmail.getError());
    }

    @Test
    public void test_email_empty() {
        loginViewModel.loginDataChanged("", "");
        assertFalse(loginViewModel.getLoginFormState().getValue().isDataValid());
    }

    @Test
    public void test_password_length_invalid() {
        loginViewModel.loginDataChanged("email@gmail.com", "pass");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_empty() {
        loginViewModel.loginDataChanged("email@gmail.com", "");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_invalid_length() {
        loginViewModel.loginDataChanged("email@gmail.com", "passw");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_no_special_character() {
        loginViewModel.loginDataChanged("email@gmail.com", "Password1");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_no_capital_letter() {
        loginViewModel.loginDataChanged("email@gmail.com", "@password1");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_no_number() {
        loginViewModel.loginDataChanged("email@gmail.com", "@Passwordpassword");
        assertEquals(loginActivity.getString(R.string.invalid_password), tilPassword.getError());
    }

    @Test
    public void test_password_valid() {
        loginViewModel.loginDataChanged("email@gmail.com", "@Password123");
        assertNull(tilPassword.getError());
    }

    @Test
    public void test_email_password_valid() {
        loginViewModel.loginDataChanged("email@gmail.com", "@Password123");
        assertTrue(loginViewModel.isDataValid());
    }

    @Test
    public void test_data_invalid_on_button_click() {

    }

    @Test
    public void test_login_success() throws Exception {
        User user = new Gson().fromJson(login_success_expected, User.class);
        //Prepare Mock data
        String fileName = "success_response.json";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(this, fileName))
        );

        //Pre-test
        assertEquals(null, loginViewModel.getLoginSuccessResult().getValue());

//        loginActivity.setTILText(tilEmail, "satheesh.kumar@generalaeronautics.com");
//        loginActivity.setTILText(tilPassword, "Password@1234");
//
//        btnSignIn.callOnClick();

        loginViewModel.login("username", "password");

        Thread.sleep(2000);

        assertTrue(loginViewModel.getLoginSuccessResult().hasObservers());
        assertNotNull(loginViewModel.getLoginSuccessResult().getValue());
        assertEquals(user.getUser_data().getFull_name(),
                loginViewModel.getLoginSuccessResult().getValue().getUser_data().getFull_name());

        assertTrue(loginViewModel.isLoggedIn());


    }

    @Test
    public void test_login_invalid_credentials() throws Exception {
        Error error = new Gson().fromJson(invalid_credentials_expected, Error.class);
        //Prepare Mock data
        String fileName = "failed_response.json";
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody(RestServiceTestHelper.getStringFromFile(this, fileName))
        );

        //Pre-test
        assertEquals(null, loginViewModel.getLoginInvalidResult().getValue());

        loginViewModel.login("username", "password");

        Thread.sleep(2000);

        assertTrue(loginViewModel.getLoginInvalidResult().hasObservers());
        assertNotNull(loginViewModel.getLoginInvalidResult().getValue());
        assertEquals(error.getError_description(),
                loginViewModel.getLoginInvalidResult().getValue().getError_description());

    }

    @Test
    public void test_login_bad_request() throws Exception {
        ServerResponse serverResponse = new Gson().fromJson(bad_response_expected, ServerResponse.class);
        //Prepare Mock data
        String fileName = "bad_request_response.json";
        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(RestServiceTestHelper.getStringFromFile(this, fileName))
        );

        //Pre-test
        assertEquals(null, loginViewModel.getLoginFailedResult().getValue());

        loginViewModel.login("username", "password");

        Thread.sleep(2000);

        assertTrue(loginViewModel.getLoginFailedResult().hasObservers());
        assertNotNull(loginViewModel.getLoginFailedResult().getValue());
        assertEquals(serverResponse.getError(),
                loginViewModel.getLoginFailedResult().getValue().getError());

    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }
}
