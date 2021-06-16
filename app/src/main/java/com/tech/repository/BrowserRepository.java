package com.tech.repository;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tech.dao.BrowserDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.History;

import java.util.concurrent.ExecutorService;

public class BrowserRepository {
    static BrowserRepository repository;

    BrowserDao browserDao;

    Application application;

    WorkManager workManager;

    ExecutorService service;

    BrowserRepository(Application application) {
        this.application = application;
        workManager = WorkManager.getInstance(application);
        BrowserDatabase database = BrowserDatabase.getDatabase(application);
        browserDao = database.getBrowserDao();
    }

    public static BrowserRepository getInstance(Application application) {
        if (repository == null) {
            repository = new BrowserRepository(application);
        }
        return repository;
    }

    public void insert(History history) {
        Data data = new Data.Builder()
                .putString("title", history.title)
                .putString("url", history.url)
                .putLong("date", history.date.getTime())
                .build();
    }

    public class InsertHistory extends Worker {
        History[] histories;

        public InsertHistory(@NonNull Context context, @NonNull WorkerParameters workerParams, History... histories) {
            super(context, workerParams);
            this.histories = histories;
        }

        @NonNull
        @Override
        public Result doWork() {
            browserDao.insert(histories);
            return Result.success();
        }
    }

}
