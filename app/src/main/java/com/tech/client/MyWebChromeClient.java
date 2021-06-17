package com.tech.client;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {
    public static final String TAG = "MyWebChromeClient";
    Callback callback;

    public MyWebChromeClient(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (callback != null) {
            callback.onProgressChanged(newProgress);
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (callback != null) {
            callback.onCreateWindow(isDialog, isUserGesture, resultMsg);
        }
        return true;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (callback != null) {
            callback.onReceivedTitle(title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (callback != null) {
            callback.onReceivedIcon(icon);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        Log.d(TAG, "onShowCustomView: ");
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        Log.d(TAG, "onHideCustomView: ");
        super.onHideCustomView();
    }

    public interface Callback {
        void onProgressChanged(int progress);

        void onCreateWindow(boolean isDialog, boolean isUserGesture, Message resultMsg);

        void onReceivedTitle(String title);

        void onReceivedIcon(Bitmap icon);
    }
}
