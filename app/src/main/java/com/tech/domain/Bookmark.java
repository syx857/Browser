package com.tech.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity
public class Bookmark implements Serializable {

    @PrimaryKey@NonNull
    public String url;
    public String title;
    public String userId;


    public Bookmark(String url, String title, String userId) {
        this.url = url;
        this.title = title;
        this.userId = userId;
    }
}
