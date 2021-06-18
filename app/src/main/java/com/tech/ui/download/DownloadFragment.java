package com.tech.ui.download;

import androidx.fragment.app.Fragment;

import com.tech.ContainerActivity;

public class DownloadFragment extends Fragment implements ContainerActivity.FragmentInterface {
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
