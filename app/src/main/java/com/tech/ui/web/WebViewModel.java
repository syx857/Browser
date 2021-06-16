package com.tech.ui.web;

import android.os.Bundle;
import android.os.Message;

import androidx.lifecycle.ViewModel;

import com.tech.model.WebFragmentToken;

public class WebViewModel extends ViewModel {
    Bundle bundle;
    Message resultMsg;
    WebFragmentToken token;

    public WebViewModel() {
    }

    public Message getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(Message resultMsg) {
        this.resultMsg = resultMsg;
    }

    public WebFragmentToken getToken() {
        return token;
    }

    public void setToken(WebFragmentToken token) {
        this.token = token;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
