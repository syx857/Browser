package com.tech;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * listen content change
 * uri content://downloads/my_downloads/
 */
public class DownloadObserver extends ContentObserver {
    public static final String TAG = "DownloadObserver";
    Callback callback;

    public DownloadObserver(Handler handler, Callback callback) {
        super(handler);
        this.callback = callback;
    }

    @Override
    public void onChange(boolean selfChange, @Nullable Uri uri) {
        if (uri != null && this.callback != null) {
            Log.d(TAG, "onChange: uri: " + uri);
            String string = uri.getLastPathSegment();
            try {
                long id = Long.parseLong(string);
                callback.onChange(id);
            } catch (Exception e) {
                Log.d(TAG, "onChange: " + e);
            }
        }
    }

    public interface Callback {
        void onChange(long id);
    }
}
