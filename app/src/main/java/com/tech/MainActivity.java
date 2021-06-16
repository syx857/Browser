package com.tech;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.tech.databinding.ActivityMainBinding;
import com.tech.model.WebFragmentToken;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TextWatcher {
    public static final String TAG = "MainActivity";

    MainViewModel viewModel;
    ActivityMainBinding binding;
    InputMethodManager inputMethodManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        viewModel.setFragmentManager(getSupportFragmentManager());
        viewModel.getProgress().observe(this, this::setProgressBar);
        viewModel.getCurrentToken().observe(this, this::setToken);
        viewModel.getTypeIn().observe(this, this::typeIn);
        viewModel.getList().observe(this, this::setTotalPage);

        if (viewModel.isEmpty()) {
            viewModel.addFragment();
        }
        binding.appBar.editText.addTextChangedListener(this);
        binding.appBar.editText.setOnFocusChangeListener(this::onFocusChange);
        binding.getRoot().setOnTouchListener(this::onTouch);
    }

    public void setTotalPage(List<WebFragmentToken> list) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_crop_square_24, null);
        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.setTint(Color.rgb(117,117,117));
        drawable.draw(canvas);
        String str;
        if(list.size() < 100) {
            str = "" + list.size() ;
        }
        else {
            str = "99+";
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(117, 117, 117));
        float scale = getResources().getDisplayMetrics().density;
        paint.setTextSize((int) (8 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;
        canvas.drawText(str, x, y, paint);
        binding.navigationBar.page.setImageBitmap(bitmap);
    }

    public void setProgressBar(int progress) {
        if (progress < 0 || progress >= 100) {
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setProgress(progress);
        }
    }

    public void typeIn(String s) {
        if (s.length() == 0) {
            binding.suggestView.suggestUrl.getRoot().setVisibility(View.GONE);
            binding.suggestView.suggestSearch.getRoot().setVisibility(View.GONE);
        } else {
            binding.suggestView.suggestUrl.getRoot().setVisibility(View.VISIBLE);
            binding.suggestView.suggestSearch.getRoot().setVisibility(View.VISIBLE);
        }
        binding.suggestView.suggestUrl.title.setText(s);
        binding.suggestView.suggestUrl.url.setText(s);
        binding.suggestView.suggestSearch.search.setText(s);
    }

    public void setToken(WebFragmentToken token) {
        if (token != null) {
            binding.appBar.editText.setText(token.url);
        } else {
            binding.appBar.editText.setText("");
        }
    }

    public void goBackward() {
        if (!viewModel.goBackward()) {
            viewModel.removeFragment(viewModel.getCurrentTokenValue());
            if(viewModel.getSize() == 1) {
                finish();
            }
        }
    }

    public void goForward() {
        viewModel.goForward();
    }

    public void clearEditTextFocus() {
        binding.appBar.editText.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void loadUrl(String url) {
        clearEditTextFocus();
        viewModel.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.clearFragmentManager();
    }

    public void onClick(View v) {
        if (v == binding.appBar.refresh) {
            clearEditTextFocus();
            viewModel.refresh();
        }
        if (v == binding.navigationBar.home) {
            clearEditTextFocus();
            viewModel.goHome();
        }
        if (v == binding.navigationBar.forward) {
            goForward();
        }
        if (v == binding.navigationBar.backward) {
            goBackward();
        }
        if (v == binding.navigationBar.page) {
            //TODO popupWindow
        }
        if (v == binding.navigationBar.menu) {
            //TODO popupWindow
        }
        if (v == binding.suggestView.suggestSearch.getRoot()) {
            loadUrl("https://www.baidu.com/s?ie=UTF-8&wd=" + Uri.encode(binding.appBar.editText.getText().toString()));
        }
        if (v == binding.suggestView.suggestUrl.getRoot()) {
            loadUrl(binding.appBar.editText.getText().toString());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.appBar.editText.isFocused()) {
                clearEditTextFocus();
            } else {
                goBackward();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        viewModel.setTypeIn(s.toString());
    }

    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            binding.suggestView.getRoot().setVisibility(View.VISIBLE);
        } else {
            binding.suggestView.getRoot().setVisibility(View.GONE);
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (v != binding.appBar.editText && event.getAction() == MotionEvent.ACTION_DOWN && binding.appBar.editText.isFocused()) {
            Log.d(TAG, "onTouch: clear edit text focus");
            clearEditTextFocus();
            return true;
        }
        return false;
    }
}