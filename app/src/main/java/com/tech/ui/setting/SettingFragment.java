package com.tech.ui.setting;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.tech.R;
import com.tech.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {
    public static final String TAG = "SettingFragment";
    FragmentSettingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        binding.appBar.back.setOnClickListener(this::onClick);
        return binding.getRoot();
    }

    public void onClick(View v) {
        if (v == binding.appBar.back) {
            requireActivity().setResult(Activity.RESULT_CANCELED);
            requireActivity().finish();
        }
    }

    public static class ContentFragment extends PreferenceFragmentCompat {
        public static final String TAG = "ContentFragment";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Log.d(TAG, "onCreatePreferences: " + rootKey);
            setPreferencesFromResource(R.xml.preference_setting, rootKey);
        }
    }
}
