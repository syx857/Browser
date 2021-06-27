package com.tech.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {

    public String historyUrl;

    public String historyTitle;

    @PrimaryKey
    @NonNull
    public long time;
    public String userId;

    public History(String historyTitle, String historyUrl, long time, String userId) {
        this.historyUrl = historyUrl;
        this.historyTitle = historyTitle;
        this.time = time;
        this.userId = userId;
    }
}
