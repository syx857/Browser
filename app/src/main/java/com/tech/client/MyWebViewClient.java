package com.tech.client;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
    public static final String TAG = "MyWebViewClient";
    Callback callback;

    public MyWebViewClient(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG, "onPageStarted: ");
        if (callback != null) {
            callback.onPageStarted(url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "onPageFinished: ");
        if (callback != null) {
            callback.onPageFinished(url);
        }
    }

    /**
     * @param view    WebView
     * @param request Request
     * @return {true} 应该阻拦
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (callback != null) {
            return callback.shouldOverrideUrlLoading(view, request) && super.shouldOverrideUrlLoading(view, request);
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    public interface Callback {
        void onPageStarted(String url, Bitmap favicon);

        void onPageFinished(String url);

        boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);
    }
}
