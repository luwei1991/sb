package com.product.sampling.httpmoudle.interfaces;

/**
 *
 * 创建时间：2018/5/26
 * 描述：
 */
public interface IRequestCallback {
    abstract void onSuccess(String response);
    abstract void onFailure(Throwable throwable);
}
