package com.tech.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tech.domain.History;
import com.tech.domain.User;
import com.tech.repository.HistoryRepository;
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

    public LiveData<List<History>> getHistoryList() {
        return historyRepository.getHistoryList();
    }

    public LiveData<List<History>> searchHistory(String keyWord) {
        return historyRepository.searchHistory(keyWord);
    }

    public void deleteAll() {
        historyRepository.deleteAll();
    }

    public void loadHistoryListFromRemote(User user) {
        historyRepository.loadHistoryListFromRemote(user);
    }
}
