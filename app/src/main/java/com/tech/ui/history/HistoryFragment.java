package com.tech.ui.history;

import androidx.fragment.app.Fragment;

import com.tech.ContainerActivity;

public class HistoryFragment extends Fragment implements ContainerActivity.FragmentInterface {
    @Override
    public boolean onBackPressed() {
        return false;
    }
    //TODO 历史页面

}
