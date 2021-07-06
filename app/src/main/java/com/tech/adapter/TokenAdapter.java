package com.tech.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.R;
import com.tech.databinding.ItemPageBinding;
import com.tech.model.WebFragmentToken;

import java.util.List;

/**
 * 可重用的适配器
 */
public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.TokenViewHolder> {
    static final int SELECTED_TEXT = Color.rgb(35, 114, 219);
    static final int UNSELECTED_TEXT = Color.rgb(117, 117, 117);
    static final int SELECTED_IMG = Color.rgb(35, 114, 219);
    static final int UNSELECTED_IMG = Color.rgb(117, 117, 117);

    List<WebFragmentToken> list;
    WebFragmentToken current;
    Callback callback;

    public TokenAdapter(@NonNull List<WebFragmentToken> list) {
        this.list = list;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void clearCallback() {
        callback = null;
    }

    /**
     * @param token 刷新选中的item
     */
    public void setCurrent(WebFragmentToken token) {
        if (current == token) {
            return;
        }
        WebFragmentToken prev = current;
        current = token;
        if (prev != null) {
            int index = list.indexOf(prev);
            if (index >= 0) {
                notifyItemChanged(index);
            }
        }
        if (token != null) {
            int index = list.indexOf(token);
            if (index >= 0) {
                notifyItemChanged(index);
            }
        }
    }

    /**
     * 常规Adapter重载方法
     */
    @NonNull
    @Override
    public TokenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPageBinding binding = ItemPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TokenViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TokenViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface Callback {
        /**
         * @param token 移除WebFragment，传入对应的Token
         */
        void removeWebFragment(WebFragmentToken token);

        /**
         * @param token 显示对应的WebFragment，传入对应的Token
         */
        void showWebFragment(WebFragmentToken token);
    }

    /**
     * 常规ViewHolder重载
     */
    class TokenViewHolder extends RecyclerView.ViewHolder {

        ItemPageBinding binding;
        WebFragmentToken token;

        public TokenViewHolder(@NonNull ItemPageBinding b) {
            super(b.getRoot());
            binding = b;
        }

        /**
         * 视图绑定
         *
         * @param token token，附带标题，图标信息
         */
        public void bind(WebFragmentToken token) {
            this.token = token;
            if (token == current || (current != null && token.tag.equals(current.tag))) {
                binding.title.setTextColor(SELECTED_TEXT);
                //binding.favicon.setColorFilter(SELECTED_IMG);
                binding.close.setColorFilter(SELECTED_IMG);
            } else {
                binding.title.setTextColor(UNSELECTED_TEXT);
                //binding.favicon.setColorFilter(UNSELECTED_IMG);
                binding.close.setColorFilter(UNSELECTED_IMG);
            }
            if (token.favicon != null) {
                binding.favicon.setImageBitmap(token.favicon);
            } else {
                binding.favicon.setImageResource(R.drawable.ic_outline_globe_24);
            }
            binding.title.setText(token.title);
            binding.getRoot().setOnClickListener(this::onClick);
            binding.close.setOnClickListener(this::onClick);
        }

        /**
         * 点击事件回调
         *
         * @param v 视图
         */
        public void onClick(View v) {
            if (callback == null || token == null) {
                return;
            }
            if (v == binding.getRoot()) {
                callback.showWebFragment(token);
            }
            if (v == binding.close) {
                callback.removeWebFragment(token);
            }
        }
    }
}
