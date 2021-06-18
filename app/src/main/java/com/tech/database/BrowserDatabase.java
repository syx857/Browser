package com.tech.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.tech.dao.BrowserDao;
import com.tech.domain.Bookmark;
import com.tech.domain.Converters;
import com.tech.domain.History;

@Database(
        entities = {Bookmark.class, History.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class BrowserDatabase extends RoomDatabase {
    static BrowserDatabase database;

    static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            BrowserDao browserDao = database.getBrowserDao();
            new Thread() {
                @Override
                public void run() {

                }
            }.start();
        }
    };

    public static BrowserDatabase getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), BrowserDatabase.class, "browser.db")
                    .addCallback(callback).build();
            database.getOpenHelper().getWritableDatabase();
        }
        return database;
    }

    public abstract BrowserDao getBrowserDao();
}
