package com.tech;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.tech.model.WebFragmentToken;
import com.tech.ui.WebFragment;
import com.tech.ui.WebViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Vector;

/**
 * MainActivity创建，由MainActivity维护，是视图管理类
 */
public class WebFragmentManager {
    Bundle bundle = new Bundle();
    List<WebFragmentToken> listValue = new Vector<>();
    MutableLiveData<List<WebFragmentToken>> list = new MutableLiveData<>();
    WebFragmentToken currentTokenValue;
    MutableLiveData<WebFragmentToken> currentToken = new MutableLiveData<>(currentTokenValue);

    FragmentManager fm;
    int containerID;

    public WebFragmentManager(int containerID) {
        this.containerID = containerID;
    }

    public void setFragmentManager(FragmentManager fm) {
        this.fm = fm;
    }

    public void clearFragmentManager() {
        fm = null;
    }

    public boolean isEmpty() {
        return listValue.isEmpty();
    }

    public boolean isShown() {
        return currentTokenValue != null;
    }

    public void addFragment() {
        addFragment(null);
    }

    public void addFragment(Message message) {
        if (fm != null) {
            WebFragment webFragment = new WebFragment();
            WebFragment currentFragment = getCurrentFragment();
            WebFragmentToken token = new WebFragmentToken();
            if (currentFragment != null) {
                fm.beginTransaction().add(containerID, webFragment).hide(currentFragment).commit();
            } else {
                fm.beginTransaction().add(containerID, webFragment).commit();
            }
            fm.putFragment(bundle, token.tag, webFragment);
            webFragment.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onCreate(@NonNull @NotNull LifecycleOwner owner) {
                    WebViewModel viewModel = new ViewModelProvider(webFragment).get(WebViewModel.class);
                    viewModel.setResultMsg(message);
                    viewModel.setToken(token);
                    Log.d("hy-hy-hy", "inject dependency: ");
                    webFragment.getLifecycle().removeObserver(this);
                }
            });
            addToken(token);
            setCurrentToken(token);
        }
    }

    public void addFragmentBackground() {
        if (fm != null) {
            WebFragment webFragment = new WebFragment();
            WebFragmentToken token = new WebFragmentToken();
            WebViewModel viewModel = new ViewModelProvider(webFragment).get(WebViewModel.class);
            viewModel.setToken(token);
            fm.beginTransaction().add(containerID, webFragment).hide(webFragment).commit();
            fm.putFragment(bundle, token.tag, webFragment);
            addToken(token);
        }
    }

    public void showFragment(WebFragmentToken token) {
        if (fm != null) {
            if (currentTokenValue != token) {
                WebFragment webFragment = (WebFragment) fm.getFragment(bundle, token.tag);
                WebFragment currentFragment = getCurrentFragment();
                assert webFragment != null;
                if (currentFragment == null) {
                    fm.beginTransaction().show(webFragment).commit();
                } else {
                    fm.beginTransaction().hide(currentFragment).show(webFragment).commit();
                }
                setCurrentToken(token);
            }
        }
    }

    public void removeFragment(WebFragmentToken token) {
        if (fm != null) {
            WebFragment webFragment = (WebFragment) fm.getFragment(bundle, token.tag);
            if (webFragment != null) {
                fm.beginTransaction().remove(webFragment).commit();
            }
            removeToken(token);
            if (currentTokenValue == token) {
                setCurrentToken(null);
            }
        }
    }

    void addToken(WebFragmentToken token) {
        listValue.add(token);
        notifyTokenListChanged();
    }

    void removeToken(WebFragmentToken token) {
        listValue.remove(token);
        notifyTokenListChanged();
    }

    public WebFragmentToken findNextToken(WebFragmentToken token) {
        if (listValue.size() <= 0) {
            return null;
        }
        int index = listValue.indexOf(token);
        if (index < 0) {
            return listValue.get(0);
        }
        if (index - 1 >= 0) {
            return listValue.get(index - 1);
        }
        if (index + 1 < listValue.size()) {
            return listValue.get(index + 1);
        }
        return null;
    }

    public WebFragment findNextFragment(WebFragmentToken token) {
        if (fm != null) {
            WebFragmentToken nextToken = findNextToken(token);
            if (nextToken != null) {
                return (WebFragment) fm.getFragment(bundle, nextToken.tag);
            }
        }
        return null;
    }

    public LiveData<WebFragmentToken> getCurrentToken() {
        return currentToken;
    }

    void setCurrentToken(WebFragmentToken token) {
        currentTokenValue = token;
        notifyCurrentTokenChanged();
    }

    public WebFragmentToken getCurrentTokenValue() {
        return currentTokenValue;
    }

    public LiveData<List<WebFragmentToken>> getList() {
        return list;
    }

    public WebFragment getCurrentFragment() {
        if (fm != null && currentTokenValue != null) {
            return (WebFragment) fm.getFragment(bundle, currentTokenValue.tag);
        }
        return null;
    }

    public void notifyTokenListChanged() {
        list.setValue(listValue);
    }

    public void notifyCurrentTokenChanged() {
        currentToken.setValue(currentTokenValue);
    }

    public int getSize() {
        return listValue.size();
    }
}
