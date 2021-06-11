package com.example.browserapp.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.browserapp.dao.HistoryDao;
import com.example.browserapp.domain.History;
import java.util.List;

public class HistoryRepository {

    HistoryDao historyDao;
    AppDatabase appDatabase;

    public HistoryRepository(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        historyDao = appDatabase.getHistoryDao();
    }

    public void addHistory(History history) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                historyDao.insertHistory(history);
                return null;
            }
        }.execute();
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
}
