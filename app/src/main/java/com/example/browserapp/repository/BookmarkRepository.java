package com.example.browserapp.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.browserapp.dao.BookmarkDao;
import com.example.browserapp.domain.Bookmark;
import java.util.List;

public class BookmarkRepository {

    BookmarkDao bookmarkDao;
    AppDatabase appDatabase;

    public BookmarkRepository(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        bookmarkDao = appDatabase.getBookMarkDao();
    }

    public LiveData<List<Bookmark>> searchBookmark(String key) {
        return bookmarkDao.findByUrlAndTitle(key);
    }

    public LiveData<List<Bookmark>> getBookmarkList() {
        return bookmarkDao.getAll();
    }

    public void deletebookmark(Bookmark... bookmark) {
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
