package com.example.browserapp.domain;

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

    public History(String historyTitle, String historyUrl, long time) {
        this.historyUrl = historyUrl;
        this.historyTitle = historyTitle;
        this.time = time;
    }
}
