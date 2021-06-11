package com.example.browserapp.repository;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.browserapp.dao.BookmarkDao;
import com.example.browserapp.dao.HistoryDao;
import com.example.browserapp.domain.Bookmark;
import com.example.browserapp.domain.History;

@Database(entities = {Bookmark.class, History.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "BrowserApp.db";

    public abstract BookmarkDao getBookMarkDao();

    public abstract HistoryDao getHistoryDao();

    private static AppDatabase database;

    public static AppDatabase getInstance(final Context context) {
        if (database == null) {
            synchronized (AppDatabase.class) {
                if (database == null) {
                    database = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return database;
    }

}
