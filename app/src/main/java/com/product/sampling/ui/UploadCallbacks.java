package com.product.sampling.ui;

public interface UploadCallbacks {
    void onProgressUpdate(int percentage);

    void onError();

    void onFinish();


}
