package com.tech.repository;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.tech.api.BookmarkApi;
import com.tech.dao.BookmarkDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.Bookmark;
import com.tech.domain.BookmarkArray;
import com.tech.domain.User;
import com.tech.utils.Const;
import com.tech.web.ResponseBody;
import com.tech.web.RetrofitFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkRepository {

    BookmarkDao bookmarkDao;
    BrowserDatabase appDatabase;
    BookmarkApi bookmarkApi;
    SharedPreferences sharedPreferences;

    public BookmarkRepository(Context context) {
        appDatabase = BrowserDatabase.getInstance(context);
        bookmarkDao = appDatabase.getBookMarkDao();
        bookmarkApi = RetrofitFactory.getInstance().create(BookmarkApi.class);
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
    }

    public LiveData<List<Bookmark>> searchBookmark(String key) {
        return bookmarkDao.findByUrlAndTitle(key);
    }

    public LiveData<List<Bookmark>> getBookmarkList() {
        return bookmarkDao.getAll();
    }

    public void loadBookmarkListFromRemote(User user) {
        _deleteAll();
        bookmarkApi.getBookmark(user).enqueue(new Callback<BookmarkArray>() {
            @Override
            public void onResponse(Call<BookmarkArray> call, Response<BookmarkArray> response) {
                if (response.body() != null) {
                    _addBookmark(response.body().result.toArray(new Bookmark[0]));
                } else {
                }
            }

            @Override
            public void onFailure(Call<BookmarkArray> call, Throwable t) {
                Log.d("mytest","on failure");
            }
        });
    }

    public void deleteBookmark(Bookmark... bookmark) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.deleteBookmark(bookmark);
                return null;
            }
        }.execute();

        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)){
            bookmarkApi.deleteBookmark(new BookmarkArray(Arrays.asList(bookmark))).enqueue(
                    new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
        }
    }

    public void _deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.deleteAll();
                return null;
            }
        }.execute();
    }

    public void deleteAll() {
        _deleteAll();

        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)){
            User user = new User(sharedPreferences.getString(Const.PHONE_NUMBER, ""));
            bookmarkApi.clearBookmark(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("mytest", "清空云书签");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    public void addBookmark(Bookmark bookmark) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.insertBookmark(bookmark);
                return null;
            }
        }.execute();

        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)){
            bookmarkApi.addBookmark(bookmark).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    public void _addBookmark(Bookmark... bookmark) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.addBookmark(bookmark);
                return null;
            }
        }.execute();
    }

    public void update(Bookmark bookmark, Bookmark newBookmark) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.update(bookmark, newBookmark);
                return null;
            }
        }.execute();

        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)){
            List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
            bookmarkList.add(bookmark);
            bookmarkList.add(newBookmark);
            bookmarkApi.updateBookmark(new BookmarkArray(bookmarkList)).enqueue(
                    new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
        }

    }
}
