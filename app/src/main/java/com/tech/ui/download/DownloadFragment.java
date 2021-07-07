package com.tech.ui.download;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.ContainerActivity;
import com.tech.adapter.DownloadHistoryAdapter;
import com.tech.databinding.FragmentDownloadBinding;
import com.tech.domain.DownloadHistory;
import com.tech.viewmodel.DownloadViewModel;
import java.io.File;

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
        adapter.setListener(this::onClick);
        return binding.getRoot();
    }

    public void onClick(DownloadHistory downloadHistory) {
        if (downloadHistory.status == DownloadManager.STATUS_SUCCESSFUL) {
            File file = new File(viewModel.getFileUri(downloadHistory.id).getPath());
            Uri uri = FileProvider.getUriForFile(requireContext(), "com.tech.fileprovider", file);
            Log.d(TAG, "onClick: uri " + uri);
            if (uri != null) {
                String mime = downloadHistory.mime;
                if (mime == null || mime.length() == 0) {
                    mime = "*/*";
                }
                openFile(uri, mime);
            }
        }
    }

    public void openFile(Uri uri, String mime) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mime);
        requireContext().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.setContext(null);
        adapter.setListener(null);
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
