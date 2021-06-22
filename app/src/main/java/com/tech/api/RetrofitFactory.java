package com.tech.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private static Retrofit retrofit;

    private static final String HOST = "https://qc6peb.fn.thelarkcloud.com/";

    static {
        retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getInstance() {
        return retrofit;
    }
}
