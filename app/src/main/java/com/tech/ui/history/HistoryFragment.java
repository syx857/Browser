package com.tech.ui.history;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.tech.ContainerActivity;
import com.tech.R;
import com.tech.adapter.HistoryAdapter;
import com.tech.databinding.FragmentHistoryBinding;
import com.tech.domain.History;
import com.tech.domain.User;
import com.tech.utils.Const;
import com.tech.viewmodel.HistoryViewModel;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements AdapterView.OnItemClickListener,
        ContainerActivity.FragmentInterface {

    FragmentHistoryBinding binding;
    HistoryViewModel viewModel;
    List<History> historyList;
    HistoryAdapter adapter;
    SparseBooleanArray checkedStateMap = new SparseBooleanArray();
    Boolean multiChoice = false;
    List<History> checkedHistory = new ArrayList<>();
    boolean isSearching = false;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(getLayoutInflater());
        initToolBar();
        sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);

        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false) && !sharedPreferences.getBoolean(Const.LOAD_HISTORY, false)) {
            User user = new User(sharedPreferences.getString(Const.PHONE_NUMBER, ""));
            viewModel.loadHistoryListFromRemote(user);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Const.LOAD_HISTORY, true);
            editor.apply();
        }

        viewModel.getHistoryList().observe(getViewLifecycleOwner(), histories -> {
            if(!isSearching) {
                historyList = histories;
                if (adapter == null) {
                    adapter = new HistoryAdapter(getContext(), R.layout.history_item, histories);
                    binding.historyListView.setAdapter(adapter);
                }
                adapter.setHistoryList(histories);
            }
        });

        binding.historyListView.setOnItemClickListener(this);
        registerForContextMenu(binding.historyListView);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.history_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_history:
                Toast.makeText(getContext(), "删除历史", Toast.LENGTH_SHORT).show();
                viewModel.deleteHistory(historyList.get(info.position));
                break;
            case R.id.copy_history_url:
                ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
                String url = historyList.get(info.position).historyUrl;
                ClipData data = ClipData.newPlainText("history_url", url);
                clipboardManager.setPrimaryClip(data);
                Toast.makeText(getContext(), "复制链接："+url, Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear_history:
                viewModel.deleteAll();
                break;
            case R.id.multi_choice_history:
                multiChoice = true;
                adapter.setShowCheckbox(true);
                setCheck(info.position);
                binding.multiHistoryDelete.setVisibility(View.VISIBLE);
                binding.multiHistoryDelete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.deleteHistory(
                                checkedHistory.toArray(new History[checkedHistory.size()]));
                        quitMultiChoice();
                    }
                });
                break;
        }
        return true;
    }

    private void initToolBar() {
        Toolbar toolbar = binding.historyToolbar;
        AppCompatActivity activity = (AppCompatActivity)requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("历史记录");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("搜索历史记录");
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
                viewModel.searchHistory(newText).observe(requireActivity(), histories -> {
                    adapter.setHistoryList(histories);
                    historyList = histories;
                });
                return true;
            }
        });
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
            History history = historyList.get(position);
            Intent intent = new Intent();
            intent.putExtra("string", history.historyUrl);
            requireActivity().setResult(Activity.RESULT_OK, intent);
            requireActivity().finish();
        }
    }

    public void setCheck(int position) {
        if (!checkedStateMap.get(position)) {
            checkedStateMap.put(position, true);
            checkedHistory.add(historyList.get(position));
            adapter.setChecked(true, position);
        } else {
            checkedStateMap.put(position, false);
            checkedHistory.remove(historyList.get(position));
            adapter.setChecked(false, position);
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

    public void quitMultiChoice() {
        multiChoice = false;
        adapter.setShowCheckbox(false);
        adapter.clearCheckedState();
        binding.multiHistoryDelete.setVisibility(View.GONE);
        checkedHistory.clear();
        checkedStateMap.clear();
    }
}
