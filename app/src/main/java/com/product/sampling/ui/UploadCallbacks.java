package com.product.sampling.ui;

import java.util.LinkedList;
import java.util.List;

public interface UploadCallbacks {
    void onProgressUpdate(int percentage);

    void onError();

    void onFinish();


}
