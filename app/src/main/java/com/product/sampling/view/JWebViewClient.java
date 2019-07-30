package com.product.sampling.view;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by gdy on 2018-11-01 08:14:22
 */
public class JWebViewClient extends WebViewClient {

    private JWebView.WebViewListener listener;
    public JWebViewClient(JWebView.WebViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (listener != null)
            listener.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (listener != null)
            listener.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean isShould = listener.shouldOverrideUrlLoading(view,url);
        if (isShould){
            return true;
        }else{
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handleSslError(view, handler, error);
    }

    private void handleSslError(WebView view, SslErrorHandler handler, SslError error) {
        /*Context context = view.getContext();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        String message = context.getString(R.string.ssl_error_message_default);
        int errorType = error.getPrimaryError();
        switch (errorType) {
            case SslError.SSL_UNTRUSTED:
                message = context.getString(R.string.ssl_error_message_untrusted);
                break;
            case SslError.SSL_EXPIRED:
                message = context.getString(R.string.ssl_error_message_expired);
                break;
            case SslError.SSL_IDMISMATCH:
                message = context.getString(R.string.ssl_error_message_id_mismatch);
                break;
            case SslError.SSL_NOTYETVALID:
                message = context.getString(R.string.ssl_error_message_not_valid);
                break;
        }
        message += " " + context.getString(R.string.ssl_error_wether_continue);
        dialogBuilder.setTitle(R.string.ssl_error_title);
        dialogBuilder.setMessage(message);

        dialogBuilder.setPositiveButton(R.string.app_string_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();*/
    }
}
