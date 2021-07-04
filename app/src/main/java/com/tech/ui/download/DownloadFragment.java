package com.tech.ui.download;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.ContainerActivity;
import com.tech.adapter.DownloadHistoryAdapter;
import com.tech.databinding.FragmentDownloadBinding;
import com.tech.viewmodel.DownloadViewModel;

public class DownloadFragment extends Fragment implements ContainerActivity.FragmentInterface {
    public static final String TAG = "DownloadFragment";
    FragmentDownloadBinding binding;
    DownloadViewModel viewModel;
    DownloadHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(DownloadViewModel.class);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        adapter = viewModel.getAdapter();
        adapter.setContext(requireContext());
        binding.recycler.setAdapter(viewModel.getAdapter());
        binding.recycler.setLayoutManager(layoutManager);
        binding.appBar.back.setOnClickListener(this::onClick);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.setContext(null);
    }

    public void onClick(View v) {
        if (v == binding.appBar.back) {
            requireActivity().setResult(Activity.RESULT_CANCELED);
            requireActivity().finish();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
