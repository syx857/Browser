package com.example.browserapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.browserapp.domain.History;
import com.example.browserapp.repository.HistoryRepository;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    HistoryRepository historyRepository = new HistoryRepository(getApplication());

    public HistoryViewModel(@NonNull Application application) {
        super(application);
    }

    public void addHistory(History history) {
        historyRepository.addHistory(history);
    }

    public void deleteHistory(History... history) {
        historyRepository.deleteHistory(history);
    }


    public LiveData<List<History>> findHistoryByTitle(String title) {
        return historyRepository.findByTitle(title);
    }

    public LiveData<List<History>> getHistoryList() {
        return historyRepository.getHistoryList();
    }

    public LiveData<List<History>> searchHistory(String keyWord) {
        return historyRepository.searchHistory(keyWord);
    }

    public void deleteAll() {
        historyRepository.deleteAll();
    }
}
