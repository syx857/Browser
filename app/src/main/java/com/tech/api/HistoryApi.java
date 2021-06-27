package com.tech.api;

import com.tech.domain.History;
import com.tech.domain.HistoryArray;
import com.tech.domain.User;
import com.tech.web.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HistoryApi {

    @POST("getHistory")
    Call<HistoryArray> getHistory(@Body User user);

    @POST("addHistory")
    Call<ResponseBody> addHistory(@Body History history);
}
