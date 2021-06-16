package com.tech.model;

import android.graphics.Bitmap;

import java.util.UUID;

public class WebFragmentToken {
    public final String tag;
    public String title = "";
    public String url = "";
    public Bitmap favicon;

    public WebFragmentToken() {
        tag = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "{" + "\"tag\":\"" +
                tag + '\"' +
                ",\"title\":\"" +
                title + '\"' +
                ",\"url\":\"" +
                url + '\"' +
                '}';
    }
}
