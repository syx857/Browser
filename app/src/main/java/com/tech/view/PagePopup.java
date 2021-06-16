package com.tech.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.R;
import com.tech.databinding.PageItemBinding;
import com.tech.databinding.PopupPageBinding;
import com.tech.model.WebFragmentToken;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PagePopup extends PopupWindow {
    PopupPageBinding binding;
    PagePopupCallback callback;

    public interface PagePopupCallback {
        void addWebFragment();

        void removeWebFragment(WebFragmentToken token);

        void showWebFragment(WebFragmentToken token);
    }

    public PagePopup(AppCompatActivity activity, PagePopupCallback cb) {
        super(activity);
        callback = cb;
        binding = PopupPageBinding.inflate(activity.getLayoutInflater());
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(binding.getRoot());
        binding.add.setOnClickListener(this::onClick);
    }

    public void onClick(View v) {
        if(v == binding.add && callback != null) {
            callback.addWebFragment();
        }
    }
/*
    static class DiffCallback extends DiffUtil.ItemCallback<WebFragmentToken> {
        @Override
        public boolean areItemsTheSame(@NonNull WebFragmentToken oldItem, @NonNull @NotNull WebFragmentToken newItem) {
            return oldItem.tag.equals(newItem.tag);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull @NotNull WebFragmentToken oldItem, @NonNull @NotNull WebFragmentToken newItem) {
            return oldItem.title.equals(newItem.title) && oldItem.url.equals(newItem.url) && oldItem.favicon == newItem.favicon;
        }
    }

    class TokenAdapter extends ListAdapter<WebFragmentToken, TokenAdapter.TokenViewHolder> {

        public TokenAdapter() {
            super(new DiffCallback());
        }

        @NonNull
        @Override
        public TokenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull TokenViewHolder holder, int position) {

        }

        class TokenViewHolder extends RecyclerView.ViewHolder {
            PageItemBinding binding;
            WebFragmentToken token;

            public TokenViewHolder(@NonNull PageItemBinding b) {
                super(b.getRoot());
                binding = b;
            }

            public void bind(WebFragmentToken token) {
                this.token = token;
                if(token.favicon != null) {
                    binding.favicon.setImageBitmap(token.favicon);
                }
                else {
                    binding.favicon.setImageResource(R.drawable.ic_outline_globe_24);
                }
                binding.title.setText(token.title);
                binding.getRoot().setOnClickListener(this::onClick);
                binding.close.setOnClickListener(this::onClick);
            }

            public void onClick(View v) {
                if(callback == null || token == null) {
                    return;
                }
                if(v == binding.getRoot()) {
                    callback.showWebFragment(token);
                }
                if(v == binding.close) {
                    callback.removeWebFragment(token);
                }
            }
        }
    }
 */
}
