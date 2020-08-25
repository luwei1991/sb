package com.product.sampling.thread;

public interface AsyncCallBack<T> {

   void onSuccess(T t);
   void onFailed(Throwable throwable);
}
