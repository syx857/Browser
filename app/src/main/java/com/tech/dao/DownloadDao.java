package com.tech.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tech.domain.DownloadHistory;

import java.util.List;

@Dao
public abstract class DownloadDao {

    @Insert
    public abstract void insert(DownloadHistory... downloadHistories);

    @Update
    public abstract void update(DownloadHistory... downloadHistories);

    @Delete
    public abstract void delete(DownloadHistory... downloadHistories);

    @Query("SELECT * FROM downloadHistory ORDER BY date DESC")
    public abstract List<DownloadHistory> getDownloadHistories();
}
