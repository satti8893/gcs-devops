package com.ga_gcs.logintest.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ga_gcs.logintest.BuildConfig;
import com.ga_gcs.logintest.constants.GAConstants;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GAApplication extends Application {

    private final static String TAG = GAApplication.class.getCanonicalName();
    private static GAApplication instance;
//    private static Retrofit retrofit = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        upgradeSecurityProvider();
    }

    public static synchronized GAApplication getInstance() {
        return instance;
    }

    private void upgradeSecurityProvider() {
        try {
            ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
                @Override
                public void onProviderInstalled() {
                    Log.i(TAG, "New security provider installed.");
                }

                @Override
                public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
                    GooglePlayServicesUtil.showErrorNotification(errorCode, GAApplication.this);
                    Log.e(TAG, "New security provider install failed.");
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, "Unknown issue trying to install a new security provider", ex);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
