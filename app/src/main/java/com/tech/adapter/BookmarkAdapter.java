package com.tech.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.tech.databinding.BookmarkItemBinding;
import com.tech.domain.Bookmark;
import java.util.List;

public class BookmarkAdapter extends ArrayAdapter<Bookmark> {

    List<Bookmark> bookmarkList;
    BookmarkItemBinding binding;
    boolean showCheckbox = false;
    SparseBooleanArray checkState = new SparseBooleanArray();

    public BookmarkAdapter(@NonNull Context context, int resource,
            @NonNull List<Bookmark> objects) {
        super(context, resource, objects);
        this.bookmarkList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bookmark bookmark = bookmarkList.get(position);
        if (convertView == null) {
            binding = BookmarkItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (BookmarkItemBinding) convertView.getTag();
        }

        binding.bookmarkTitle.setText(bookmark.title);
        binding.bookmarkUrl.setText(bookmark.url);
        if (showCheckbox) {
            binding.bookmarkCheckbox.setVisibility(View.VISIBLE);
        } else {
            binding.bookmarkCheckbox.setVisibility(View.GONE);
        }

        if (!checkState.get(position)) {
            binding.bookmarkCheckbox.setChecked(false);
        } else {
            binding.bookmarkCheckbox.setChecked(true);
        }

        return convertView;
    }

    public void setShowCheckbox(boolean show) {
        showCheckbox = show;
        notifyDataSetChanged();
    }

    public void setChecked(boolean checked, int position) {
        checkState.put(position, checked);
        notifyDataSetChanged();
    }

    public void clearCheckedState() {
        checkState.clear();
        notifyDataSetChanged();
    }

    public void setBookmarkList(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bookmarkList.size();
    }
}
