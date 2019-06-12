package com.product.sampling.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;
import com.product.sampling.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.product.sampling.adapter.TaskSampleRecyclerViewAdapter.RequestCodePdf;
import static com.product.sampling.ui.WebViewActivity.Intent_Order;

/**
 * 样品信息
 */
public class TaskSampleFragment extends BasePhotoFragment implements View.OnClickListener {
    public static final String TAG = TaskSampleFragment.class.getSimpleName();

    public static final int Select_Check = 101;
    public static final int Select_Handle = 102;

    public int selectId = -1;


    RecyclerView mRecyclerView;
    TaskDetailViewModel taskDetailViewModel;
    private static TaskSampleFragment fragment;
    TaskSampleRecyclerViewAdapter adapter;

    public TaskSampleFragment() {
    }

    public static TaskSampleFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putParcelable("task", task);

        if (fragment == null) {
            fragment = new TaskSampleFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample_detail, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView = rootView.findViewById(R.id.item_image_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.btn_save).setOnClickListener(this);
        rootView.findViewById(R.id.btn_save_upload).setOnClickListener(this);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List list, boolean isLocalData) {
        adapter = new TaskSampleRecyclerViewAdapter(R.layout.item_sample_list_content, list, this, isLocalData);
        adapter.addHeaderView(getHeaderView(), 0);
        adapter.addHeaderView(getAddView(), 1);
        recyclerView.setAdapter(adapter);
    }

    private View getHeaderView() {
        View view = View.inflate(getContext(), R.layout.layout_task_step, null);
        TextView tv = view.findViewById(R.id.tv_step_2);
        tv.setBackgroundResource(R.drawable.bg_circle_blue);

        TextView tvStep = view.findViewById(R.id.tv_step_3);
        tvStep.setBackgroundResource(R.drawable.bg_circle_blue);

        return view;
    }

    private View getAddView() {
        View view = View.inflate(getContext(), R.layout.layout_task_add, null);
        view.findViewById(R.id.tv_create).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_create) {
            EditText et = new EditText(getActivity());
            new AlertDialog.Builder(getActivity()).setTitle("请输入样品名称")
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //按下确定键后的事件
                            String text = et.getText().toString();
                            createNewSample(text);
                        }
                    }).setNegativeButton("取消", null).show();


        } else if (v.getId() == R.id.btn_save) {
            saveSampleInFile();
        } else if (R.id.btn_save_upload == v.getId()) {
            postSceneData();
            postSampleData();
        }
    }

    private void saveSampleInFile() {
//        for (int i = 0; i < taskDetailViewModel.taskEntity.taskSamples.size(); i++) {
//            if (!taskDetailViewModel.taskEntity.taskSamples.get(i).isLocalData) continue;
//            File file = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).disposalfile);
//            if (file.exists()) {
//            }
//            File fileHandle = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).samplingfile);
//            if (fileHandle.exists()) {
//            }
//        }
//        if (!taskDetailViewModel.taskEntity.isLoadLocalData) return;
        if (!taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
            saveTaskInLocalFile(false);
        }
    }

    void createNewSample(String text) {
        if (null != text && !text.isEmpty()) {
            TaskSample sample = new TaskSample();
            sample.setRemarks(text);
            sample.list = new ArrayList<>();
            sample.isLocalData = true;
            taskDetailViewModel.taskEntity.taskSamples.add(sample);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);

        if (taskDetailViewModel.taskEntity.isLoadLocalData) {
            findTaskInLocalFile();
            setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, true);
        } else {
            taskDetailViewModel.requestTasksamples(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
        }

        taskDetailViewModel.sampleDetailLiveData.observe(this, new Observer<LoadDataModel<List<TaskSample>>>() {
            @Override
            public void onChanged(LoadDataModel<List<TaskSample>> taskEntityLoadDataModel) {
                if (taskEntityLoadDataModel.isSuccess()) {
                    taskDetailViewModel.taskEntity.taskSamples = taskEntityLoadDataModel.getData();
                    setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, false);
                }
            }
        });
    }

    private void postSampleData() {
        showLoadingDialog();
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("taskid", taskDetailViewModel.taskEntity.id)
                .addFormDataPart("id", System.currentTimeMillis() + "");
        if (null == taskDetailViewModel.taskEntity.taskSamples || taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
            ToastUtil.showToast(getActivity(), "请创建样品数据");
            return;
        }
        for (int i = 0; i < taskDetailViewModel.taskEntity.taskSamples.size(); i++) {

            if (!taskDetailViewModel.taskEntity.taskSamples.get(i).isLocalData) continue;
            //TODO:是否是最后一个
//            if (i == taskDetailViewModel.taskEntity.taskSamples.size() - 1) {
//                multipartBodyBuilder.addFormDataPart("islastone", "1");
//            }
            {
                File samplingfile = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).samplingfile);
                if (samplingfile.exists()) {
                    Log.e("file", samplingfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, samplingfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("samplingfile", samplingfile.getName(), requestFile);//抽样单
                }
            }
            {
                File samplingpicfile = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).samplingpicfile);
                if (samplingpicfile.exists()) {
                    Log.e("file", samplingpicfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, samplingpicfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("samplingpicfile", samplingpicfile.getName(), requestFile);//抽样单
                }
            }

            {
                HashMap<String, String> samplingInfoMap = taskDetailViewModel.taskEntity.taskSamples.get(i).samplingInfoMap;
                //没填的时候默认值
                multipartBodyBuilder.addFormDataPart("sampling.taskfrom", "1");//抽样单
                if (null != samplingInfoMap && !samplingInfoMap.isEmpty()) {
                    for (String s : samplingInfoMap.keySet()) {
                        if (!s.startsWith("sampling.")) continue;
                        multipartBodyBuilder.addFormDataPart(s, samplingInfoMap.get(s) + "");//抽样单
                    }
                }
            }


            {
                File disposalfile = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).disposalfile);
                if (disposalfile.exists()) {
                    Log.e("file", disposalfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, disposalfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("disposalfile", disposalfile.getName(), requestFile);//处置单
                }
            }
            {
                File disposalpicfile = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).disposalpicfile);
                if (disposalpicfile.exists()) {
                    Log.e("file", disposalpicfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, disposalpicfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("disposalpicfile", disposalpicfile.getName(), requestFile);//处置单
                }
            }

            {
                HashMap<String, String> adviceInfoMap = taskDetailViewModel.taskEntity.taskSamples.get(i).adviceInfoMap;
                //没填的时候默认值
                multipartBodyBuilder.addFormDataPart("advice.companyname", "3");
                if (null != adviceInfoMap && !adviceInfoMap.isEmpty()) {
                    for (String s : adviceInfoMap.keySet()) {
                        if (!s.startsWith("advice.")) continue;
                        multipartBodyBuilder.addFormDataPart(s, adviceInfoMap.get(s) + "");//抽样单
                    }
                }

            }

            List<Pics> list = taskDetailViewModel.taskEntity.taskSamples.get(i).getPics();

            if (null == list || list.isEmpty()) {
                ToastUtil.showToast(getActivity(), "请选择图片");
                continue;
            }
            for (Pics entity : list) {
                File f = new File(entity.getOriginalPath());
                if (!f.exists()) {
                    Log.e("file", f.getAbsolutePath());
                    ToastUtil.showToast(getActivity(), "无效图片");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("picstrs", entity.getRemarks() + "")
                        .addFormDataPart("uploadpics", f.getName(), requestImage);
            }
//            List<LocalMediaInfo> listVideo = taskExecptionViewModel.taskList.get(i).videoList;

//            for (LocalMediaInfo localMedia :
//                    listVideo) {
//                File f = new File(localMedia.getPath());
//                if (!f.exists()) {
//                    Log.e("file", f.getAbsolutePath());
//                    ToastUtil.showToast(getActivity(), "无效视频");
//                    continue;
//                }
//                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
//                multipartBodyBuilder.addFormDataPart("videostrs", localMedia.title)
//                        .addFormDataPart("uploadvideos", f.getName(), requestImage);
//            }

        }
        showLoadingDialog();

        RetrofitService.createApiService(Request.class)
                .savesampleByBody(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        dismissLoadingDialog();
                        if (!TextUtils.isEmpty(s)) {
                            com.product.sampling.maputil.ToastUtil.show(getActivity(), "上传成功,样品记录为" + s);
                        }
                        saveTaskInLocalFile(true);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        dismissLoadingDialog();
                        com.product.sampling.maputil.ToastUtil.show(getActivity(), message + "");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MediaHelper.REQUEST_IMAGE_CODE:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    if (selectId != -1) {
                        List<Pics> mediaInfos = new ArrayList<>();
                        for (LocalMedia media :
                                selectList) {
                            Pics mediaInfo = new Pics();
                            mediaInfo.setOriginalPath(media.getPath());
                            mediaInfo.setCompressPath(media.getCompressPath());
                            mediaInfo.setCompressed(media.isCompressed());
                            mediaInfos.add(mediaInfo);
                        }
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).isLocalData = true;
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).pics.addAll(mediaInfos);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    break;
                case RequestCodePdf:
                    if (data != null) {
                        int index = data.getIntExtra("task", -1);
                        if (index != -1) {

                            //把生成的pdf 赋值给列表

                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            HashMap map = new HashMap();
                            String para = data.getStringExtra("data");
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(para);
                                String jsonData = jsonObject.get("data").toString();
                                String[] sourceStrArray = jsonData.split("&");
                                for (int i = 0; i < sourceStrArray.length; i++) {
                                    String[] value = sourceStrArray[i].split("=");
                                    if (value.length > 1) {
                                        map.put(value[0], value[1]);
                                    } else {
                                    }
                                }
                                int pos = data.getIntExtra(Intent_Order, 1);
                                if (pos == 1) {
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).samplingfile = data.getStringExtra("pdf");
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).samplingInfoMap = map;
                                    shareBySystem(data.getStringExtra("pdf"));
                                } else if (pos == 2) {
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).adviceInfoMap = map;
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).disposalfile = data.getStringExtra("pdf");
                                    shareBySystem(data.getStringExtra("pdf"));
//                                    findPrintShare(data.getStringExtra("pdf"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("para", para);
                        }
                    }
                    break;
                case Select_Handle:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).samplingpicfile = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        shareBySystem(selectHandle.get(0).getPath());
                    }
                    break;
                case Select_Check:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).disposalpicfile = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        shareBySystem(selectHandle.get(0).getPath());
                    }
                    break;


            }
        }
    }


    /**
     * 取pdf截图
     *
     * @param requstCode
     */
    public void startGalleryForPdf(int index, int requstCode) {
        MediaHelper.startGallery(TaskSampleFragment.this, PictureConfig.SINGLE, requstCode);
    }

    private void saveTaskInLocalFile(boolean isRemove) {
        Gson gson = new Gson();
        ArrayList<TaskEntity> listTask = new ArrayList<>();
        String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
        if (!TextUtils.isEmpty(taskListStr)) {
            Type listType = new TypeToken<List<TaskEntity>>() {
            }.getType();
            listTask = gson.fromJson(taskListStr, listType);
            if (null != listTask && !listTask.isEmpty()) {
                for (int i = 0; i < listTask.size(); i++) {
                    if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
                        listTask.remove(i);
                    }
                }
            }
        }
        if (!isRemove) {
            taskDetailViewModel.taskEntity.isLoadLocalData = true;
            listTask.add(taskDetailViewModel.taskEntity);
            com.product.sampling.maputil.ToastUtil.show(getActivity(), "保存成功");
        }
        SPUtil.put(getActivity(), "tasklist", gson.toJson(listTask));
    }

    void findTaskInLocalFile() {
        Gson gson = new Gson();
        String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
        if (!TextUtils.isEmpty(taskListStr)) {
            Type listType = new TypeToken<List<TaskEntity>>() {
            }.getType();
            ArrayList<TaskEntity> listTask = gson.fromJson(taskListStr, listType);
            if (null != listTask && !listTask.isEmpty()) {
                for (int i = 0; i < listTask.size(); i++) {
                    if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
                        taskDetailViewModel.taskEntity = listTask.get(i);
                    }
                }
//                if (null != taskDetailViewModel.taskEntity.taskSamples && !taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
//                    for (int j = 0; j < taskDetailViewModel.taskEntity.taskSamples.size(); j++) {
//                        TaskSample taskSample = taskDetailViewModel.taskEntity.taskSamples.get(j);
//                        if (!taskSample.isLocalData) {
//                            taskDetailViewModel.taskEntity.taskSamples.remove(taskSample);
//                        }
//                    }
//                }

            }

        }
    }

    private void postSceneData() {
        if (!taskDetailViewModel.taskEntity.isLoadLocalData) return;

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("id", taskDetailViewModel.taskEntity.id)
                .addFormDataPart("taskisok", "0")
                .addFormDataPart("updateorsave", "0")
                .addFormDataPart("samplecount", "1");

        if (null != MainApplication.INSTANCE.getMyLocation()) {
            multipartBodyBuilder.addFormDataPart("taskaddress", MainApplication.INSTANCE.getMyLocation().getAddress() + "")
                    .addFormDataPart("longitude", MainApplication.INSTANCE.getMyLocation().getLongitude() + "")
                    .addFormDataPart("latitude", MainApplication.INSTANCE.getMyLocation().getLatitude() + "");
        }

        boolean hasData = false;
        if (taskDetailViewModel.taskEntity.isLoadLocalData && null != taskDetailViewModel.taskEntity.pics && !taskDetailViewModel.taskEntity.pics.isEmpty()) {
            for (int i = 0; i < taskDetailViewModel.taskEntity.pics.size(); i++) {
                String path = taskDetailViewModel.taskEntity.pics.get(i).getOriginalPath();
                if (TextUtils.isEmpty(path)) continue;
                File f = new File(path);
                if (!f.exists()) {
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("picstrs", taskDetailViewModel.taskEntity.pics.get(i).title + "")
                        .addFormDataPart("uploadpics", f.getName(), requestImage);
                hasData = true;
            }
        }
        if (taskDetailViewModel.taskEntity.isLoadLocalData && null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {

            for (int i = 0; i < taskDetailViewModel.taskEntity.voides.size(); i++) {
                if (!taskDetailViewModel.taskEntity.voides.get(i).isLocal) {
                    continue;
                }
                File f = new File(taskDetailViewModel.taskEntity.voides.get(i).getPath());
                if (!f.exists()) {
                    Log.e("视频", f.getAbsolutePath());
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效视频");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("videostrs", taskDetailViewModel.taskEntity.voides.get(i).title + "")
                        .addFormDataPart("uploadvideos", f.getName(), requestImage);
                hasData = true;
            }
        }
        if (!hasData) {
//            com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "请先选择图片或者视频");
            return;
        }
        showLoadingDialog();
        RetrofitService.createApiService(Request.class)
                .uploadtaskinfo(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        dismissLoadingDialog();
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "添加成功,现场id为" + s);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        dismissLoadingDialog();
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), message);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });


    }

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    private void shareBySystem(String path) {
        File doc = new File(path);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(doc));

        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.product.sampling.fileprovider", doc);
//        share.setDataAndType(contentUri, "application/vnd.android.package-archive");
        share.setDataAndType(contentUri, "application/pdf");

        startActivity(Intent.createChooser(share, "分享文件"));

    }

    public void findPrintShare(String path) {
   /* Intent intent = new Intent(this, DayinActivity.class);http://xzc.197746.com/printershare11165.apk
    startActivity(intent);*/
        if (isAvilible(getActivity(), "org.mopria.printplugin")) {
            File doc = new File(path);
            ComponentName comp = new ComponentName("org.mopria.printplugin", "org.mopria.printplugin.DocumentRenderingActivity");
            // ComponentName comp = new ComponentName("com.dynamixsoftware.printershare","com.dynamixsoftware.printershare.ActivityPrintDocuments");
            Intent intent = new Intent();
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
            intent.setType("application/doc");
//            intent.setData(Uri.fromFile(doc));
//            startActivity(intent);


            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.product.sampling.fileprovider", doc);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            startActivity(intent);


        } else {
            String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath() + File.separator + "printershare11165.apk";
//            new DownloadTask(this).execute("http://xzc.197746.com/printershare11165.apk",destPath);
        }
    }

    private boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.contains("canon")) {
                    Log.e("canon", pn);
                }
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

}
