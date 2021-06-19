package com.tech.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.tech.dao.BookmarkDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.Bookmark;
import java.util.List;

public class BookmarkRepository {

    BookmarkDao bookmarkDao;
    BrowserDatabase appDatabase;

    public BookmarkRepository(Context context) {
        appDatabase = BrowserDatabase.getInstance(context);
        bookmarkDao = appDatabase.getBookMarkDao();
    }

    public LiveData<List<Bookmark>> searchBookmark(String key) {
        return bookmarkDao.findByUrlAndTitle(key);
    }

    public LiveData<List<Bookmark>> getBookmarkList() {
        return bookmarkDao.getAll();
    }

    public void deleteBookmark(Bookmark... bookmark) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.deleteBookmark(bookmark);
                return null;
            }
        }.execute();
    }

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.deleteAll();
                return null;
            }
        }.execute();
    }

    public void addBookmark(Bookmark bookmark) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.addBookmark(bookmark);
                return null;
            }
        }.execute();
    }

    public void update(int id, String url, String title) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                bookmarkDao.update(id, url, title);
                return null;
            }
        }.execute();
    }
}
