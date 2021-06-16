package com.tech.client;

import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
    Callback callback;

    public MyWebViewClient(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (callback != null) {
            callback.onPageStarted(url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (callback != null) {
            callback.onPageFinished(url);
        }
    }

    /**
     * @param view WebView
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
