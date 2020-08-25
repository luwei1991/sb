package com.product.sampling.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.List;

import static com.product.sampling.utils.FileTypeUtil.getMIMEType;


/**
 * Created by 陆伟 on 2020/5/8.
 * Copyright (c) 2020 . All rights reserved.
 */


public class FileShareUtil {

    public static void openFile(Context context, String path){

        try {
            context.startActivity(getWordFileIntent(context,path));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }



    }

    private static Intent getWordFileIntent(Context context, String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0以上共享文件，分享路径定义在xml/file_paths.xml
            uri = FileProvider.getUriForFile(context, "com.product.sampling.fileprovider", file);
        } else {
            // 7.0以下,共享文件
            uri = Uri.fromFile(file);
        }
        String type = getMIMEType(file);
        if(type.contains("pdf") || type.contains("x-xls") || type.contains("msword") || type.contains("vnd.openxmlformats-officedocument.wordprocessingml.document") || type.contains("vnd.openxmlformats-officedocument.spreadsheetml.sheet")|| type.contains("text/html")){
            if (checkWps(context)) {
                intent.setClassName("cn.wps.moffice_eng",
                        "cn.wps.moffice.documentmanager.PreStartActivity2");
                List<ResolveInfo> resInfoList = context.getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                intent.setDataAndType(uri,type);
            } else {
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(uri, type);
            }
        }else{
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(uri, type);
        }
        return intent;
    }
    private static boolean checkWps(Context context){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");//WPS个人版的包名
        if (intent == null) {
            if(context instanceof Activity){
                Snackbar.make(((Activity)context).getWindow().getDecorView(),"您没有安装WPS", Snackbar.LENGTH_SHORT).show();
            }

            return false;
        } else {
            return true;
        }
    }


}
