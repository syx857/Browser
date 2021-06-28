package com.tech.view;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.PreferenceManager;
import com.tech.R;
import com.tech.databinding.PopupMenuBinding;

public class MenuPopup extends PopupWindow {
    PopupMenuBinding binding;
    MenuPopupCallback callback;
    boolean isBookmarked = false;

    public MenuPopup(AppCompatActivity activity, MenuPopupCallback cb) {
        super(activity);
        callback = cb;
        binding = PopupMenuBinding.inflate(LayoutInflater.from(activity));
        initial();
        setupOnClick();
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
    }

    public interface MenuPopupCallback {
        void onClick(int id);
    }
}
