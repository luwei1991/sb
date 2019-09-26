package com.product.sampling.ui.update;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.product.sampling.BuildConfig;
import com.product.sampling.R;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * NewRcApp
 * author:wangqiong on 2018/5/28.
 * mail:wqandroid@gmail.com
 */
public class ApkDownLoadService extends Service {

    public static final String DO_WHAT = "do_what";

    public static final String ACTION_DOWNLOAD = "DOWNLOAD";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private ApkDownloadTaskInfo taskInfo;
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);

    }

    protected void onHandleIntent(@Nullable Intent intent) {

        if (!intent.hasExtra(DO_WHAT)) return;

        String action = intent.getStringExtra(DO_WHAT);

        if (action.equals(ACTION_DOWNLOAD)) {
            //执行下载任务
            if (taskInfo == null || downloadId == 0) {
                taskInfo = (ApkDownloadTaskInfo) intent.getSerializableExtra("taskInfo");

                if(taskInfo.apkVer==null){
                    downloadFile(taskInfo);
                }else{
                    downloadApk(taskInfo);
                }
                initNotification();
            } else {
                //多次启动到下载service
                if (notification == null || notificationManager == null) {
                    initNotification();
                }
            }
        } else {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }


    }

    private long downloadId = 0;
    private DownloadManager downloadManager;
    private Disposable disposable;


    public void downloadApk(ApkDownloadTaskInfo taskInfo) {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(taskInfo.apkUrl));
        request.setTitle("下载");
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.allowScanningByMediaScanner();
        request.setMimeType("application/vnd.android.package-archive");
        //下载完成之后显示系统的点击安装
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        File file = new File(taskInfo.apkLocalPath);
        if (file.exists()) {
            file.delete();
        }
        request.setDestinationUri(Uri.fromFile(file));
        downloadId = downloadManager.enqueue(request);
        sendApkUpdate();
    }

    public void downloadFile(ApkDownloadTaskInfo taskInfo) {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(taskInfo.apkUrl));
        request.setTitle("下载");
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.allowScanningByMediaScanner();
/*         request.setMimeType("application/vnd.android.package-archive");*/
         if("doc".equals(taskInfo.fileType)){
             request.setMimeType("application/msword");
         }else if("docx".equals(taskInfo.fileType)){
             request.setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
         }else if("pdf".equals(taskInfo.fileType)){
             request.setMimeType("application/pdf");
         }else if("rar".equals(taskInfo.fileType)){
             request.setMimeType("application/octet-stream");
         }else if("zip".equals(taskInfo.fileType)){
             request.setMimeType("application/x-zip-compressed");
         }else if("xls".equals(taskInfo.fileType)){
             request.setMimeType("application/vnd.ms-excel application/x-excel");

         }else if("xlsx".equals(taskInfo.fileType)){
             request.setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

         }
        //下载完成之后显示系统的点击安装
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        //创建目录
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        File file = new File(taskInfo.apkLocalPath);
        if (file.exists()) {
            file.delete();
        }
        request.setDestinationUri(Uri.fromFile(file));
        downloadId = downloadManager.enqueue(request);
        Toast.makeText(this, "下载成功，请去实用工具文件夹下的下载处查看", Toast.LENGTH_SHORT).show();

    }


    /**
     * 每个人一秒钟去查询下载状态
     */
    public void sendApkUpdate() {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(a -> getBytesAndStatus(downloadId))
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(ints -> ints[2] == DownloadManager.STATUS_SUCCESSFUL || ints[2] == DownloadManager.STATUS_FAILED)
                .subscribe(new Observer<int[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(int[] ints) {
                        updateNowStatus(ints);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    private void updateNowStatus(int[] ints) {
        if (ints == null || ints.length != 3) return;
        if (ints[0] == 0 || ints[1] == 0) return;

        Log.e("download", ints[2] + "_now" + ints[0] + "_max" + ints[1]);

        int status = ints[2];


        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            //点击通知安装

//            String filePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+taskInfo.getApkFileName();
            String filePath = taskInfo.apkLocalPath;
            //直接安装更新
            try {
                install(filePath);
                //正常安装清空通知栏
                notificationManager.cancelAll();
            } catch (Exception e) {
                e.printStackTrace();
                //安装权限被禁止 则通知栏提示点击安装
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri apkUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(filePath));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
                notification = builder.setContentIntent(pi)
                        .setContentText("下载成功")
                        .setProgress(0, 0, false)
                        .build();
                notificationManager.notify(1, notification);
            }
            endDownloadTask();
        } else if (status == DownloadManager.STATUS_FAILED) {
            //下载失败
            Intent intent = webLauncher(taskInfo.getH5DownPage());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notification = builder.setContentIntent(pi)
                    .setContentText("下载失败")
                    .build();
            notificationManager.notify(1, notification);
            endDownloadTask();
        } else if (status == DownloadManager.STATUS_RUNNING) {
            //下载中
            int progress = ints[0] * 100 / ints[1];
            builder.setProgress(100, progress, false);
            builder.setContentTitle("安装文件");
            builder.setContentText("下载进度" + progress + "%");
            notification = builder.build();
            notificationManager.notify(1, notification);
        }

    }


    public void endDownloadTask() {
        downloadId = 0;
        //停止查询下载状态
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        //并关闭自己
        stopSelf();
    }


    public int[] getBytesAndStatus(long downloadId) {

        int[] bytesAndStatus = new int[]{-1, -1, 0};
        if (downloadId == 0 || downloadManager == null) return bytesAndStatus;
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                //当前已经下载字节
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) / 1000;
                // 总字节
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)) / 1000;

                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return bytesAndStatus;
    }


    //初始化通知
    private void initNotification() {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//适配8.0,自行查看8.0的通知，主要是NotificationChannel

            NotificationChannel chan1 = new NotificationChannel("channel_1",
                    "Primary Channel", NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(chan1);
            builder = new NotificationCompat.Builder(this, "channel_1");
        } else {
            builder = new NotificationCompat.Builder(this, null);
        }

        builder.setContentTitle("更新App")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)) //设置通知的大图标
                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(false)//设置通知被点击一次是否自动取消
                .setContentText("更新进度1%")
                .setProgress(100, 0, false);

        notification = builder.build();//构建通知对象
    }


    private static Intent webLauncher(String downloadUrl) {
        Uri download = Uri.parse(downloadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, download);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    private void install(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }


}
