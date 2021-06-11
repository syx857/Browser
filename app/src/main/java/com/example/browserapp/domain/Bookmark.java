package com.example.browserapp.domain;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Bookmark {
    @PrimaryKey(autoGenerate = true)
    public int id = 0;
    public String url;
    public String title;
    public boolean isDirectory;
    public boolean isBookmark;
    /**
     * '/' root directory
     */
    public String directory;

    public Bookmark() {
    }

    /**
     * 创造书签
     *
     * @param url       保存链接
     * @param title     标题
     * @param directory 路径 默认'/'
     */
    @Ignore
    public Bookmark(String url, String title, String directory) {
        this.url = url;
        this.title = title;
        this.isBookmark = true;
        this.isDirectory = false;
        if (directory == null || directory.length() == 0) {
            this.directory = "/";
        } else {
            this.directory = directory;
        }
    }

    /**
     * 创造文件夹
     *
     * @param title     文件夹名
     * @param directory 路径 默认'/' '/path1/path2/'
     */
    @Ignore
    public Bookmark(String title, String directory) {
        this.url = "";
        this.title = title;
        this.isBookmark = false;
        this.isDirectory = true;
        if (directory == null || directory.length() == 0) {
            this.directory = "/";
        } else {
            this.directory = directory;
        }
    }

    public static Bookmark createDirectory(String title, String directory) {
        return new Bookmark(title, directory);
    }

    public static Bookmark createBookmark(String url, String title, String directory) {
        return new Bookmark(url, title, directory);
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", isDirectory=" + isDirectory +
                ", isBookmark=" + isBookmark +
                ", directory='" + directory + '\'' +
                '}';
    }
}
