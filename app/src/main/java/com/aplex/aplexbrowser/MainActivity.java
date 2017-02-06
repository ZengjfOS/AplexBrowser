package com.aplex.aplexbrowser;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends Activity {
    @InjectView(R.id.wv)
    public WebView mWebView;

    @InjectView(R.id.dialog_btn)
    public TextView mShowDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // User settings
        WebSettings webSettings = mWebView.getSettings();
        //设置支持javaScript
        webSettings.setJavaScriptEnabled(true);
        //设置js打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //设置WebView使用广泛的视窗
        webSettings.setUseWideViewPort(true);//关键点

        webSettings.setDisplayZoomControls(false);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 设置显示缩放按钮
        webSettings.setBuiltInZoomControls(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        //设置WebView 可以加载更多格式页面
        webSettings.setLoadWithOverviewMode(true);

        //根据手机密度设置ZoomDensity
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        Log.d("MainActivity", "densityDpi = " + mDensity);
        if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if(mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else if (mDensity == DisplayMetrics.DENSITY_TV){
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else{
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }

        /**
        * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
        * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
        */
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.loadUrl("http://www.aplex.com.tw");
        //自己设置client 防止弹出自带浏览器
        mWebView.setWebViewClient(new AplexWebViewClient());

    }

    @OnClick(R.id.dialog_btn)
    public void showUrlBar(View view){
        final UrlBarDialog dialog = new UrlBarDialog(this);
        dialog.setOnUrlBarRequestListener(new UrlBarDialog.UrlBarRequetListener() {
            @Override
            public void request(String url) {
                Log.e("error",url);
                mWebView.loadUrl(url);
            }
        });
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            mWebView.goBack();
            return true;
        }
        return false;
    }
    private class AplexWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
