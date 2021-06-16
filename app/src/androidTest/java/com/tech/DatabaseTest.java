package com.tech;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tech.dao.BrowserDao;
import com.tech.database.BrowserDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    BrowserDao browserDao;
    BrowserDatabase database;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, BrowserDatabase.class).build();
        browserDao = database.getBrowserDao();
    }

    @After
    public void closeDB() throws IOException {
        database.close();
    }

    @Test
    public void test() {
        browserDao.insert(TestUtils.bookmarks);
        browserDao.insert(TestUtils.histories);
        System.out.println(browserDao.searchBookmarks("http"));
        System.out.println(browserDao.getBookmarks("/"));
        System.out.println(browserDao.getBookmarks("/学习/"));
        System.out.println(browserDao.getBookmarks("/学习/我的最爱/"));
        System.out.println(browserDao.getHistories());
        TestUtils.bookmarks[2].id = 3;
        browserDao.deleteBookmarks(TestUtils.bookmarks[2]);
        System.out.println(browserDao.getBookmarks("/"));
        System.out.println(browserDao.getBookmarks("/学习/"));
        System.out.println(browserDao.getBookmarks("/学习/我的最爱/"));
    }
}
