package com.product.sampling.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;

public class DownloadProgressHandler implements DownloadCallBack{
    public static final int DOWNLOAD_SUCCESS = 0;
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_FAIL = 2;

    protected ResponseHandler mHandler = new ResponseHandler(this, Looper.getMainLooper());

    /**
     * 发送消息，更新进度
     *
     * @param what
     * @param downloadInfo
     */
    public void sendMessage(int what, DownloadInfo downloadInfo) {
        mHandler.obtainMessage(what, downloadInfo).sendToTarget();
    }


    /**
     * 处理消息
     * @param message
     */
    protected void handleMessage(Message message) {
        DownloadInfo progressBean = (DownloadInfo) message.obj;
        switch (message.what) {
            case DOWNLOAD_SUCCESS://下载成功
                onCompleted(progressBean.getFile());
                removeMessage();
                break;
            case DOWNLOAD_PROGRESS://下载中
                onProgress(progressBean.getProgress(), progressBean.getFileSize(),progressBean.getSpeed());
                break;
            case DOWNLOAD_FAIL://下载失败
                onError(progressBean.getErrorMsg());
                break;
            default:
                removeMessage();
                break;
        }
    }

    private void removeMessage() {
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onProgress(int progress, long total, long speed) {

    }

    @Override
    public void onCompleted(File file) {

    }

    @Override
    public void onError(Throwable e) {

    }


    protected static class ResponseHandler extends Handler {

        private DownloadProgressHandler mProgressHandler;

        public ResponseHandler(DownloadProgressHandler mProgressHandler, Looper looper) {
            super(looper);
            this.mProgressHandler = mProgressHandler;
        }

        @Override
        public void handleMessage(Message msg) {
            mProgressHandler.handleMessage(msg);
        }
    }
}
