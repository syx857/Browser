package com.tech.repository;

import android.app.Application;

import androidx.work.WorkManager;

import com.tech.dao.BrowserDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.Bookmark;
import com.tech.domain.History;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrowserRepository {
    static BrowserRepository repository;

    BrowserDao browserDao;

    Application application;

    WorkManager workManager;

    ExecutorService service = Executors.newCachedThreadPool();

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

    public void insert(History... histories) {
        service.submit(() -> browserDao.insert(histories));
    }

    public void delete(History... histories) {
        service.submit(() -> browserDao.delete(histories));
    }

    public void insert(Bookmark... bookmarks) {
        service.submit(() -> browserDao.insert(bookmarks));
    }

    public void delete(Bookmark... bookmarks) {
        service.submit(() -> browserDao.deleteBookmarks(bookmarks));
    }

    public void update(Bookmark... bookmarks) {
        service.submit(() -> browserDao.update(bookmarks));
    }
}
