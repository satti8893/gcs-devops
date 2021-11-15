package com.ga_gcs.logintest.retrofit;

import com.ga_gcs.logintest.BuildConfig;
import com.ga_gcs.logintest.constants.GAConstants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static Retrofit.Builder builder;

    public static void getRetrofit() {
        builder = new Retrofit.Builder()
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(GAConstants.BASE_URL);
    }

    public static <S> S createService(Class<S> serviceClass) {
        return builder.build().create(serviceClass);
    }

    public static OkHttpClient getHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        builder.addInterceptor(loggingInterceptor);

        return builder.build();
    }
}
