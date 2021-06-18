package com.tech.ui.bookmark;

import androidx.fragment.app.Fragment;

import com.tech.ContainerActivity;

public class BookmarkFragment extends Fragment implements ContainerActivity.FragmentInterface {
    @Override
    public boolean onBackPressed() {
        return false;
    }
    //TODO 书签
}
