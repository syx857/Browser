package com.tech.ui.web;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebInterface {
    Context context;

    public WebInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void search(String text) {

    }
}
