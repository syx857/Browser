package com.tech.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.PreferenceManager;
import com.tech.R;
import com.tech.databinding.PopupMenuBinding;
import com.tech.utils.Const;

public class MenuPopup extends PopupWindow {
    PopupMenuBinding binding;
    MenuPopupCallback callback;
    boolean isBookmarked = false;
    SharedPreferences sharedPreferences;

    public MenuPopup(AppCompatActivity activity, MenuPopupCallback cb) {
        super(activity);
        sharedPreferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        callback = cb;
        binding = PopupMenuBinding.inflate(LayoutInflater.from(activity));
        initial();
        setupOnClick();
        setLogin(sharedPreferences.getBoolean(Const.LOGIN_STATE, false));
        setIncognito(sharedPreferences.getBoolean(Const.INCOGNITO, false));
    }

    void onClick(View view) {
        if (callback != null) {
            callback.onClick(view.getId());
        }
    }

    void initial() {
        setContentView(binding.getRoot());
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(null);
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        setAnimationStyle(R.style.popwindow_anim_style);
    }

    public void setIsBookmarked(int i) {
        isBookmarked = !(i == 0);
        if (isBookmarked) {
            //TODO UI更新
        } else {
            //TODO
        }
    }

    void setupOnClick() {
        binding.navAddBookmark.setOnClickListener(this::onClick);
        binding.navBookmark.setOnClickListener(this::onClick);
        binding.navHistory.setOnClickListener(this::onClick);
        binding.navSetting.setOnClickListener(this::onClick);
        binding.navLogin.setOnClickListener(this::onClick);
        binding.navDownload.setOnClickListener(this::onClick);
        binding.navIncognito.setOnClickListener(this::onClick);
    }

    public interface MenuPopupCallback {
        void onClick(int id);
    }

    public void setLogin(boolean isLogin) {
        if (isLogin) {
            binding.navLogin.setImageResource(R.drawable.ic_nav_logged);
        } else {
            binding.navLogin.setImageResource(R.drawable.ic_nav_login);
        }
    }

    public void setIncognito(boolean isIncognito) {
        if (isIncognito) {
            binding.navIncognito.setImageResource(R.drawable.ic_nav_is_incognito);
        } else {
            binding.navIncognito.setImageResource(R.drawable.ic_nav_incognito);
        }
    }
}
