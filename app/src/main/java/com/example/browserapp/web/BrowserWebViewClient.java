package com.example.browserapp.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import com.example.browserapp.domain.History;
import com.example.browserapp.repository.HistoryRepository;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;

public class BrowserWebViewClient extends BridgeWebViewClient {

    HistoryRepository historyRepository;
    boolean isLoad;

    public BrowserWebViewClient(BridgeWebView webView) {
        super(webView);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        isLoad = false;
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        isLoad = true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (isLoad) {
            addToHistory(view);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
        super.onReceivedSslError(view, handler, error);
    }

    public void addToHistory(WebView view) {
        historyRepository = new HistoryRepository(view.getContext());
        WebHistoryItem historyItem = view.copyBackForwardList().getCurrentItem();

        History history = new History(historyItem.getTitle(), historyItem.getUrl(),
                System.currentTimeMillis());
        historyRepository.addHistory(history);
    }
}
