package com.tech.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.tech.dao.BookmarkDao;
import com.tech.dao.HistoryDao;
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

    public abstract BookmarkDao getBookMarkDao();

    public abstract HistoryDao getHistoryDao();

    public static BrowserDatabase getInstance(final Context context) {
        if (database == null) {
            synchronized (BrowserDatabase.class) {
                if (database == null) {
                    database = Room.databaseBuilder(context, BrowserDatabase.class, "browser.db")
                            .build();
                }
            }
        }
        return database;
    }

}
