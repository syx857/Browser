package com.tech.ui.bookmark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.tech.ContainerActivity;
import com.tech.MyActivityResultContract;
import com.tech.R;
import com.tech.databinding.FragmentBookmarkEditBinding;
import com.tech.domain.Bookmark;
import com.tech.viewmodel.BookmarkViewModel;

public class BookmarkEditFragment extends Fragment implements ContainerActivity.FragmentInterface{
    FragmentBookmarkEditBinding binding;
    Bookmark bookmark;
    BookmarkViewModel bookmarkViewModel;
    ActivityResultLauncher<Integer> launcher;
    NavController navController;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentBookmarkEditBinding.inflate(getLayoutInflater());

        initToolBar();
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        bookmark = (Bookmark)getArguments().getSerializable("bookmark");
        if (bookmark != null) {
            binding.editBookmarkTitle.setText(bookmark.title);
            binding.editBookmarkUrl.setText(bookmark.url);
        }
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = Navigation.findNavController(binding.getRoot());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.menu_complete_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navController.navigate(R.id.bookmarkFragment);
                return true;
            case R.id.complete_edit:
                Bookmark newBookmark;
                String newUrl = binding.editBookmarkUrl.getText().toString();
                String newTitle = binding.editBookmarkTitle.getText().toString();
                newBookmark = new Bookmark(newUrl, newTitle, sharedPreferences.getString("phoneNumber", ""));
                bookmarkViewModel.update(bookmark, newBookmark);
                navController.navigate(R.id.bookmarkFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        Toolbar toolbar = binding.bookmarkEditToolbar;
        AppCompatActivity activity = (AppCompatActivity)requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("编辑书签");
    }

    @Override
    public boolean onBackPressed() {
        //navController.popBackStack();
        navController.navigate(R.id.bookmarkFragment);
        return true;
    }
}
