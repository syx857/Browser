package com.example.browserapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.browserapp.domain.History;
import java.util.List;

@Dao
public abstract class HistoryDao {

    @Insert
    public abstract void addHistory(History history);

    @Delete
    public abstract void deleteHistory(History... history);

    @Query("SELECT * FROM History ORDER BY time DESC")
    public abstract LiveData<List<History>> getAll();

    @Query("SELECT * FROM History WHERE historyTitle=:title")
    public abstract LiveData<List<History>> findByTitle(String title);

    @Query("SELECT * FROM History WHERE historyUrl=:url")
    public abstract LiveData<List<History>> findByUrl(String url);

    @Query("SELECT * FROM History WHERE historyUrl LIKE '%' || :key || '%' OR historyTitle LIKE '%' || :key || '%' ORDER BY time DESC")
    public abstract LiveData<List<History>> findByUrlAndTitle(String key);

    @Query("DELETE FROM History WHERE historyUrl=:url")
    public abstract void deleteByUrl(String url);

    @Query("DELETE FROM History")
    public abstract void deleteAll();

    @Transaction
    public void insertHistory(History history) {
        deleteByUrl(history.historyUrl);
        addHistory(history);
    }
}
