package com.tech.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tech.databinding.FragmentUsercenterBinding;

public class UserCenterFragment extends Fragment {

    FragmentUsercenterBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentUsercenterBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }


}
