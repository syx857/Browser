package com.example.browserapp.domain;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity
public class Bookmark implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id = 0;
    public String url;
    public String title;


    public Bookmark(String url, String title) {
        this.url = url;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
