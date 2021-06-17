package com.example.browserapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.browserapp.domain.Bookmark;
import java.util.List;

@Dao
public abstract class BookmarkDao {

    @Insert
    public abstract void addBookmark(Bookmark bookmark);

    @Delete
    public abstract void deleteBookmark(Bookmark... bookmarks);

    @Query("SELECT * FROM Bookmark")
    public abstract LiveData<List<Bookmark>> getAll();

    @Query("SELECT * FROM Bookmark WHERE title=:title")
    public abstract LiveData<List<Bookmark>> findByTitle(String title);

    @Query("SELECT * FROM Bookmark WHERE url=:url")
    public abstract LiveData<List<Bookmark>> findByUrl(String url);

    @Query("SELECT * FROM Bookmark WHERE url LIKE '%' || :key || '%' OR title LIKE '%' || :key || '%' ")
    public abstract LiveData<List<Bookmark>> findByUrlAndTitle(String key);

    @Query("DELETE FROM Bookmark")
    public abstract void deleteAll();

    @Query("UPDATE Bookmark SET url=:url, title=:title WHERE id=:id")
    public abstract void update(int id, String url, String title);

}
