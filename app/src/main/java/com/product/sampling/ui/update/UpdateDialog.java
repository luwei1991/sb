package com.product.sampling.ui.update;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.FileProvider;

import com.product.sampling.BuildConfig;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.bean.UpdateEntity;
import com.product.sampling.databinding.DialogUpdateBinding;
import com.product.sampling.net.download.DownLoadListener;
import com.product.sampling.net.download.DownLoadUtils;
import com.product.sampling.ui.ActivityManager;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.io.File;

/**
 * Created by 陆伟 on 2020/4/14.
 * Copyright (c) 2020 . All rights reserved.
 */


public class UpdateDialog extends QMUIDialog {
    private DialogUpdateBinding mBinding;
    private Context context;
    private static final String APK_NAME = "release.apk";

    public UpdateDialog(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogUpdateBinding.bind(LayoutInflater.from(context).inflate(R.layout.dialog_update, null));
        setContentView(mBinding.getRoot());
        setCancelable(false);//取消消失
        mBinding.progress.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
            @Override
            public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                if(value == maxValue){
                    return "安装";
                }
                return 100 * value / maxValue + "%";
            }
        });
    }

    public void downLoadApk(UpdateEntity updateEntity) {
        mBinding.remark.setText(updateEntity.getRemark());
        String downLoadUrl = Constants.IMAGE_DOWNBASE_URL + updateEntity.getAppfileid();
        DownLoadUtils.download(downLoadUrl, Constants.PDF_PATH + "hnmccs" + ".apk", new DownLoadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {
                mBinding.progress.setProgress(progress);
            }

            @Override
            public void onFinish(String path) {
                install(path);
                ActivityManager.getInstance().currentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.progress.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                         install(path);
                    }
                });
                    }
                });


            }

            @Override
            public void onFail(String errorInfo) {


            }
        });

    }

    private void install(String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(filePath);
            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            //安装权限被禁止 则通知栏提示点击安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", new File(filePath));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        }

    }
}
