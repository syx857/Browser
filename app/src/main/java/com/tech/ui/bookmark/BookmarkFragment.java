package com.tech.ui.bookmark;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnCloseListener;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.tech.ContainerActivity;
import com.tech.R;
import com.tech.adapter.BookmarkAdapter;
import com.tech.databinding.FragmentBookmarkBinding;
import com.tech.domain.Bookmark;
import com.tech.domain.User;
import com.tech.viewmodel.BookmarkViewModel;
import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment implements AdapterView.OnItemClickListener,
        ContainerActivity.FragmentInterface {

    FragmentBookmarkBinding binding;
    BookmarkViewModel viewModel;
    BookmarkAdapter adapter;
    List<Bookmark> bookmarkList;
    SparseBooleanArray checkedStateMap = new SparseBooleanArray();
    Boolean multiChoice = false;
    List<Bookmark> checkedBookmark = new ArrayList<>();
    boolean isSearching = false;
    NavController navController;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentBookmarkBinding.inflate(getLayoutInflater());
        initToolBar();
        sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);

        viewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        if (!sharedPreferences.getBoolean("loadBookmark", false)) {
            User user = new User(sharedPreferences.getString("phoneNumber", ""));
            viewModel.loadBookmarkListFromRemote(user);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("loadBookmark", true);
            editor.apply();
        }
        viewModel.getBookmarkList().observe(getViewLifecycleOwner(), bookmarks -> {
            if (!isSearching) {
                bookmarkList = bookmarks;
                if (adapter == null) {
                    adapter = new BookmarkAdapter(getContext(), R.layout.bookmark_item, bookmarks);
                    binding.bookmarkListView.setAdapter(adapter);
                }
                adapter.setBookmarkList(bookmarks);
            }
        });

        binding.bookmarkListView.setOnItemClickListener(this);
        registerForContextMenu(binding.bookmarkListView);
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = Navigation.findNavController(binding.getRoot());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.bookmark_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_bookmark:
                Toast.makeText(getContext(), "删除书签", Toast.LENGTH_SHORT).show();
                viewModel.deleteBookmark(bookmarkList.get(info.position));
                break;
            case R.id.copy_bookmark_url:
                ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
                String url = bookmarkList.get(info.position).url;
                ClipData data = ClipData.newPlainText("bookmark_url", url);
                clipboardManager.setPrimaryClip(data);
                Toast.makeText(getContext(), "复制链接："+url, Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear_bookmark:
                viewModel.deleteAll();
                break;
            case R.id.multi_choice_bookmark:
                multiChoice = true;
                adapter.setShowCheckbox(true);
                setCheck(info.position);
                adapter.notifyDataSetChanged();
                binding.multiBookmarkDelete.setVisibility(View.VISIBLE);

                binding.multiBookmarkDelete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.deleteBookmark(
                                checkedBookmark.toArray(new Bookmark[checkedBookmark.size()]));
                        quitMultiChoice();
                    }
                });
                break;
            case R.id.edit_bookmark:
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookmark", bookmarkList.get(info.position));
                navController.navigate(R.id.bookmarkEditFragment, bundle);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("搜索书签");
        searchView.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearching = false;
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearching = true;
                viewModel.searchBookmark(newText).observe(requireActivity(), bookmarks -> {
                    adapter.setBookmarkList(bookmarks);
                    bookmarkList = bookmarks;
                });
                return true;
            }
        });
    }

    private void initToolBar() {
        Toolbar toolbar = binding.bookmarkToolbar;
        AppCompatActivity activity = (AppCompatActivity)requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("书签");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (multiChoice) {
            setCheck(position);
        } else {
            Bookmark bookmark = bookmarkList.get(position);
            Intent intent = new Intent();
            intent.putExtra("string", bookmark.url);
            requireActivity().setResult(Activity.RESULT_OK, intent);
            requireActivity().finish();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (multiChoice) {
            quitMultiChoice();
            return true;
        }
        return false;
    }

    private void setCheck(int position) {
        if (!checkedStateMap.get(position)) {
            checkedStateMap.put(position, true);
            checkedBookmark.add(bookmarkList.get(position));
            adapter.setChecked(true, position);
        } else {
            checkedStateMap.put(position, false);
            checkedBookmark.remove(bookmarkList.get(position));
            adapter.setChecked(false, position);
        }
    }

    private void quitMultiChoice() {
        multiChoice = false;
        adapter.setShowCheckbox(false);
        adapter.clearCheckedState();
        binding.multiBookmarkDelete.setVisibility(View.GONE);
        checkedBookmark.clear();
        checkedStateMap.clear();
    }
}
