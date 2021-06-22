package com.tech.api;

import com.tech.domain.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    @POST("login")
    Call<ResponseBody> confirmLogin(@Body User user);

    @POST("register")
    Call<ResponseBody> register(@Body User user);
}

