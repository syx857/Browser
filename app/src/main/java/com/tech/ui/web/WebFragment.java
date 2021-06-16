package com.tech.ui.web;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tech.MainViewModel;
import com.tech.client.MyWebChromeClient;
import com.tech.client.MyWebViewClient;
import com.tech.databinding.FragmentWebBinding;
import com.tech.model.WebFragmentToken;
import com.tech.utils.WebViewUtils;

import org.jetbrains.annotations.NotNull;

public class WebFragment extends Fragment implements MyWebViewClient.Callback, MyWebChromeClient.Callback {
    public static final String TAG = "WebFragment";
    public static final String HOME = "file:///android_asset/home.html";

    FragmentWebBinding binding;
    WebViewModel webViewModel;
    MainViewModel mainViewModel;
    WebFragmentToken token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        webViewModel = new ViewModelProvider(this).get(WebViewModel.class);
        binding = FragmentWebBinding.inflate(inflater, container, false);

        WebViewUtils.initialWebView(binding.webView);
        binding.webView.setWebViewClient(new MyWebViewClient(this));
        binding.webView.setWebChromeClient(new MyWebChromeClient(this));

        if (webViewModel.getResultMsg() != null) {
            Message resultMsg = webViewModel.getResultMsg();
            WebView.WebViewTransport webViewTransport = (WebView.WebViewTransport) resultMsg.obj;
            webViewTransport.setWebView(binding.webView);
            webViewModel.setResultMsg(null);
            resultMsg.sendToTarget();
            webViewModel.setBundle(new Bundle());
            Log.d(TAG, "onCreateView: is transfer");
        } else if (webViewModel.getBundle() == null && webViewModel.getResultMsg() == null) {
            binding.webView.loadUrl(HOME);
            webViewModel.setBundle(new Bundle());
            Log.d(TAG, "onCreateView: is new page");
        } else {
            binding.webView.restoreState(webViewModel.getBundle());
            Log.d(TAG, "onCreateView: is restore");
        }

        token = webViewModel.getToken();
        Log.d(TAG, "onCreateView: get dependency token: " + token);

        return binding.getRoot();
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
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
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

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        setTitle(url);
        Log.d(TAG, "onPageStarted: url: " + url);
        if (url.equals("data:text/html; charset=UTF-8,")) {
            setUrl("");
            setFavicon(null);
        }
        if (url.equals(HOME)) {
            setUrl("");
            setFavicon(null);
            return;
        } else {
            setUrl(url);
        }
        setFavicon(favicon);
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
