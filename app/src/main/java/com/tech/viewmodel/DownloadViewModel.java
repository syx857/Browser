package com.tech.viewmodel;

import android.app.Application;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.tech.adapter.DownloadHistoryAdapter;
import com.tech.repository.DownloadRepository;

public class DownloadViewModel extends AndroidViewModel {
    DownloadRepository repository;

    public DownloadViewModel(@NonNull Application application) {
        super(application);
        repository = DownloadRepository.getInstance(application);
    }

    public DownloadHistoryAdapter getAdapter() {
        return repository.getAdapter();
    }

    public Uri getFileUri(long id) {
        return repository.getFileUri(id);
    }
}
