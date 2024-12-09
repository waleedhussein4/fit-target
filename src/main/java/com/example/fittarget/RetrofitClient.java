package com.example.fittarget;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.fittarget.APIServices.SyncService;
import com.example.fittarget.APIServices.userService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private Retrofit retrofit;
    private userService userService;
    private SyncService syncService;

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://fit-target-backend.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        userService = retrofit.create(userService.class);
        syncService = retrofit.create(SyncService.class);

    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public userService getUserService() {
        return userService;
    }

    public SyncService getSyncService() {
        return syncService;
    }
}