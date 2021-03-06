package com.tech.repository;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.tech.api.HistoryApi;
import com.tech.dao.HistoryDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.History;
import com.tech.domain.HistoryArray;
import com.tech.domain.User;
import com.tech.utils.Const;
import com.tech.web.ResponseBody;
import com.tech.web.RetrofitFactory;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository<AppDatabase> {

    HistoryDao historyDao;
    BrowserDatabase appDatabase;
    HistoryApi historyApi;
    SharedPreferences sharedPreferences;
    ExecutorService service = Executors.newCachedThreadPool();

    public HistoryRepository(Context context) {
        appDatabase = BrowserDatabase.getInstance(context);
        historyDao = appDatabase.getHistoryDao();
        historyApi = RetrofitFactory.getInstance().create(HistoryApi.class);
        sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
    }

    public void addHistory(History history) {
        service.submit(() -> historyDao.insertHistory(history));

        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)){
            historyApi.addHistory(history).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("mytest","添加历史成功");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("mytest","添加历史失败");
                }
            });
        }
    }

    public void deleteHistory(History... history) {
        service.submit(() -> historyDao.deleteHistory(history));

        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)){
            historyApi.deleteHistory(new HistoryArray(Arrays.asList(history))).enqueue(
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


    public LiveData<List<History>> findByTitle(String title) {
        return historyDao.findByTitle(title);
    }

    public LiveData<List<History>> getHistoryList() {
        return historyDao.getAll();
    }

    public LiveData<List<History>> searchHistory(String key) {
        return historyDao.findByUrlAndTitle(key);
    }

    public void _deleteAll() {
        service.submit(() -> historyDao.deleteAll());
    }

    public void deleteAll() {
        _deleteAll();

        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)){
            User user = new User(sharedPreferences.getString(Const.PHONE_NUMBER, ""));
            historyApi.clearHistory(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("mytest", "清空云历史");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    public void loadHistoryListFromRemote(User user) {
        _deleteAll();
        historyApi.getHistory(user).enqueue(new Callback<HistoryArray>() {
            @Override
            public void onResponse(Call<HistoryArray> call, Response<HistoryArray> response) {
                if (response.body() != null) {
                    Log.d("mytest", "获取历史成功");
                    _addHistory(response.body().result.toArray(new History[0]));
                } else {
                    Log.d("mytest", "获取历史为空");
                }
            }

            @Override
            public void onFailure(Call<HistoryArray> call, Throwable t) {

            }
        });
    }

    public void _addHistory(History... histories) {
        service.submit(() -> historyDao.addHistory(histories));
    }
}
