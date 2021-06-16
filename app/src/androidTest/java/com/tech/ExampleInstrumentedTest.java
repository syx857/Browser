package com.tech;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.tech.domain.Bookmark;
import com.tech.domain.History;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.tech", appContext.getPackageName());
    }

    @Test
    public void testJson() throws JSONException {
        Bookmark bookmark = Bookmark.createBookmark("http://", "http title", "/aa/");
        System.out.println(bookmark.toString());
        Bookmark bookmarkCopy = Bookmark.toBookmark(bookmark.toString());
        System.out.println(bookmarkCopy.toString());

        History history = new History("http://", "baidu");
        System.out.println(history.toString());
        History historyCopy = History.toHistory(history.toString());
        System.out.println(historyCopy.toString());
    }
}