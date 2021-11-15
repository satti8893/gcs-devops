package com.ga_gcs.logintest.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.os.SystemClock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ga_gcs.logintest.R;
import com.ga_gcs.logintest.constants.GAConstants;
import com.ga_gcs.logintest.retrofit.RestServiceTestHelper;
import com.ga_gcs.logintest.view.login.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public ActivityScenarioRule<LoginActivity> scenarioRule
        = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    private MockWebServer server;

    @Before
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();
        GAConstants.BASE_URL = server.url("/").toString();

    }

    @Test
    public void test_login_success() throws Exception {
        String fileName = "success_response.json";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName))
        );

        onView(ViewMatchers.withId(R.id.edt_login_id)).perform(typeText("rahul.suresh@generalaeronautics.com"));
        onView(withId(R.id.edt_password)).perform(typeText("Demo@1234"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Welcome Rahul S")));

    }

    @Test
    public void test_login_invalid() throws Exception {
        String fileName = "failed_response.json";
        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName))
        );

        onView(ViewMatchers.withId(R.id.edt_login_id)).perform(typeText("rahul.suresh@generalaeronautics.com"));
        onView(withId(R.id.edt_password)).perform(typeText("Demo@123"));

        onView(withId(R.id.btn_sign_in)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Invalid user credentials")));

    }

    @Test
    public void test_login_bad_request() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody("")
        );

        onView(ViewMatchers.withId(R.id.edt_login_id)).perform(typeText("rahul.suresh@generalaeronautics.com"));
        onView(withId(R.id.edt_password)).perform(typeText("Demo@1234")); //Removed Password validation

        onView(withId(R.id.btn_sign_in)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Server Error")));

    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }


}
