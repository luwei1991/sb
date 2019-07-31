package com.product.sampling.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.product.sampling.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * WebView Activity gdy
 * 2018-11-01 16:47:18
 */
public class WebViewActivity extends BaseActivity {

    String url;

    WebView mJWebView;
    ImageView mImageView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        setTitle("");
        initView();
        addEvent();
    }

    public void initView() {
        mJWebView = findViewById(R.id.web_view);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mJWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mJWebView.getSettings().setBlockNetworkImage(false);
        Intent intent = getIntent();
        url = intent.getStringExtra("extra_url");
        String title = intent.getStringExtra("extra_title");
        if (title != null && title.trim().length() > 0) {
            tvTitle.setText(title);
        }
        WebSettings webSettings = mJWebView.getSettings();

        /*与js交互*/
        webSettings.setJavaScriptEnabled(true);

        /*自适应屏幕*/
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        /*细节操作*/
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持js弹窗
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 让网页的内容呈单列显示
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH); // 加速显示图片


        mJWebView.setWebViewClient(new WebViewClient());

    }

    public void addEvent() {
//        mJWebView.setWebListener(
//                new JWebView.WebViewListener() {
//
//                    @Override
//                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                    }
//
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//
//                    }
//
//
//                    @Override
//                    public void onProgressChanged(WebView view, int newProgress) {
//
//                    }
//
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        return false;
//                    }
//                }
//        );
        loadUrl();
    }

    private void loadUrl() {
        try {
            mJWebView.loadDataWithBaseURL("", url, "text/html", "utf-8", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void startWebView(Activity activity, String url, String name) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("extra_url", url);
        intent.putExtra("extra_title", name);
        activity.startActivity(intent);
    }


}
