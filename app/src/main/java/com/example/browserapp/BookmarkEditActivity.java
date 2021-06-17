package com.example.browserapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.example.browserapp.databinding.ActivityBookmarkEditBinding;
import com.example.browserapp.domain.Bookmark;
import com.example.browserapp.viewmodel.BookmarkViewModel;

public class BookmarkEditActivity extends AppCompatActivity {

    ActivityBookmarkEditBinding binding;
    Bookmark bookmark;
    BookmarkViewModel bookmarkViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBookmarkEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolBar();

        bookmark = (Bookmark) getIntent().getSerializableExtra("bookmark");
        if (bookmark != null) {
            binding.editBookmarkTitle.setText(bookmark.title);
            binding.editBookmarkUrl.setText(bookmark.url);
        }

        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.complete_edit:
                String newUrl = binding.editBookmarkUrl.getText().toString();
                String newTitle = binding.editBookmarkTitle.getText().toString();
                bookmarkViewModel.update(bookmark.id, newUrl, newTitle);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        Toolbar toolbar = binding.bookmarkEditToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("编辑书签");
    }
}