package com.tech.repository;

import android.app.Application;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tech.adapter.DownloadHistoryAdapter;
import com.tech.dao.DownloadDao;
import com.tech.database.BrowserDatabase;
import com.tech.domain.DownloadHistory;
import com.tech.utils.DownloadUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadRepository implements DownloadHistoryAdapter.Callback {
    public static final String TAG = "DownloadRepository";
    static DownloadRepository repository;
    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return false;
        }
    });
    ExecutorService service = Executors.newCachedThreadPool();

    List<DownloadHistory> list = new Vector<>();

    HashMap<Long, DownloadHistory> hashMap = new HashMap<>();

    DownloadHistoryAdapter adapter;

    DownloadDao downloadDao;

    ContentResolver resolver;

    DownloadManager manager;

    DownloadRepository(Application application) {
        BrowserDatabase database = BrowserDatabase.getInstance(application);
        downloadDao = database.getDownloadDao();
        resolver = application.getContentResolver();
        manager = (DownloadManager) application.getSystemService(Context.DOWNLOAD_SERVICE);
        adapter = new DownloadHistoryAdapter(list, this);
        initial();
    }

    public static DownloadRepository getInstance(Application application) {
        if (repository == null) {
            repository = new DownloadRepository(application);
        }
        return repository;
    }

    public DownloadHistoryAdapter getAdapter() {
        return adapter;
    }

    void initial() {
        service.submit(() -> {
            List<DownloadHistory> list = getDownloadHistories();
            for (DownloadHistory d : list) {
                DownloadUtils.updateDownload(d, manager, resolver);
            }
            updateDownloadHistories(list.toArray(new DownloadHistory[0]));
            handler.post(() -> {
                addAllEnd(list);
                for (DownloadHistory d : list) {
                    hashMap.put(d.id, d);
                }
            });
        });
    }

    public void startDownload(String url, String contentDisposition, String mimetype) {
        DownloadHistory downloadHistory = DownloadUtils.startDownload(url, contentDisposition, mimetype, manager);
        hashMap.put(downloadHistory.id, downloadHistory);
        insertDownloadHistories(downloadHistory);
        addFront(downloadHistory);
    }

    public void pauseDownload(long id) {
        DownloadUtils.pauseDownload(id, resolver);
    }

    public void resumeDownload(long id) {
        DownloadUtils.resumeDownload(id, resolver);
    }

    public void updateDownload(long id) {
        Log.d(TAG, "updateDownload: id = " + id);
        if (hashMap.containsKey(id)) {
            DownloadHistory downloadHistory = hashMap.get(id);
            if (downloadHistory != null) {
                DownloadUtils.updateDownload(downloadHistory, manager, resolver);
                updateDownloadHistories(downloadHistory);
                int idx = list.indexOf(downloadHistory);
                if (idx >= 0) {
                    adapter.notifyItemChanged(idx);
                }
                Log.d(TAG, "updateDownload: " + downloadHistory);
            }
        }
    }

    /**
     * 取消下载且保留下载记录，如果下载成功则不做任何事
     */
    public void cancelDownload(DownloadHistory downloadHistory) {
        if (downloadHistory != null && downloadHistory.status != DownloadManager.STATUS_SUCCESSFUL) {
            DownloadUtils.removeDownload(downloadHistory.id, manager);
            downloadHistory.status = DownloadHistory.STATUS_CANCEL;
            updateDownloadHistories(downloadHistory);
            int idx = list.indexOf(downloadHistory);
            if (idx >= 0) {
                adapter.notifyItemChanged(idx);
            }
        }
    }

    /**
     * 删除下载文件且删除下载记录
     */
    public void deleteDownload(DownloadHistory downloadHistory) {
        if (downloadHistory != null) {
            DownloadUtils.removeDownload(downloadHistory.id, manager);
            deleteDownloadHistories(downloadHistory);
            int idx = list.indexOf(downloadHistory);
            list.remove(downloadHistory);
            hashMap.remove(downloadHistory.id);
            if (idx >= 0) {
                adapter.notifyItemRemoved(idx);
            }
        }
    }

    /**
     * 去除下载记录，如果下载成功，则保留下载文件，如果下载未成功则删除下载文件和记录
     */
    public void removeDownload(DownloadHistory downloadHistory) {
        if (downloadHistory != null) {
            if (downloadHistory.status != DownloadManager.STATUS_SUCCESSFUL) {
                DownloadUtils.removeDownload(downloadHistory.id, manager);
            }
            deleteDownloadHistories(downloadHistory);
            int idx = list.indexOf(downloadHistory);
            list.remove(downloadHistory);
            hashMap.remove(downloadHistory.id);
            if (idx >= 0) {
                adapter.notifyItemRemoved(idx);
            }
        }
    }

    void addFront(DownloadHistory downloadHistory) {
        list.add(0, downloadHistory);
        adapter.notifyItemInserted(0);
    }

    void addAllEnd(Collection<DownloadHistory> downloadHistories) {
        int idx = list.size();
        int count = downloadHistories.size();
        list.addAll(downloadHistories);
        adapter.notifyItemRangeInserted(idx, count);
    }

    void insertDownloadHistories(DownloadHistory... downloadHistories) {
        service.submit(() -> downloadDao.insert(downloadHistories));
    }

    void updateDownloadHistories(DownloadHistory... downloadHistories) {
        service.submit(() -> downloadDao.update(downloadHistories));
    }

    void deleteDownloadHistories(DownloadHistory... downloadHistories) {
        service.submit(() -> downloadDao.delete(downloadHistories));
    }

    List<DownloadHistory> getDownloadHistories() {
        return downloadDao.getDownloadHistories();
    }

    @Override
    public void pause(DownloadHistory downloadHistory) {
        pauseDownload(downloadHistory.id);
    }

    @Override
    public void resume(DownloadHistory downloadHistory) {
        resumeDownload(downloadHistory.id);
    }

    @Override
    public void cancel(DownloadHistory downloadHistory) {
        cancelDownload(downloadHistory);
    }

    @Override
    public void delete(DownloadHistory downloadHistory) {
        deleteDownload(downloadHistory);
    }

    @Override
    public void remove(DownloadHistory downloadHistory) {
        removeDownload(downloadHistory);
    }
}
