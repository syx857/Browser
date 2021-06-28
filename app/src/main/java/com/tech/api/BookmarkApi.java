package com.tech.api;

import com.tech.domain.Bookmark;
import com.tech.domain.BookmarkArray;
import com.tech.domain.User;
import com.tech.web.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BookmarkApi {

    @POST("getBookmark")
    Call<BookmarkArray> getBookmark(@Body User user);

    @POST("addBookmark")
    Call<ResponseBody> addBookmark(@Body Bookmark bookmark);

    @POST("deleteBookmark")
    Call<ResponseBody> deleteBookmark(@Body BookmarkArray bookmark);

    @POST("updateBookmark")
    Call<ResponseBody> updateBookmark(@Body BookmarkArray bookmark);
}
