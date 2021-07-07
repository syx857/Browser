package com.tech.utils;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.webkit.CookieManager;
import android.webkit.URLUtil;

import com.tech.domain.DownloadHistory;

import java.text.DecimalFormat;

public class DownloadUtils {
    public static final String TAG = "DownloadUtils";

    public static final String[] UNIT = {"B", "KB", "MB", "GB", "TB", "PB", "EB"};
    public static final DecimalFormat format = new DecimalFormat();

    static {
        format.applyLocalizedPattern("0.## ");
    }

    /**
     * 开始下载
     */
    public static DownloadHistory startDownload(String url, String contentDisposition, String mimetype, DownloadManager manager) {
        String cookie = CookieManager.getInstance().getCookie(url);
        String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(true);
        request.addRequestHeader("cookie", cookie);
        request.setTitle(filename);
        request.setDescription(url);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        long id = manager.enqueue(request);
        return new DownloadHistory(id, filename, url, mimetype);
    }

    public static Uri getFileUri(long id, DownloadManager manager) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = manager.query(query);
        String string;
        if (cursor != null && cursor.moveToFirst()) {
            string = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            if (string != null) {
                return Uri.parse(string);
            }
        }
        return null;
    }

    /**
     * 删除下载文件
     *
     * @param id      id
     * @param manager DownloadManager
     */
    public static void removeDownload(long id, DownloadManager manager) {
        manager.remove(id);
    }

    /**
     * 更新下载记录
     *
     * @param history         DownloadHistory
     * @param manager         DownloadManager
     * @param contentResolver ContentResolver
     */
    public static void updateDownload(DownloadHistory history, DownloadManager manager, ContentResolver contentResolver) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(history.id);
        Cursor cursor = manager.query(query);
        String string;
        if (cursor != null && cursor.moveToFirst()) {
            history.size = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            history.total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            history.status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            string = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
            if (string != null) {
                history.mime = string;
            }
            string = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            if (string != null) {
                history.filename = getFileName(Uri.parse(string), contentResolver);
            }
        } else {
            history.status = DownloadHistory.STATUS_CANCEL;
        }
    }

    /**
     * 暂停下载
     *
     * @param id       id
     * @param resolver ContentResolver
     */
    public static void pauseDownload(long id, ContentResolver resolver) {
        ContentValues values = new ContentValues();
        values.put("control", 1);
        resolver.update(Uri.parse("content://downloads/my_downloads/"), values, "_id=?", new String[]{"" + id});
    }

    /**
     * 继续下载
     *
     * @param id       id
     * @param resolver ContentResolver
     */
    public static void resumeDownload(long id, ContentResolver resolver) {
        ContentValues values = new ContentValues();
        values.put("control", 0);
        resolver.update(Uri.parse("content://downloads/my_downloads/"), values, "_id=?", new String[]{"" + id});
    }

    public static String getFileName(Uri uri, ContentResolver contentResolver) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public static String smartSize(long size) {
        int index = 0;
        long var1 = 1, var2 = 2 * 1024;
        do {
            if (size < var2) {
                double d = (1.0 * size) / var1;
                return format.format(d) + UNIT[index];
            }
            var1 *= 1024;
            var2 *= 1024;
            index += 1;
        } while (index < UNIT.length - 1);
        double d = (1.0 * size) / var1;
        return format.format(d) + UNIT[index];
    }
}
