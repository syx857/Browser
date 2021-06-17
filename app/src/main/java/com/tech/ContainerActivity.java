package com.tech;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.tech.databinding.ActivityContainerBinding;

public class ContainerActivity extends AppCompatActivity {
    ActivityContainerBinding binding;
    NavController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        controller = Navigation.findNavController(this, R.id.container);
        Intent intent = getIntent();
        int dest = intent.getIntExtra("dest", -1);
        navigation(dest);
    }

    public void navigation(int id) {
        if (id == R.id.bookmarkFragment) {
            controller.navigate(id);
            return;
        }
        if (id == R.id.historyFragment) {
            controller.navigate(id);
            return;
        }
        if (id == R.id.settingFragment) {
            controller.navigate(id);
            return;
        }
        finish();
    }
}
