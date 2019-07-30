package com.product.sampling.view;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.product.sampling.R;


/**
 * Created by gdy on 2018-11-01 08:13:51.
 */
public class JWebChromeClient extends WebChromeClient {
    private final Context mContext;
    private JWebView.WebViewListener listener;
    private ProgressBar progressBar;

    private WebView mWebView;

    public JWebChromeClient(JWebView.WebViewListener listener, Context context) {
        this.listener = listener;
        this.mContext = context;

        View inflate = LayoutInflater.from(mContext).inflate(R.layout.progress_horizontal, null);
        progressBar = inflate.findViewById(R.id.progressBar);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
//        if (mContext instanceof BaseActivity){
//            ((BaseActivity) mContext).setTitle(title);
//        }
    }

    // 设置网页加载的进度条
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        View view0 = view.getChildAt(0);
        if (listener != null) {
//            listener.onProgressChanged(view, newProgress);
        }

        if (null==view0&&progressBar!=null){
            view.addView(progressBar,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5));
            progressBar.setProgress(newProgress);
        }
        if (progressBar!=null){
            progressBar.setProgress(newProgress);
        }
        if (newProgress==100){
            view.removeViewAt(0);
        }

    }



    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        WebView newWebView = new WebView(view.getContext());
        newWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 在此处进行跳转URL的处理, 一般情况下_black需要重新打开一个页面, 这里我直接让当前的webview重新load了url
                mWebView.loadUrl(url);
                return true;
            }

        });
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();
        return true;
    }

    public void setmWebView(WebView mWebView) {
        this.mWebView = mWebView;
    }
}
