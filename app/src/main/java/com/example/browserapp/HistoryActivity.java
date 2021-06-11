package com.example.browserapp;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnCloseListener;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.example.browserapp.adapter.HistoryAdapter;
import com.example.browserapp.databinding.ActivityHistoryBinding;
import com.example.browserapp.domain.History;
import com.example.browserapp.viewmodel.HistoryViewModel;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ActivityHistoryBinding binding;
    HistoryViewModel viewModel;
    List<History> historyList;
    HistoryAdapter adapter;
    SparseBooleanArray checkedStateMap = new SparseBooleanArray();
    Boolean multiChoice = false;
    List<History> checkedHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initToolBar();

        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        viewModel.getHistoryList().observe(this, histories -> {
            historyList = histories;
            if(adapter == null) {
                adapter = new HistoryAdapter(this, R.layout.history_item, histories);
                binding.historyListView.setAdapter(adapter);
            }
            adapter.setHistoryList(histories);
        });

        binding.historyListView.setOnItemClickListener(this);

        registerForContextMenu(binding.historyListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_history:
                Toast.makeText(getApplicationContext(),"删除历史",Toast.LENGTH_SHORT).show();
                viewModel.deleteHistory(historyList.get(info.position));
                return true;
            case R.id.copy_history_url:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("history_url", historyList.get(info.position).historyUrl);
                clipboardManager.setPrimaryClip(data);
                return true;
            case R.id.clear_history:
                viewModel.deleteAll();
            case R.id.multi_choice_history:
                multiChoice = true;
                adapter.setShowCheckbox(true);

                setCheck(info.position);

                binding.multiHistoryDelete.setVisibility(View.VISIBLE);

                binding.multiHistoryDelete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.deleteHistory(checkedHistory.toArray(new History[checkedHistory.size()]));
                        quitMultiChoice();
                    }
                });
                //binding.historyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        }
        return super.onContextItemSelected(item);
    }

    private void initToolBar() {
        Toolbar toolbar = binding.historyToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("历史记录");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_history).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("搜索历史记录");
        searchView.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HistoryActivity.this,query,Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.searchHistory(newText).observe(HistoryActivity.this, histories -> {
                    adapter.setHistoryList(histories);
                    historyList = histories;
                });
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
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
            intent.putExtra("url", history.historyUrl);
            setResult(RESULT_OK, intent);
            finish();
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
    public void onBackPressed() {
        if (multiChoice) {
            quitMultiChoice();
            return;
        }
        super.onBackPressed();
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