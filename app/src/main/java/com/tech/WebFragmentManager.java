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

import com.tech.adapter.TokenAdapter;
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
    public static final String TAG = "WebFragmentManager";
    Bundle bundle = new Bundle();
    WebFragmentToken currentTokenValue;
    List<WebFragmentToken> list = new Vector<>();

    MutableLiveData<Integer> count = new MutableLiveData<>(0);
    MutableLiveData<WebFragmentToken> currentToken = new MutableLiveData<>(currentTokenValue);

    TokenAdapter adapter;

    FragmentManager fm;
    int containerID;

    /**
     * @param containerID Fragment容器ID
     */
    public WebFragmentManager(int containerID) {
        this.containerID = containerID;
        adapter = new TokenAdapter(list);
    }

    /**
     * @param fm 设置FragmentManager，在Activity创建时调用
     */
    public void setFragmentManager(FragmentManager fm) {
        this.fm = fm;
    }

    /**
     * 清除FragmentManager，在Activity销毁时调用
     */
    public void clearFragmentManager() {
        fm = null;
    }

    /**
     * @return 容器内是否还有Fragment
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * @return 容器内是否有内容展示
     */
    public boolean isShown() {
        return currentTokenValue != null;
    }

    /**
     * 添加Fragment
     */
    public void addFragment() {
        addFragment(null);
    }

    /**
     * 添加Fragment，附带Message跳转信息
     *
     * @param message 跳转信息
     */
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
            /*
            在onCreate阶段向ViewModel注入依赖
             */
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

    /**
     * 展示Fragment
     *
     * @param token fragment的token
     */
    public void showFragment(@NonNull WebFragmentToken token) {
        if (fm != null) {
            if (currentTokenValue != token) {
                WebFragment webFragment = (WebFragment) fm.getFragment(bundle, token.tag);
                WebFragment currentFragment = getCurrentFragment();
                if (webFragment == null) {
                    Log.d(TAG, "showFragment: get null fragment");
                    return;
                }
                if (currentFragment == null) {
                    fm.beginTransaction().show(webFragment).commit();
                } else {
                    fm.beginTransaction().hide(currentFragment).show(webFragment).commit();
                }
                setCurrentToken(token);
            }
        }
    }

    /**
     * 移除Fragment
     *
     * @param token fragment的token
     */
    public void removeFragment(@NonNull WebFragmentToken token) {
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

    /**
     * 向列表添加Token，通知adapter，更新Token数
     *
     * @param token fragment的token
     */
    void addToken(WebFragmentToken token) {
        list.add(token);
        adapter.notifyItemInserted(list.size() - 1);
        updateTokenCount();
    }

    /**
     * 向列表移除Token，通知adapter，更新Token数
     *
     * @param token fragment的token
     */
    void removeToken(WebFragmentToken token) {
        int index = list.indexOf(token);
        list.remove(token);
        adapter.notifyItemRemoved(index);
        updateTokenCount();
    }

    /**
     * 寻找下一个Token
     *
     * @param token fragment的token
     * @return 下一个Fragment的Token，列表为空则返回null
     */
    public WebFragmentToken findNextToken(@NonNull WebFragmentToken token) {
        if (list.size() <= 0) {
            return null;
        }
        int index = list.indexOf(token);
        if (index < 0) {
            return list.get(0);
        }
        if (index - 1 >= 0) {
            return list.get(index - 1);
        }
        if (index + 1 < list.size()) {
            return list.get(index + 1);
        }
        return null;
    }

    @Deprecated
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
        adapter.setCurrent(token);
        currentTokenValue = token;
        currentToken.setValue(currentTokenValue);
    }

    public WebFragmentToken getCurrentTokenValue() {
        return currentTokenValue;
    }

    public WebFragment getCurrentFragment() {
        if (fm != null && currentTokenValue != null) {
            return (WebFragment) fm.getFragment(bundle, currentTokenValue.tag);
        }
        return null;
    }

    public void notifyTokenChanged(WebFragmentToken token) {
        if (token == currentTokenValue) {
            currentToken.setValue(token);
        }
        int index = list.indexOf(token);
        if (index >= 0) {
            adapter.notifyItemChanged(index);
        }
    }

    public void updateTokenCount() {
        count.setValue(list.size());
    }

    public int getSize() {
        return list.size();
    }

    public TokenAdapter getAdapter() {
        return adapter;
    }

    public LiveData<Integer> getCount() {
        return count;
    }
}
