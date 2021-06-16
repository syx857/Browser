package com.tech.ui.setting;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.tech.R;

public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_setting, rootKey);
    }
}
