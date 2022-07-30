package com.apps.jaywalker.kaveri_b;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(new GsonBuilder().
                serializeNulls()
                .create()))
                .baseUrl("http://templeapi.obbe.in/")
                .client(okHttpClient)
                .build();

        return retrofit;
    }



    public static Apis getUserService(){
        Apis userService = getRetrofit().create(Apis.class);

        return userService;
    }




}


