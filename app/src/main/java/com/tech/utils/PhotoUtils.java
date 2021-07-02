package com.tech.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class PhotoUtils {

    public static Uri createImageUri(Activity activity) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, generateFileName() + ".jpg");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/zhiWei");
        ContentResolver contentResolver = activity.getContentResolver();
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public static void saveImage(Bitmap bitmap, Activity activity, Context context) {
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/zhiWei"),
                PhotoUtils.generateFileName() + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream outputStream;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver contentResolver = activity.getContentResolver();
            Uri uri = PhotoUtils.createImageUri(activity);
            if (uri == null) {
                Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                outputStream = contentResolver.openOutputStream(uri);
                FileInputStream fileInputStream = new FileInputStream(file);
                FileUtils.copy(fileInputStream, outputStream);
                fileInputStream.close();
                outputStream.close();
                Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MediaScannerConnection.scanFile(
                    activity.getApplicationContext(),
                    new String[]{file.getAbsolutePath()},
                    new String[]{"image/jpeg"},
                    (path, uri) -> {
                        // Scan Completed
                    });
        }
    }
}
