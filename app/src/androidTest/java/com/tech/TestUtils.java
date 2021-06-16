package com.tech;

import com.tech.domain.Bookmark;
import com.tech.domain.History;

import java.util.Date;

public class TestUtils {
    static final int dataSize = 5;
    public static final Bookmark[] bookmarks = new Bookmark[dataSize];
    public static final History[] histories = new History[dataSize];

    static {
        bookmarks[0] = Bookmark.createBookmark("http://jandan.net/p/109066", "煎蛋", "/学习/");
        bookmarks[1] = Bookmark.createBookmark("https://github.com/freefq/free", "github", "/学习/我的最爱/");
        bookmarks[2] = Bookmark.createDirectory("学习", "/");
        bookmarks[3] = Bookmark.createDirectory("我的最爱", "/学习/");
        bookmarks[4] = Bookmark.createBookmark("https://music.163.com/", "网易云音乐", "/");

        histories[0] = new History("http://jandan.net/p/109066", "煎蛋", new Date(1623069692653L));
        histories[1] = new History("http://jandan.net/p/109066", "煎蛋", new Date(1623069792653L));
        histories[2] = new History("http://jandan.net/p/109066", "煎蛋", new Date(1623069892653L));
        histories[3] = new History("http://jandan.net/p/109066", "煎蛋", new Date(1623069992653L));
        histories[4] = new History("http://jandan.net/p/109066", "煎蛋", new Date(1623070092653L));
    }
}
