package com.tech.view;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.R;
import com.tech.adapter.TokenAdapter;
import com.tech.databinding.PopupPageBinding;
import com.tech.model.WebFragmentToken;

public class PagePopup extends PopupWindow implements TokenAdapter.Callback {
    public static final String TAG = "PagePopup";

    AppCompatActivity activity;
    PopupPageBinding binding;
    PagePopupCallback callback;
    TokenAdapter adapter;
    int maxHeight;

    public PagePopup(AppCompatActivity activity, PagePopupCallback cb, TokenAdapter adapter) {
        super(activity);
        this.activity = activity;
        callback = cb;
        this.adapter = adapter;
        binding = PopupPageBinding.inflate(activity.getLayoutInflater());
        initial();
        binding.add.setOnClickListener(this::onClick);
        adapter.setCallback(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(layoutManager);
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

    @Override
    public void removeWebFragment(WebFragmentToken token) {
        if (callback != null) {
            callback.removeWebFragment(token);
        }
    }

    @Override
    public void showWebFragment(WebFragmentToken token) {
        if (callback != null) {
            callback.showWebFragment(token);
        }
    }

    public void onClick(View v) {
        if (v == binding.add && callback != null) {
            callback.addWebFragment();
        }
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        Log.d(TAG, "setMaxHeight: " + maxHeight);
        Resources r = activity.getResources();
        int btnHeight = (int) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, r.getDisplayMetrics()));
        Log.d(TAG, "btnHeight: " + btnHeight);
        binding.recycler.setMaxHeight((int) (maxHeight - btnHeight));
    }

    public interface PagePopupCallback {
        void addWebFragment();

        void removeWebFragment(WebFragmentToken token);

        void showWebFragment(WebFragmentToken token);
    }
}
