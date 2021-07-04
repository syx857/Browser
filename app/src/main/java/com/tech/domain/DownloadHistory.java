package com.tech.domain;

import android.app.DownloadManager;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class DownloadHistory {
    //public static final int STATUS_MISSING = -2;
    public static final int STATUS_CANCEL = -1;

    @PrimaryKey
    public long id = 0;

    public String filename;

    public String url;

    public String mime;

    public long total = 0;

    public long size = 0;

    public int status = DownloadManager.STATUS_PENDING;

    public Date date;

    public DownloadHistory() {
    }

    @Ignore
    public DownloadHistory(long id, String filename, String url, String mime) {
        this.id = id;
        this.filename = filename;
        this.url = url;
        this.mime = mime;
        date = new Date();
    }

    @Override
    public String toString() {
        return "{" + "\"id\":" +
                id +
                ",\"filename\":\"" +
                filename + '\"' +
                ",\"url\":\"" +
                url + '\"' +
                ",\"mime\":\"" +
                mime + '\"' +
                ",\"total\":" +
                total +
                ",\"size\":" +
                size +
                ",\"status\":" +
                status +
                ",\"date\":\"" +
                date.getTime() + '\"' +
                '}';
    }
}
