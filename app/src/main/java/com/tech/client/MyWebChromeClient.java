package com.tech.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.tech.utils.Const;
import com.tech.utils.JsUtils;

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
        if (this.callback != null) {
            this.callback.onShowCustomView(view, callback);
        }
    }

    @Override
    public void onHideCustomView() {
        Log.d(TAG, "onHideCustomView: ");
        if (callback != null) {
            callback.onHideCustomView();
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (callback != null) {
            return callback.onJsAlert(url, message, result);
        }
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (callback != null) {
            return callback.onJsConfirm(url, message, result);
        }
        return super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (callback != null) {
            return callback.onJsPrompt(url, message, defaultValue, result);
        }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    public interface Callback {
        void onProgressChanged(int progress);

        void onCreateWindow(boolean isDialog, boolean isUserGesture, Message resultMsg);

        void onReceivedTitle(String title);

        void onReceivedIcon(Bitmap icon);

        void onShowCustomView(View view, CustomViewCallback callback);

        void onHideCustomView();

        boolean onJsAlert(String url, String message, JsResult result);

        boolean onJsConfirm(String url, String message, JsResult result);

        boolean onJsPrompt(String url, String message, String defaultValue, JsPromptResult result);
    }
}
