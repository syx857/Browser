package com.example.browserapp.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.browserapp.databinding.HistoryItemBinding;
import com.example.browserapp.domain.History;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<History> {

    HistoryItemBinding binding;
    boolean showCheckbox = false;
    SparseBooleanArray checkState = new SparseBooleanArray();

    public HistoryAdapter(Context context, int resource, List<History> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        History history = getItem(position);
        if (convertView == null) {
            binding = HistoryItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (HistoryItemBinding) convertView.getTag();
        }

        binding.historyTitle.setText(history.historyTitle);
        binding.historyUrl.setText(history.historyUrl);
        if (showCheckbox) {
            binding.historyCheckbox.setVisibility(View.VISIBLE);
        } else {
            binding.historyCheckbox.setVisibility(View.GONE);
        }

        if (!checkState.get(position)) {
            binding.historyCheckbox.setChecked(false);
        } else {
            binding.historyCheckbox.setChecked(true);
        }

        return convertView;
    }

    public void setShowCheckbox(boolean show) {
        showCheckbox = show;
    }

    public void setChecked(boolean checked, int position) {
        checkState.put(position, checked);
    }

    public void clearCheckedState() {
        checkState.clear();
    }
}
