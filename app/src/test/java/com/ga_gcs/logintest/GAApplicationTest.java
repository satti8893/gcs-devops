package com.ga_gcs.logintest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ga_gcs.logintest.application.GAApplication;
import com.ga_gcs.logintest.constants.GAConstants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

@RunWith(JUnit4.class)
public class GAApplicationTest extends GAApplication implements TestLifecycleApplication {
    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    public void afterTest(Method method) {

    }

    @Test
    public void test_internet_available() {
        boolean available = isNetworkAvailable();
        assertEquals(false, available);
    }

}
