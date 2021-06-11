package com.example.browserapp;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.browserapp.databinding.ActivityMainBinding;
import com.example.browserapp.web.BrowserWebViewClient;
import com.github.lzyzsd.jsbridge.BridgeWebView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    public static final int REQUEST_CODE_HISTORY = 0;
    public static final int REQUEST_CODE_LABEL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        BridgeWebView webView = binding.webView;
        webView.setWebViewClient(new BrowserWebViewClient(webView));
        //binding.webView.loadUrl("https://wwww.baidu.com/");
        webView.loadUrl("file:///android_asset/index.html");
        webView.setWebChromeClient(new WebChromeClient());

        //开启webView的LocalStorage功能，使H5可以保存数据
        webView.getSettings().setDomStorageEnabled(true);


        binding.historyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivityForResult(historyIntent, REQUEST_CODE_HISTORY);
            }
        });

        binding.labelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent labelIntent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivityForResult(labelIntent, REQUEST_CODE_LABEL);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.webView.canGoBack()) {
            binding.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_HISTORY:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    String resultUrl = data.getStringExtra("url");
                    binding.webView.loadUrl(resultUrl);
                }
        }
    }
}