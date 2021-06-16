package com.tech;

import android.app.Application;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tech.domain.History;
import com.tech.model.WebFragmentToken;
import com.tech.ui.WebFragment;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    WebFragmentManager manager;

    MutableLiveData<Integer> progress = new MutableLiveData<>();

    MutableLiveData<String> typeIn = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        manager = new WebFragmentManager(R.id.container);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        manager.setFragmentManager(fragmentManager);
    }

    public void clearFragmentManager() {
        manager.clearFragmentManager();
    }

    public void notifyTokenChanged() {
        manager.notifyCurrentTokenChanged();
        manager.notifyTokenListChanged();
    }

    public LiveData<WebFragmentToken> getCurrentToken() {
        return manager.getCurrentToken();
    }

    public WebFragmentToken getCurrentTokenValue() {
        return manager.getCurrentTokenValue();
    }

    public LiveData<List<WebFragmentToken>> getList() {
        return manager.getList();
    }

    public boolean isEmpty() {
        return manager.isEmpty();
    }

    public boolean isShown() {
        return manager.isShown();
    }

    public void addFragment() {
        manager.addFragment();
    }

    public void addFragment(Message msg) {
        manager.addFragment(msg);
    }

    public void showFragment(WebFragmentToken token) {
        manager.showFragment(token);
    }

    public void removeFragment(WebFragmentToken token) {
        WebFragmentToken current = manager.getCurrentTokenValue();
        if (current == null) {
            WebFragmentToken next = manager.findNextToken(token);
            manager.removeFragment(token);
            if (next == null) {
                manager.addFragment();
            } else {
                manager.showFragment(next);
            }
            return;
        }
        if (current == token || current.tag.equals(token.tag)) {
            WebFragmentToken next = manager.findNextToken(token);
            manager.removeFragment(token);
            if (next == null) {
                manager.addFragment();
            } else {
                manager.showFragment(next);
            }
            return;
        }
        {
            manager.removeFragment(token);
        }
    }

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress.setValue(progress);
    }

    public WebFragmentToken findNextToken(WebFragmentToken token) {
        return manager.findNextToken(token);
    }

    public void loadUrl(String url) {
        WebFragment fragment = manager.getCurrentFragment();
        if (fragment == null) {
            addFragment();
            fragment = manager.getCurrentFragment();
        }
        fragment.loadUrl(url);
    }

    public boolean goBackward() {
        WebFragment fragment = manager.getCurrentFragment();
        if (fragment != null) {
            return fragment.goBackward();
        }
        return false;
    }

    public boolean goForward() {
        WebFragment fragment = manager.getCurrentFragment();
        if (fragment != null) {
            return fragment.goForward();
        }
        return false;
    }

    public void goHome() {
        WebFragment fragment = manager.getCurrentFragment();
        if (fragment != null) {
            fragment.goHome();
        }
    }

    public void refresh() {
        WebFragment fragment = manager.getCurrentFragment();
        if (fragment != null) {
            fragment.refresh();
        }
    }

    public void close() {
        WebFragment fragment = manager.getCurrentFragment();
        if (fragment != null) {
            fragment.close();
            WebFragmentToken token = manager.getCurrentTokenValue();
            WebFragmentToken next = manager.findNextToken(token);
            manager.removeFragment(token);
            if (next != null) {
                manager.showFragment(token);
            } else {
                manager.addFragment();
            }
        } else {
            manager.addFragment();
        }
    }

    public LiveData<String> getTypeIn() {
        return typeIn;
    }

    public void setTypeIn(String s) {
        typeIn.setValue(s);
    }

    public int getSize() {
        return manager.getSize();
    }

    public void insert(History history) {
        //TODO
    }
}
