package com.tech.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.tech.domain.Bookmark;
import com.tech.domain.History;
import java.util.List;

@Dao
public abstract class BookmarkDao {

    @Insert
    public abstract void addBookmark(Bookmark... bookmark);

    @Delete
    public abstract void deleteBookmark(Bookmark... bookmarks);

    @Query("SELECT * FROM Bookmark")
    public abstract LiveData<List<Bookmark>> getAll();

    @Query("SELECT * FROM Bookmark WHERE title=:title")
    public abstract LiveData<List<Bookmark>> findByTitle(String title);

    @Query("SELECT * FROM Bookmark WHERE url=:url")
    public abstract List<Bookmark> findByUrl(String url);

    @Query("SELECT * FROM Bookmark WHERE url LIKE '%' || :key || '%' OR title LIKE '%' || :key || '%' ")
    public abstract LiveData<List<Bookmark>> findByUrlAndTitle(String key);

    @Query("DELETE FROM Bookmark")
    public abstract void deleteAll();

    @Query("UPDATE Bookmark SET url=:url, title=:title WHERE url=:url")
    public abstract void update(String url, String title);

    @Transaction
    public void update(Bookmark bookmark, Bookmark newBookmark) {
        deleteBookmark(bookmark);
        insertBookmark(newBookmark);
    }

    @Query("DELETE FROM Bookmark WHERE url=:url")
    public abstract void deleteByUrl(String url);

    @Transaction
    public void insertBookmark(Bookmark bookmark) {
        deleteByUrl(bookmark.url);
        addBookmark(bookmark);
    }
}
