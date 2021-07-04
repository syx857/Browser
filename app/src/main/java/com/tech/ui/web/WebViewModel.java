package com.tech.ui.web;

import android.app.Application;
import android.os.Bundle;
import android.os.Message;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.tech.model.WebFragmentToken;
import com.tech.repository.DownloadRepository;

public class WebViewModel extends AndroidViewModel {
    Bundle bundle;
    Message resultMsg;
    WebFragmentToken token;
    Object pageJump;
    DownloadRepository repository;

    public WebViewModel(Application application) {
        super(application);
        repository = DownloadRepository.getInstance(application);
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

    public Object getPageJump() {
        return pageJump;
    }

    public void setPageJump(Object pageJump) {
        this.pageJump = pageJump;
    }

    public void startDownload(String url, String contentDisposition, String mimetype) {
        repository.startDownload(url, contentDisposition, mimetype);
    }
}
