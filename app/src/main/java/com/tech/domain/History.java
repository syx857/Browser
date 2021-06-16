package com.tech.domain;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@Entity
public class History {
    @PrimaryKey(autoGenerate = true)
    public int id = 0;
    public String title;
    public String url;
    public Date date;

    public History() {
    }

    @Ignore
    public History(String url, String title) {
        this.url = url;
        this.title = title;
        date = new Date();
    }

    @Ignore
    public History(String url, String title, Date date) {
        this.url = url;
        this.title = title;
        this.date = date;
    }

    public static History toHistory(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        History history = new History();
        history.id = jsonObject.getInt("id");
        history.title = jsonObject.getString("title");
        history.url = jsonObject.getString("url");
        history.date = new Date(jsonObject.getLong("date"));
        return history;
    }

    @Override
    public String toString() {
        return "{" + "\"id\":" +
                id +
                ",\"title\":\"" +
                title + '\"' +
                ",\"url\":\"" +
                url + '\"' +
                ",\"date\":\"" +
                date.getTime() + '\"' +
                '}';
    }
}
