package com.tech.client;

import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {
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

    public interface Callback {
        void onProgressChanged(int progress);

        void onCreateWindow(boolean isDialog, boolean isUserGesture, Message resultMsg);

        void onReceivedTitle(String title);
    }
}
