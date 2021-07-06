package com.tech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.webkit.WebView;
import com.tech.R;
import java.io.IOException;
import java.io.InputStream;

public class JsUtils {

    public static void addNightMode(WebView webView) {
        SharedPreferences sharedPreferences = webView.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Const.NIGHT_MODE, false)) {
            return;
        }
        InputStream is = webView.getContext().getResources().openRawResource(R.raw.night);
        byte[] buffer = new byte[0];
        try {
            buffer = new byte[is.available()];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String nightCode = Base64.encodeToString(buffer, Base64.NO_WRAP);

        webView.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);"
                + "var style = document.createElement('style');"
                + "style.type = 'text/css';"
                + "style.innerHTML = window.atob('"
                + nightCode + "');"
                + "parent.appendChild(style)"
                + "})();");
    }

}
