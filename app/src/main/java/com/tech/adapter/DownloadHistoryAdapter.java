package com.tech.adapter;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.R;
import com.tech.databinding.ItemDownloadBinding;
import com.tech.domain.DownloadHistory;
import com.tech.utils.DownloadUtils;

import java.util.List;

public class DownloadHistoryAdapter extends RecyclerView.Adapter<DownloadHistoryAdapter.DownloadHistoryViewHolder> {
    List<DownloadHistory> list;
    Callback callback;
    Context context;

    public DownloadHistoryAdapter(List<DownloadHistory> list, Callback callback) {
        this.list = list;
        this.callback = callback;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DownloadHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDownloadBinding binding = ItemDownloadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DownloadHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadHistoryViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface Callback {
        void pause(DownloadHistory downloadHistory);

        void resume(DownloadHistory downloadHistory);

        void cancel(DownloadHistory downloadHistory);

        void delete(DownloadHistory downloadHistory);

        void remove(DownloadHistory downloadHistory);
    }

    class DownloadHistoryViewHolder extends RecyclerView.ViewHolder {
        PopupMenu popupMenu;
        ItemDownloadBinding binding;
        DownloadHistory downloadHistory;

        public DownloadHistoryViewHolder(ItemDownloadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DownloadHistory downloadHistory) {
            this.downloadHistory = downloadHistory;
            setText();
            setProgress();
            setVisibility();
            setControlImage();
            binding.control.setOnClickListener(this::onClick);
            binding.getRoot().setOnLongClickListener(this::onLongClick);
        }

        public void setProgress() {
            if (downloadHistory.total == 0) {
                binding.progressBar.setProgress(0);
            } else {
                binding.progressBar.setProgress((int) ((downloadHistory.size * 1.0 / downloadHistory.total) * 100));
            }
        }

        public void setVisibility() {
            if (downloadHistory.status == DownloadManager.STATUS_SUCCESSFUL) {
                binding.progressBar.setVisibility(View.GONE);
                binding.text.setVisibility(View.GONE);
                binding.control.setVisibility(View.GONE);
            }
            if (downloadHistory.status == DownloadManager.STATUS_FAILED) {
                binding.progressBar.setVisibility(View.GONE);
                binding.control.setVisibility(View.GONE);
            }
            if (downloadHistory.status == DownloadHistory.STATUS_CANCEL) {
                binding.progressBar.setVisibility(View.GONE);
                binding.control.setVisibility(View.GONE);
            }
        }

        public void setControlImage() {
            if (downloadHistory.status == DownloadManager.STATUS_PENDING || downloadHistory.status == DownloadManager.STATUS_RUNNING) {
                binding.control.setImageResource(R.drawable.ic_baseline_pause_24);
            }
            if (downloadHistory.status == DownloadManager.STATUS_PAUSED) {
                binding.control.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        }

        public void setText() {
            binding.filename.setText(downloadHistory.filename);
            String description = DownloadUtils.smartSize(downloadHistory.total) + " " + downloadHistory.url;
            binding.description.setText(description);
            String text = DownloadUtils.smartSize(downloadHistory.size) + " / " + DownloadUtils.smartSize(downloadHistory.total);
            switch (downloadHistory.status) {
                case DownloadManager.STATUS_FAILED:
                    text = "失败 " + text;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    text = "暂停 " + text;
                    break;
                case DownloadManager.STATUS_PENDING:
                    text = "挂起 " + text;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    text = "下载中 " + text;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    text = "下载成功 " + text;
                    break;
                case DownloadHistory.STATUS_CANCEL:
                    text = "已取消";
                    break;
            }
            binding.text.setText(text);
        }

        public boolean onLongClick(View v) {
            if (v == binding.getRoot() && context != null) {
                doPopupMenu();
            }
            return true;
        }

        public void onClick(View v) {
            if (v == binding.control && callback != null) {
                if (downloadHistory.status == DownloadManager.STATUS_PENDING || downloadHistory.status == DownloadManager.STATUS_RUNNING) {
                    callback.pause(downloadHistory);
                }
                if (downloadHistory.status == DownloadManager.STATUS_PAUSED) {
                    callback.resume(downloadHistory);
                }
            }
        }

        public boolean onMenuItemClick(MenuItem item) {
            if (callback == null) {
                return true;
            }
            if (item.getItemId() == R.id.cancel) {
                callback.cancel(downloadHistory);
            }
            if (item.getItemId() == R.id.delete_record) {
                callback.remove(downloadHistory);
            }
            if (item.getItemId() == R.id.delete_file) {
                callback.delete(downloadHistory);
            }
            if (item.getItemId() == R.id.copy_url) {
                saveToClipboard();
            }
            return true;
        }

        public void saveToClipboard() {
            if (context != null) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("下载链接", downloadHistory.url);
                cm.setPrimaryClip(mClipData);
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
            }
        }

        public void doPopupMenu() {
            if (context != null && popupMenu == null) {
                popupMenu = new PopupMenu(context, binding.getRoot());
                popupMenu.inflate(R.menu.menu_item_download);
                popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
            }
            if (popupMenu != null) {
                popupMenu.show();
            }
        }
    }
}
