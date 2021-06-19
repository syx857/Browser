package com.tech.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tech.domain.Bookmark;
import com.tech.repository.BookmarkRepository;
import java.util.List;

public class BookmarkViewModel extends AndroidViewModel {

    BookmarkRepository bookmarkRepository = new BookmarkRepository(getApplication());

    public BookmarkViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Bookmark>> searchBookmark(String key) {
        return bookmarkRepository.searchBookmark(key);
    }

    public LiveData<List<Bookmark>> getBookmarkList() {
        return bookmarkRepository.getBookmarkList();
    }

    public void deleteBookmark(Bookmark... bookmark) {
        bookmarkRepository.deleteBookmark(bookmark);
    }

    public void deleteAll() {
        bookmarkRepository.deleteAll();
    }

    public void addBookmark(Bookmark bookmark) {
        bookmarkRepository.addBookmark(bookmark);
    }

    public void update(int id, String url, String title) {
        bookmarkRepository.update(id, url, title);
    }
}
