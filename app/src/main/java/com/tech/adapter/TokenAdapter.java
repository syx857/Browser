package com.tech.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.R;
import com.tech.databinding.PageItemBinding;
import com.tech.model.WebFragmentToken;

import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.TokenViewHolder> {
    static final int SELECTED_TEXT = Color.rgb(35,114,219);
    static final int UNSELECTED_TEXT = Color.rgb(32,33,37);
    static final int SELECTED_IMG = Color.rgb(35,114,219);
    static final int UNSELECTED_IMG = Color.rgb(117,117,117);

    List<WebFragmentToken> list;
    WebFragmentToken current;
    Callback callback;

    public interface Callback {
        void removeWebFragment(WebFragmentToken token);

        void showWebFragment(WebFragmentToken token);
    }

    public TokenAdapter(List<WebFragmentToken> list, Callback callback) {
        this.list = list;
        this.callback = callback;
    }

    public void setCurrent(WebFragmentToken current) {
        this.current = current;
    }

    @NonNull
    @Override
    public TokenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PageItemBinding binding = PageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    class TokenViewHolder extends RecyclerView.ViewHolder {

        PageItemBinding binding;
        WebFragmentToken token;

        public TokenViewHolder(@NonNull PageItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        public void bind(WebFragmentToken token) {
            if(token == current || (current != null && token.tag.equals(current.tag))) {
                binding.title.setTextColor(SELECTED_TEXT);
                binding.favicon.setColorFilter(SELECTED_IMG);
                binding.close.setColorFilter(SELECTED_IMG);
            }
            else {
                binding.title.setTextColor(UNSELECTED_TEXT);
                binding.favicon.setColorFilter(UNSELECTED_IMG);
                binding.close.setColorFilter(UNSELECTED_IMG);
            }
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
