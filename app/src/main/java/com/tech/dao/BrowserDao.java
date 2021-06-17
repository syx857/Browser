package com.tech.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.tech.domain.Bookmark;
import com.tech.domain.History;

import java.util.List;
import java.util.Vector;

@Dao
public abstract class BrowserDao {
    @Insert
    public abstract void insert(History... histories);

    @Delete
    public abstract void delete(History... histories);

    @Query("SELECT * FROM history ORDER BY date DESC")
    public abstract LiveData<List<History>> getHistories();

    @Insert
    public abstract void insert(Bookmark... bookmarks);

    @Update
    public abstract void update(Bookmark... bookmarks);

    @Delete
    public abstract void delete(Bookmark... bookmarks);

    @Query("DELETE FROM bookmark WHERE bookmark.directory LIKE :directory || '%'")
    public abstract void deleteDirectory(String... directory);

    @Transaction
    public void deleteBookmarks(Bookmark... bookmarks) {
        delete(bookmarks);
        Vector<String> directories = new Vector<>();
        for (Bookmark b : bookmarks) {
            if (b.isDirectory) {
                directories.add(b.directory + b.title + "/");
            }
        }
        deleteDirectory(directories.toArray(new String[0]));
    }

    @Query("SELECT * FROM bookmark WHERE directory = :directory ORDER BY isBookmark, title")
    public abstract LiveData<List<Bookmark>> getBookmarks(String directory);

    @Query("SELECT CASE WHEN :url IN (SELECT url FROM bookmark) THEN 1 ELSE 0 END")
    public abstract LiveData<Integer> isBookmarked(String url);

    @Query("SELECT * FROM bookmark WHERE isBookmark = 1 AND ( url LIKE '%' || :str || '%' OR title LIKE '%' || :str || '%')")
    public abstract LiveData<List<Bookmark>> searchBookmarks(String str);

    @Query("SELECT * FROM history WHERE url LIKE '%' || :str || '%' OR title LIKE '%' || :str || '%'")
    public abstract LiveData<List<History>> searchHistories(String str);
}
