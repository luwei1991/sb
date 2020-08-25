package com.product.sampling.ui.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import org.devio.takephoto.app.TakePhotoFragment;

/**
 * 创建时间：2018/5/28
 * 描述：
 */
public class BaseFragment extends TakePhotoFragment {

    /**
     * fragment 不可见调用
     */
    public void disVisible() {
    }

    /**
     * fragment 可见调用
     */
    public void inVisiable() {
    }


    /**
     * 显示进度框
     */
    ProgressDialog progDialog;

    public void showProgressDialog(String title) {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(title);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    public void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    public void showSimpleDialog(String title, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity()).setTitle(title)
                .setPositiveButton("确定", listener).setNegativeButton("取消", null).show();
    }

    public void showToast(String toast) {
        com.product.sampling.maputil.ToastUtil.show(getActivity(), toast);
    }

    public void showShortToast(String toast) {
        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), toast);
    }


}
