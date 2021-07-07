package com.tech.ui.blacklist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.tech.ContainerActivity;
import com.tech.R;
import com.tech.databinding.FragmentBlacklistBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class BlacklistFragment extends Fragment implements ContainerActivity.FragmentInterface {
    FragmentBlacklistBinding binding;
    NavController controller;
    SharedPreferences preferences;
    HashSet<String> hashSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBlacklistBinding.inflate(inflater, container, false);
        binding.appBar.back.setOnClickListener(this::onClick);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Set<String> set = preferences.getStringSet("blacklist", null);
        if (set != null) {
            hashSet = new HashSet<>(set);
        } else {
            hashSet = new HashSet<>();
        }
        show();
        controller = Navigation.findNavController(requireActivity(), R.id.container);
        binding.confirmBlackDomain.setOnClickListener(this::onClick);
        return binding.getRoot();
    }

    public void onClick(View v) {
        if (v == binding.appBar.back) {
            controller.navigateUp();
        } else if (v == binding.confirmBlackDomain) {
            save();
            show();
        }
    }

    public void show() {
        StringBuilder builder = new StringBuilder();
        hashSet.iterator().forEachRemaining(s -> {
            builder.append(s);
            builder.append("\n");
        });
        binding.blackList.setText(builder.toString());
    }

    public void save() {
        try {
            String string = binding.edit.getText().toString().trim();
            BufferedReader reader = new BufferedReader(new StringReader(string));
            String[] strings = reader.lines().toArray(String[]::new);
            reader.close();
            for (String s : strings) {
                String temp = s.trim();
                if (temp.length() > 0) {
                    hashSet.add(s);
                }
            }
            preferences.edit().putStringSet("blacklist", hashSet).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //save();
    }

    @Override
    public boolean onBackPressed() {
        controller.navigateUp();
        return true;
    }
}
