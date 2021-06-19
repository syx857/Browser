package com.tech.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.tech.dao.HistoryDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.History;
import java.util.List;

public class HistoryRepository<AppDatabase> {

    HistoryDao historyDao;
    BrowserDatabase appDatabase;

    public HistoryRepository(Context context) {
        appDatabase = BrowserDatabase.getInstance(context);
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
