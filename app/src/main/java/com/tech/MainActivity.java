package com.tech;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import com.tech.databinding.ActivityMainBinding;
import com.tech.domain.Bookmark;
import com.tech.model.WebFragmentToken;
import com.tech.ui.web.WebFragment;
import com.tech.utils.Const;
import com.tech.utils.PhotoUtils;
import com.tech.view.MenuPopup;
import com.tech.view.PagePopup;
import com.tech.viewmodel.BookmarkViewModel;
import com.tech.viewmodel.HistoryViewModel;
import com.yalantis.ucrop.UCrop;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements TextWatcher,
        PagePopup.PagePopupCallback {

    public static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_SAVE_PICTURE = 0x11;

    MainViewModel viewModel;
    BookmarkViewModel bookmarkViewModel;
    ActivityMainBinding binding;
    InputMethodManager inputMethodManager;
    PagePopup pagePopup;
    MenuPopup menuPopup;
    WebFragmentToken webFragmentToken;
    SharedPreferences sharedPreferences;
    HistoryViewModel historyViewModel;
    Bitmap bitmap;

    ActivityResultLauncher<Integer> launcher = registerForActivityResult(
            new MyActivityResultContract(), this::onActivityResult);

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (menuPopup != null) {
                if (key.equals(Const.LOGIN_STATE)) {
                    menuPopup.setLogin(sharedPreferences.getBoolean(key, false));
                }
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        viewModel.setFragmentManager(getSupportFragmentManager());
        viewModel.getProgress().observe(this, this::setProgressBar);
        viewModel.getCurrentToken().observe(this, this::setToken);
        viewModel.getTypeIn().observe(this, this::typeIn);
        viewModel.getCount().observe(this, this::setTotalPage);

        if (viewModel.isEmpty()) {
            viewModel.addFragment();
        }
        binding.appBar.editText.addTextChangedListener(this);
        binding.appBar.editText.setOnFocusChangeListener(this::onFocusChange);
        binding.getRoot().setOnTouchListener(this::onTouch);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 绘制页数
     *
     * @param num 页数
     */
    public void setTotalPage(int num) {
        Drawable drawable = ResourcesCompat
                .getDrawable(getResources(), R.drawable.ic_baseline_crop_square_24, null);
        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.setTint(Color.rgb(117, 117, 117));
        drawable.draw(canvas);
        String str;
        if (num < 100) {
            str = "" + num;
        } else {
            str = "99+";
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(117, 117, 117));
        float scale = getResources().getDisplayMetrics().density;
        paint.setTextSize((int) (8 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(str, x, y, paint);
        binding.navigationBar.page.setImageBitmap(bitmap);
    }

    /**
     * 设置网页加载进度
     *
     * @param progress 进度
     */
    public void setProgressBar(int progress) {
        if (progress < 0 || progress >= 100) {
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setProgress(progress);
        }
    }

    /**
     * 设置建议行动
     *
     * @param s 输入的文字
     */
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

    /**
     * 获取Token更新视图
     *
     * @param token 当前页面的Token
     */
    public void setToken(WebFragmentToken token) {
        webFragmentToken = token;
        if (token != null) {
            binding.appBar.editText.setText(token.url);
        } else {
            binding.appBar.editText.setText("");
        }
    }

    public boolean goBackward() {
        return viewModel.goBackward();
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
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
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
            doPagePopup();
        }
        if (v == binding.navigationBar.menu) {
            doMenuPopup();
        }
        if (v == binding.suggestView.suggestSearch.getRoot()) {
            String prefix = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("search engine", "https://www.baidu.com/s?ie=UTF-8&wd=");
            loadUrl(prefix + Uri.encode(binding.appBar.editText.getText().toString()));
        }
        if (v == binding.suggestView.suggestUrl.getRoot()) {
            loadUrl(binding.appBar.editText.getText().toString());
        }
    }

    public void doPagePopup() {
        if (pagePopup == null) {
            pagePopup = new PagePopup(this, this, viewModel.getAdapter());
        }
        int[] location = new int[2];
        binding.navigationBar.getRoot().getLocationOnScreen(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getDisplay().getRealMetrics(displayMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        pagePopup.setMaxHeight(binding.container.getHeight());
        pagePopup.showAtLocation(binding.getRoot(), Gravity.BOTTOM, location[0],
                displayMetrics.heightPixels - location[1]);
    }

    public void doMenuPopup() {
        if (menuPopup == null) {
            menuPopup = new MenuPopup(this, this::menuClick);
        }
        int[] location = new int[2];
        binding.navigationBar.getRoot().getLocationOnScreen(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getDisplay().getRealMetrics(displayMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        menuPopup.showAtLocation(binding.getRoot(), Gravity.BOTTOM, location[0],
                displayMetrics.heightPixels - location[1]);
    }

    public void menuClick(int id) {
        switch(id) {
            case R.id.nav_add_bookmark:
                if (!TextUtils.isEmpty(webFragmentToken.url) && !TextUtils
                        .isEmpty(webFragmentToken.title)) {
                    Bookmark bookmark = new Bookmark(webFragmentToken.url, webFragmentToken.title,
                            sharedPreferences.getString(Const.PHONE_NUMBER, "anonymous"));
                    bookmarkViewModel.addBookmark(bookmark);
                    menuPopup.dismiss();
                    Toast.makeText(getApplicationContext(), "已添加至书签", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_bookmark:
                menuPopup.dismiss();
                toContainerActivity(R.id.bookmarkFragment);
                break;
            case R.id.nav_history:
                menuPopup.dismiss();
                toContainerActivity(R.id.historyFragment);
                break;
            case R.id.nav_setting:
                menuPopup.dismiss();
                toContainerActivity(R.id.settingFragment);
                break;
            case R.id.nav_login:
                menuPopup.dismiss();
                if (sharedPreferences.getBoolean(Const.LOGIN_STATE, false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示").setMessage("您已经登录，是否退出登录？");
                    builder.setPositiveButton("确定", (dialog, which) -> {
                        exitLogin();
                        dialog.dismiss();
                    });
                    builder.setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    builder.setCancelable(true);
                    builder.create().show();
                } else {
                    toContainerActivity(R.id.passwordLoginFragment);
                }
                break;
            default:
                break;

        }
    }

    public void exitLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Const.LOGIN_STATE, false);
        editor.putString(Const.PHONE_NUMBER, null);
        editor.putBoolean(Const.LOAD_HISTORY, false);
        editor.putBoolean(Const.LOAD_BOOKMARK, false);
        editor.apply();
        //bookmarkViewModel.deleteAll();
        //historyViewModel.deleteAll();
    }

    @Override
    public void onBackPressed() {
        if (binding.appBar.editText.isFocused()) {
            clearEditTextFocus();
        } else {
            if (!goBackward()) {
                if (viewModel.getSize() == 1) {
                    viewModel.removeFragment(viewModel.getCurrentTokenValue());
                    finish();
                } else {
                    viewModel.removeFragment(viewModel.getCurrentTokenValue());
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        viewModel.setTypeIn(s.toString());
    }

    /**
     * 根据地址栏Focus事件决定建议视图显示和消失
     *
     * @param view     地址栏
     * @param hasFocus 是否锁定
     */
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            binding.suggestView.getRoot().setVisibility(View.VISIBLE);
        } else {
            binding.suggestView.getRoot().setVisibility(View.GONE);
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (v != binding.appBar.editText && event.getAction() == MotionEvent.ACTION_DOWN
                && binding.appBar.editText.isFocused()) {
            Log.d(TAG, "onTouch: clear edit text focus");
            clearEditTextFocus();
            return true;
        }
        return false;
    }

    /**
     * 跳转到功能界面，设置、书签、下载等
     *
     * @param id 导航id，在/res/navigation/navigation_container.xml注册
     */
    public void toContainerActivity(int id) {
        launcher.launch(id);
    }

    @Override
    public void addWebFragment() {
        viewModel.addFragment();
        pagePopup.dismiss();
        pagePopup = null;
    }

    @Override
    public void removeWebFragment(WebFragmentToken token) {
        if (viewModel.getSize() == 1) {
            viewModel.removeFragment(token);
            pagePopup.dismiss();
            pagePopup = null;
        } else {
            viewModel.removeFragment(token);
        }
    }

    @Override
    public void showWebFragment(WebFragmentToken token) {
        viewModel.showFragment(token);
        pagePopup.dismiss();
        pagePopup = null;
    }

    /**
     * 从ContainerActivity返回的url
     *
     * @param result url
     */
    public void onActivityResult(String result) {
        if (result.length() > 0) {
            viewModel.loadUrl(result);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null && resultUri.getScheme().equals("file")) {
                        copyFileToDownloads(resultUri);
                    } else {
                        Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    Toast.makeText(this, cropError.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SAVE_PICTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (bitmap != null) {
                        PhotoUtils.saveImage(bitmap, this, this);
                    }
                } else {
                    Toast.makeText(this, "没有获得权限，无法保存图片", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void copyFileToDownloads(Uri croppedFileUri) {
        bitmap = null;
        try {
            bitmap = BitmapFactory
                    .decodeStream(getContentResolver().openInputStream(croppedFileUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            if (verifyPermissions()) {
                PhotoUtils.saveImage(bitmap, this, this);
            }
        }
    }

    public boolean verifyPermissions() {
        int permissionExternalMemory = ContextCompat
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS,
                    MY_PERMISSIONS_REQUEST_SAVE_PICTURE);
            return false;
        }
        return true;
    }
}