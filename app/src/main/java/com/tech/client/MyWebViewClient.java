package com.tech.client;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tech.domain.History;
import com.tech.repository.HistoryRepository;

public class MyWebViewClient extends WebViewClient {
    public static final String TAG = "MyWebViewClient";
    Callback callback;
    boolean isLoad;

    public MyWebViewClient(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG, "onPageStarted: ");
        if (callback != null) {
            callback.onPageStarted(url, favicon);
        }
        isLoad = true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "onPageFinished: ");
        if (callback != null) {
            callback.onPageFinished(url);
        }
        if (isLoad) {
            addToHistory(view);
        }
        addImageClickListener(view);
    }

    /**
     * 注入js监听图片的点击事件并传图片url给native
     * @param webView
     */
    private void addImageClickListener(WebView webView) {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "var imgURLs=new Array(objs.length); " +
                "for(var i=0;i<objs.length;i++){" +
                "    imgURLs[i] = objs[i].src" +
                "}" +
                "for(let i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick= function()  " +
                "    {  "
                + "        window._TECH_BROWSER_.openImage(this.src, imgURLs, i);  " +
                "    } " +
                "}" +
                "})()");
    }

    /**
     * @param view    WebView
     * @param request Request
     * @return {true} 应该阻拦
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        isLoad = false;
        if (callback != null) {
            //Log.d("mytest",callback.shouldOverrideUrlLoading(view, request)+"");
            return callback.shouldOverrideUrlLoading(view, request) || super.shouldOverrideUrlLoading(view, request);
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    public interface Callback {
        void onPageStarted(String url, Bitmap favicon);

        void onPageFinished(String url);

        boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);
    }

    public void addToHistory(WebView view) {
        HistoryRepository historyRepository = new HistoryRepository(view.getContext());
        WebHistoryItem historyItem = view.copyBackForwardList().getCurrentItem();
        if(historyItem.getUrl().equals("file:///android_asset/home.html")) {
            return;
        }
        History history = new History(historyItem.getTitle(), historyItem.getUrl(),
                System.currentTimeMillis());
        historyRepository.addHistory(history);
    }
}
