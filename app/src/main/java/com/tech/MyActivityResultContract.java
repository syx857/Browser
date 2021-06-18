package com.tech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyActivityResultContract extends ActivityResultContract<Integer, String> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Integer input) {
        Intent intent = new Intent(context, ContainerActivity.class);
        intent.putExtra("dest", input);
        return intent;
    }

    @Override
    public String parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return intent.getStringExtra("string");
        }
        return "";
    }
}
