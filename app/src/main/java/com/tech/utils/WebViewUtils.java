package com.tech.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewUtils {

    @SuppressLint("SetJavaScriptEnabled")
    public static void initialWebView(WebView webView) {
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setFocusableInTouchMode(true);
        webView.setFocusable(true);
        webView.setScrollbarFadingEnabled(true);
        webView.setSaveEnabled(true);

        WebSettings settings = webView.getSettings();
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        //settings.setAppCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //Zoom
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        CookieManager.getInstance().setAcceptCookie(true);
    }

    public static void destroyWebView(WebView webView) {
        webView.stopLoading();
        webView.getSettings().setJavaScriptEnabled(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(null);
        webView.setTag(null);
        webView.clearHistory();
        webView.clearCache(false);
        webView.onPause();
        webView.removeAllViews();
        webView.destroy();
    }
}
