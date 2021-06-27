package com.tech.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.tech.api.HistoryApi;
import com.tech.dao.HistoryDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.History;
import com.tech.domain.HistoryArray;
import com.tech.domain.User;
import com.tech.web.ResponseBody;
import com.tech.web.RetrofitFactory;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository<AppDatabase> {

    HistoryDao historyDao;
    BrowserDatabase appDatabase;
    HistoryApi historyApi;

    public HistoryRepository(Context context) {
        appDatabase = BrowserDatabase.getInstance(context);
        historyDao = appDatabase.getHistoryDao();
        historyApi = RetrofitFactory.getInstance().create(HistoryApi.class);
    }

    public void addHistory(History history) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                historyDao.insertHistory(history);
                return null;
            }
        }.execute();
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

    public void deleteHistory(History... history) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                historyDao.deleteHistory(history);
                return null;
            }
        }.execute();
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

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                historyDao.deleteAll();
                return null;
            }
        }.execute();
    }

    public void loadHistoryListFromRemote(User user) {
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                historyDao.addHistory(histories);
                return null;
            }
        }.execute();
    }
}
