package com.tech;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.tech.databinding.ActivityContainerBinding;

import java.util.List;

public class ContainerActivity extends AppCompatActivity {
    ActivityContainerBinding binding;
    NavController controller;
    NavHostFragment hostFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.container);
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
        if (id == R.id.passwordLoginFragment) {
            controller.navigate(id);
            return;
        }
        finish();
    }

    public Fragment getCurrentFragment() {
        if (hostFragment != null) {
            List<Fragment> list = hostFragment.getChildFragmentManager().getFragments();
            for (Fragment fragment : list) {
                if (fragment.isVisible()) {
                    return fragment;
                }
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof FragmentInterface) {
            FragmentInterface fi = (FragmentInterface) fragment;
            if (fi.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    public interface FragmentInterface {
        boolean onBackPressed();
    }
}
