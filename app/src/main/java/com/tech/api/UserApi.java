package com.tech.api;

import com.tech.domain.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.tech.web.ResponseBody;

public interface UserApi {

    @POST("login")
    Call<User> confirmLogin(@Body User user);

    @POST("register")
    Call<ResponseBody> register(@Body User user);

    @POST("getUser")
    Call<User> getUser(@Body User user);
}

