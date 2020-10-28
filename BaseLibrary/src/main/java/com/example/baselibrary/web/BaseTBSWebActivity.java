package com.example.baselibrary.web;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.smtt.export.external.interfaces.IX5WebSettings;

public abstract class BaseTBSWebActivity extends AppCompatActivity {

    public WebView mWebView;
    protected WebSettings mWebSettings;

    public void setWebSettings(){
        if (mWebView == null){
            mWebView = new WebView(this);
        }
        if (setWebViewContainer() == null){
            return;
        }
        /**
         * 这就需要在写xml布局的时候，需要一个写viewgroup包裹webview
         */
        setWebViewContainer().addView(mWebView);
        mWebSettings = mWebView.getSettings();
        // 支持屏幕缩放
        mWebSettings.setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setBuiltInZoomControls(true);
        // 不显示webview缩放按钮
        mWebSettings.setDisplayZoomControls(false);
        // 设置自适应屏幕宽度
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        // 设置缓存模式
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 允许android调用javascript
        mWebSettings.setDomStorageEnabled(true);
        // 设置WebView对象的编码格式为UTF_8
        mWebSettings.setDefaultTextEncodingName("utf-8");
        // 解决图片不显示
        mWebSettings.setBlockNetworkImage(false);
        // 支持自动加载图片
        mWebSettings.setLoadsImagesAutomatically(true);
        // 设置不用系统浏览器打开,直接显示在当前WebView
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    public void loadUrl(String url) {
        // WebView加载web资源
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            // 再次打开页面时，若界面没有消亡，会导致进度条不显示并且界面崩溃
            mWebView.stopLoading();
            mWebView.onPause();
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroyDrawingCache();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    parent.removeView(mWebView);
                }
                mWebView.removeAllViews();
                mWebView.destroy();
            } else {
                mWebView.removeAllViews();
                mWebView.destroy();
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    parent.removeView(mWebView);
                }
            }
            mWebView = null;
        }
        super.onDestroy();
    }

    protected abstract ViewGroup setWebViewContainer();
}
