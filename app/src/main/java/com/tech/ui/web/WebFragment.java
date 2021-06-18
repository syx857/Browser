package com.tech.ui.web;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.tech.MainViewModel;
import com.tech.client.MyWebChromeClient;
import com.tech.client.MyWebViewClient;
import com.tech.databinding.FragmentWebBinding;
import com.tech.model.WebFragmentToken;
import com.tech.utils.Const;
import com.tech.utils.WebViewUtils;

public class WebFragment extends Fragment implements MyWebViewClient.Callback, MyWebChromeClient.Callback {
    public static final String TAG = "WebFragment";
    public static final String HOME = "file:///android_asset/test.html";
    public static final int SEARCH = 0x33;

    /**
     * for full screen
     */
    View overlay;
    int orientation;
    int visibility;
    WebChromeClient.CustomViewCallback callback;
    /**
     * for full screen
     */

    FragmentWebBinding binding;
    WebViewModel webViewModel;
    MainViewModel mainViewModel;
    WebFragmentToken token;
    Handler handler = new Handler(Looper.getMainLooper(), msg -> {
        if (msg.what == SEARCH) {
            String s = (String) msg.obj;
            String prefix = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("search engine", "https://www.baidu.com/s?ie=UTF-8&wd=");
            loadUrl(prefix + Uri.encode(s));
            return true;
        }
        return false;
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        webViewModel = new ViewModelProvider(this).get(WebViewModel.class);
        binding = FragmentWebBinding.inflate(inflater, container, false);

        WebViewUtils.initialWebView(binding.webView);
        binding.webView.setWebViewClient(new MyWebViewClient(this));
        binding.webView.setWebChromeClient(new MyWebChromeClient(this));
        binding.webView.setDownloadListener(this::onDownloadStart);
        //binding.webView.setOnCreateContextMenuListener(this::webViewOnCreateContextMenu);
        token = webViewModel.getToken();
        load();
        initial();
        Log.d(TAG, "onCreateView: get dependency token: " + token);
        return binding.getRoot();
    }

    /**
     * WebView 长按菜单
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        if (v == binding.webView) {
            WebView.HitTestResult hitTestResult = binding.webView.getHitTestResult();
            String str = hitTestResult.getExtra();
            switch (hitTestResult.getType()) {
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: SRC_ANCHOR_TYPE " + str);
                    //requireActivity().getMenuInflater().inflate();
                    //TODO is url
                    return;
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: SRC_IMAGE_ANCHOR_TYPE " + str);
                    //TODO is url
                    return;
                case WebView.HitTestResult.PHONE_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: PHONE_TYPE " + str);
                    return;
                case WebView.HitTestResult.EMAIL_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: EMAIL_TYPE " + str);
                    return;
                case WebView.HitTestResult.EDIT_TEXT_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: EDIT_TEXT_TYPE " + str);
                    return;
                case WebView.HitTestResult.GEO_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: GEO_TYPE " + str);
                    return;
                case WebView.HitTestResult.IMAGE_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: IMAGE_TYPE " + str);
                    return;
                case WebView.HitTestResult.UNKNOWN_TYPE:
                    Log.d(TAG, "webViewOnCreateContextMenu: UNKNOWN_TYPE " + str);
                    return;
                default:
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Log.d(TAG, "onDownloadStart: url: " + url + " userAgent: " + userAgent + " contentDisposition: " + " mimetype: " + mimetype + contentDisposition + " contentLength: " + contentLength);
    }

    void load() {
        if (webViewModel.getResultMsg() != null) {
            Message resultMsg = webViewModel.getResultMsg();
            WebView.WebViewTransport webViewTransport = (WebView.WebViewTransport) resultMsg.obj;
            webViewTransport.setWebView(binding.webView);
            resultMsg.sendToTarget();
            webViewModel.setResultMsg(null);
            return;
        }
        if (webViewModel.getBundle() != null) {
            binding.webView.restoreState(webViewModel.getBundle());
            return;
        }
        binding.webView.loadUrl(HOME);
    }

    void initial() {
        if (webViewModel.getBundle() == null) {
            webViewModel.setBundle(new Bundle());
        }
        binding.webView.addJavascriptInterface(this, "_TECH_BROWSER_");
    }

    @JavascriptInterface
    public void homeSearch(String text) {
        Message msg = Message.obtain();
        msg.what = SEARCH;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.webView.onPause();
        mainViewModel.setProgress(101);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.webView.saveState(webViewModel.getBundle());
    }

    @Override
    public void onProgressChanged(int progress) {
        mainViewModel.setProgress(progress);
    }

    @Override
    public void onCreateWindow(boolean isDialog, boolean isUserGesture, Message resultMsg) {
        mainViewModel.addFragment(resultMsg);
    }

    @Override
    public void onReceivedTitle(String title) {
        setTitle(title);
    }

    @Override
    public void onReceivedIcon(Bitmap icon) {
        Log.d(TAG, "onReceivedIcon: ");
        setFavicon(icon);
    }

    /**
     * 全屏模式
     *
     * @param callback 退出全屏回调
     */
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (overlay != null) {
            this.callback.onCustomViewHidden();
            return;
        }
        this.callback = callback;
        //TODO full screen
        //view.setFitsSystemWindows(true);
        overlay = view;
        orientation = requireActivity().getRequestedOrientation();
        visibility = requireActivity().getWindow().getDecorView().getSystemUiVisibility();
        ((FrameLayout) requireActivity().getWindow().getDecorView()).addView(view,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        requireActivity().getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                requireActivity().getWindow().getDecorView().setSystemUiVisibility(Const.FULLSCREEN_SYS_UI);
            }
        });
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(Const.FULLSCREEN_SYS_UI);
        //requireActivity().getWindow().getW
    }

    /**
     * 退出全屏模式
     */
    @Override
    public void onHideCustomView() {
        //TODO cancel full screen
        if (overlay == null) {
            return;
        }
        ((FrameLayout) requireActivity().getWindow().getDecorView()).removeView(overlay);
        overlay = null;
        requireActivity().setRequestedOrientation(orientation);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(visibility);
        callback.onCustomViewHidden();
        callback = null;
    }

    @Override
    public boolean onJsAlert(String url, String message, JsResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("此网页显示").setMessage(message);
        builder.setPositiveButton("确定", (dialog, which) -> {
            result.confirm();
            dialog.dismiss();
        });
        builder.setCancelable(true);
        builder.setOnCancelListener(dialog -> result.cancel());
        builder.create().show();
        return true;
    }

    @Override
    public boolean onJsConfirm(String url, String message, JsResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                result.confirm();
            }
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                result.cancel();
            }
            dialog.dismiss();
        };
        builder.setTitle("此网页显示").setMessage(message);
        builder.setPositiveButton("确定", listener).setNegativeButton("取消", listener);
        builder.setCancelable(false);
        builder.create().show();
        return true;
    }

    @Override
    public boolean onJsPrompt(String url, String message, String defaultValue, JsPromptResult result) {
        EditText input = new EditText(requireActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(defaultValue);
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                result.confirm(input.getText().toString());
            }
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                result.cancel();
            }
            dialog.dismiss();
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(input);
        builder.setTitle("此网页显示").setMessage(message);
        builder.setPositiveButton("确定", listener).setNegativeButton("取消", listener);
        builder.setCancelable(false);
        builder.create().show();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onHideCustomView();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void fullscreen() {
        Window window = requireActivity().getWindow();
        View decorView = window.getDecorView();
        WindowInsets windowInsets = decorView.getRootWindowInsets();
        decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                return null;
            }
        });
        WindowInsetsController insetsController = window.getInsetsController();
        insetsController.hide(WindowInsets.Type.navigationBars());
        insetsController.hide(WindowInsets.Type.statusBars());

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.d(TAG, "onPageStarted: url: " + url);
        if (url.equals("data:text/html; charset=UTF-8,")) {
            setUrl("");
            setTitle("about:blank");
            setFavicon(null);
        }
        if (url.equals(HOME)) {
            setUrl("");
            setTitle("首页");
            setFavicon(null);
        } else {
            setTitle(url);
            setUrl(url);
            setFavicon(favicon);
        }
    }

    @Override
    public void onPageFinished(String url) {
        mainViewModel.setProgress(101);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String scheme = request.getUrl().getScheme();
        return !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https");

    }

    public void setTitle(String title) {
        if (token != null) {
            token.title = title;
            mainViewModel.notifyTokenChanged(token);
        }
    }

    public void setUrl(String url) {
        if (token != null) {
            token.url = url;
            mainViewModel.notifyTokenChanged(token);
        }
    }

    public void setFavicon(Bitmap ico) {
        if (token != null) {
            token.favicon = ico;
            mainViewModel.notifyTokenChanged(token);
        }
    }

    public void loadUrl(String url) {
        Log.d(TAG, "loadUrl: " + url);
        binding.webView.loadUrl(url);
    }

    public boolean goBackward() {
        if (binding.webView.canGoBack()) {
            Log.d(TAG, "goBackward: canGoBack");
            binding.webView.goBack();
            return true;
        }
        return false;
    }

    public void refresh() {
        binding.webView.reload();
    }

    public void goForward() {
        if (binding.webView.canGoForward()) {
            binding.webView.goForward();
            Log.d(TAG, "goForward: canGoForward");
        }
    }

    public void goHome() {
        binding.webView.stopLoading();
        binding.webView.loadUrl(HOME);
        binding.webView.clearHistory();
        if (token != null) {
            token.title = "首页";
            token.favicon = null;
            token.url = "";
            mainViewModel.notifyTokenChanged(token);
        }
    }

    public void close() {
        WebViewUtils.destroyWebView(binding.webView);
    }
}
