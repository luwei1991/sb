package com.product.sampling.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.adapter.ImageAndTextRecyclerViewAdapter;
import com.product.sampling.adapter.ImageServerRecyclerViewAdapter;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.adapter.VideoAndTextRecyclerViewAdapter;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.Videos;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.FileDownloader;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.product.sampling.adapter.TaskSampleRecyclerViewAdapter.RequestCodePdf;
import static com.product.sampling.ui.WebViewActivity.Intent_Order;

/**
 * 未抽到样品
 */
public class TaskUnfindSampleFragment extends BasePhotoFragment {

    RecyclerView mRecyclerViewImageList;
    RecyclerView mRecyclerViewVideoList;
    static TaskUnfindSampleFragment fragment;
    TaskDetailViewModel taskDetailViewModel;
    public static final int Unfind_Sample_Result = 103;

    TextView mTextViewCompanyname;

    public TaskUnfindSampleFragment() {

    }

    public TaskEntity taskUnFindEntity = new TaskEntity();
    EditText etTips;

    public static TaskUnfindSampleFragment newInstance(TaskEntity task) {
        Bundle args = new Bundle();
        args.putParcelable("task", task);
        if (fragment == null) {
            fragment = new TaskUnfindSampleFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_unfind_sample, container, false);
        initView(rootView);
        return rootView;
    }


    /**
     * init view.
     *
     * @param view
     */
    private void initView(View view) {

        // image
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        mRecyclerViewImageList = view.findViewById(R.id.item_image_list);
        mRecyclerViewImageList.setLayoutManager(linearLayoutManager);


        // video
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        mRecyclerViewVideoList = view.findViewById(R.id.item_video_list);
        mRecyclerViewVideoList.setLayoutManager(linearLayoutManager);

        mTextViewCompanyname = view.findViewById(R.id.tv_companyname);
        etTips = view.findViewById(R.id.remark);
        // 现场照片选择
        view.findViewById(R.id.iv_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startGallery(fragment, PictureConfig.MULTIPLE, MediaHelper.REQUEST_IMAGE_CODE);
            }
        });


        // 现场视频选择
        view.findViewById(R.id.iv_choose_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startVideo(fragment, MediaHelper.REQUEST_VIDEO_CODE);
            }
        });


        // TODO 保存并提交
        // 保存并提交
        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postUnfindData();
            }
        });


        // TODO 保存
        // 保存
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // 编辑单据并打印
        view.findViewById(R.id.btn_edit_handling_sheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                Bundle b = new Bundle();
                b.putInt(Intent_Order, 4);
                b.putSerializable("task", (Serializable) taskUnFindEntity);
                b.putSerializable("map", (Serializable) taskUnFindEntity.unfindSampleInfoMap);
                intent.putExtras(b);
                startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
            }
        });


        //TODO 上传
        // 上传
        view.findViewById(R.id.btn_upload_handling_sheet).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                MediaHelper.startGallery(fragment, PictureConfig.SINGLE, Unfind_Sample_Result);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskUnFindEntity = (TaskEntity) getArguments().get("task");

        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        taskDetailViewModel.orderDetailLiveData.observe(this, new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskRefusedEntityLoadDataModel) {
                if (taskRefusedEntityLoadDataModel.isSuccess()) {
                    taskUnFindEntity = taskRefusedEntityLoadDataModel.getData();
                    mTextViewCompanyname.setText(taskUnFindEntity.companyname);
                    etTips.setText(taskUnFindEntity.remark);
                    setupRecyclerViewFromServer(mRecyclerViewImageList, taskUnFindEntity.pics);
                    setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskUnFindEntity.voides);
                    if (null != taskUnFindEntity.voides && !taskUnFindEntity.voides.isEmpty()) {
                        rxPermissionTest();
                    }
                    if (null != taskUnFindEntity.unfind) {
                        Gson gson = new Gson();
                        String obj1 = gson.toJson(taskUnFindEntity.unfind);
                        JsonObject object = new JsonParser().parse(obj1).getAsJsonObject();
                        HashMap<String, String> map = new HashMap();
                        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                            map.put("unfind." + entry.getKey(), entry.getValue().getAsString());
                        }
                        taskUnFindEntity.unfindSampleInfoMap = map;
                    }
                }
            }
        });
        taskDetailViewModel.requestOrderList(AccountManager.getInstance().getUserId(), taskUnFindEntity.id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MediaHelper.REQUEST_VIDEO_CODE: {

                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    List<Videos> mediaInfos = new ArrayList<>();
                    for (LocalMedia media :
                            selectList) {
                        Videos mediaInfo = new Videos();
                        mediaInfo.setPath(media.getPath());
                        mediaInfo.setCompressPath(media.getCompressPath());
                        mediaInfo.setCutPath(media.getCutPath());
                        mediaInfo.setDuration(media.getDuration());
                        mediaInfo.setChecked(media.isChecked());
                        mediaInfo.setCut(media.isCut());

                        mediaInfo.setPosition(media.getPosition());
                        mediaInfo.setNum(media.getNum());
                        mediaInfo.setMimeType(media.getMimeType());
                        mediaInfo.setPictureType(media.getPictureType());
                        mediaInfo.setCompressed(media.isCompressed());
                        mediaInfo.setWidth(media.getWidth());
                        mediaInfo.setHeight(media.getHeight());
                        mediaInfo.isLocal = true;
                        mediaInfos.add(mediaInfo);
                    }

//                    for (int i = taskDetailViewModel.taskEntity.voides.size() - 1; i >= 0; i--) {
//                        if (!taskDetailViewModel.taskEntity.voides.get(i).isLocal) {
//                            taskDetailViewModel.taskEntity.voides.remove(i);
//                        }
//                    }
                    taskUnFindEntity.isLoadLocalData = true;
                    taskUnFindEntity.isEditedTaskScene = true;
                    taskUnFindEntity.voides.addAll(mediaInfos);
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskUnFindEntity.voides);
                }
                break;
                case MediaHelper.REQUEST_IMAGE_CODE: {


                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    List<Pics> mediaInfos = new ArrayList<>();
                    for (LocalMedia media :
                            selectList) {
                        Pics mediaInfo = new Pics();
                        mediaInfo.setOriginalPath(media.getPath());
                        mediaInfo.setCompressPath(media.getCompressPath());
                        mediaInfo.setCompressed(media.isCompressed());
                        mediaInfo.isLocal = true;
                        mediaInfos.add(mediaInfo);
                    }
/*                    for (int i = taskDetailViewModel.taskEntity.pics.size() - 1; i >= 0; i--) {
                        Pics pics1 = taskDetailViewModel.taskEntity.pics.get(i);
                        if (!pics1.isLocal) {
                            taskDetailViewModel.taskEntity.pics.remove(pics1);
                        }
                    }*/
                    taskUnFindEntity.pics.addAll(mediaInfos);
                    taskUnFindEntity.isLoadLocalData = true;
                    taskUnFindEntity.isEditedTaskScene = true;
                    setupRecyclerView(mRecyclerViewImageList, taskUnFindEntity.pics);
                }
                break;
                case RequestCodePdf:
                    if (data != null) {

                        //把生成的pdf 赋值给列表
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
                            int pos = data.getIntExtra(Intent_Order, 3);
                            taskUnFindEntity.unfindfile = data.getStringExtra("pdf");
                            taskUnFindEntity.unfindSampleInfoMap = map;
                            shareBySystem(data.getStringExtra("pdf"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("para", para);
                    }
                    break;
                case Unfind_Sample_Result:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskUnFindEntity.unfindpicfile = selectHandle.get(0).getPath();
                        shareBySystem(selectHandle.get(0).getPath());
                    }
                    break;
            }
        }

    }

    // help method.

    /**
     * display images
     *
     * @param recyclerView
     * @param task
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
        ImageAndTextRecyclerViewAdapter adapter = new ImageAndTextRecyclerViewAdapter(this, task, true);
        recyclerView.setAdapter(adapter);
    }


    /**
     * display video
     *
     * @param mRecyclerViewVideoList
     * @param videoList
     */
    private void setupRecyclerViewVideo(RecyclerView mRecyclerViewVideoList, List<Videos> videoList) {
        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, true));

    }

    /**
     * getPath
     *
     * @return
     */
    private String getPath() {
        return "/storage/emulated/0/zip";
    }


    /**
     * @param recyclerView
     * @param task
     */
    private void setupRecyclerViewFromServer(@NonNull RecyclerView recyclerView, List task) {
        ImageServerRecyclerViewAdapter adapter = new ImageServerRecyclerViewAdapter(getActivity(), task, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * @param mRecyclerViewVideoList
     * @param videoList
     */
    private void setupRecyclerViewVideoFromServer(RecyclerView mRecyclerViewVideoList, List<Videos> videoList) {
        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, false));
    }

    private void downLoadVideo() {
        for (Videos videos : taskUnFindEntity.voides) {

            FileDownloader.downloadFile(RetrofitService.createApiService(Request.class).downloadVideo(videos.getId()), Constants.getPath(), videos.getFileName(), new DownloadProgressHandler() {


                @Override
                public void onProgress(int progress, long total, long speed) {
                    LogUtils.i("下载文件中:" + progress / total);
                }

                @Override
                public void onCompleted(File file) {
                    videos.setPath(file.getPath());
                    setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskUnFindEntity.voides);
                    LogUtils.i("下载文件成功", file.getAbsolutePath() + "-" + file.getPath() + "-" + file.getName());
//                                    FileDownloader.clear();
                }

                @Override
                public void onError(Throwable e) {
                    LogUtils.e("下载文件异常", e.getMessage());
                }
            });
        }


    }

    private void rxPermissionTest() {

        com.tbruyelle.rxpermissions2.RxPermissions rxPermissions = new com.tbruyelle.rxpermissions2.RxPermissions(getActivity());
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        downLoadVideo();
                    } else {
                        // At least one permission is denied
                    }
                });


    }

    private void postUnfindData() {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("id", taskUnFindEntity.id)
                .addFormDataPart("taskisok", "2")//任务异常状态0正常1拒检2未抽样到单位
                .addFormDataPart("remark", etTips.getText().toString());

        if (null != MainApplication.INSTANCE.getMyLocation()) {
            multipartBodyBuilder.addFormDataPart("taskaddress", MainApplication.INSTANCE.getMyLocation().getAddress() + "")
                    .addFormDataPart("longitude", MainApplication.INSTANCE.getMyLocation().getLongitude() + "")
                    .addFormDataPart("latitude", MainApplication.INSTANCE.getMyLocation().getLatitude() + "");
        }

        boolean hasData = false;
        if (null != taskUnFindEntity.pics && !taskUnFindEntity.pics.isEmpty()) {
            for (int i = 0; i < taskUnFindEntity.pics.size(); i++) {
                Pics pics = taskUnFindEntity.pics.get(i);
                if (!TextUtils.isEmpty(pics.getId())) {
                    multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileStr", pics.getRemarks() + "");
                    multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileid", pics.getId() + "");
                    continue;

                }
                String path = pics.getOriginalPath();
                if (TextUtils.isEmpty(path)) continue;
                File f = new File(path);
                if (!f.exists()) {
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体

                multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileStr", pics.getRemarks() + "")
                        .addFormDataPart("uploadPic[" + i + "].fileStream", f.getName(), requestImage);
                hasData = true;
            }
        }
        if (null != taskUnFindEntity.voides && !taskUnFindEntity.voides.isEmpty()) {

            for (int i = 0; i < taskUnFindEntity.voides.size(); i++) {
                Videos videos = taskUnFindEntity.voides.get(i);
                if (!TextUtils.isEmpty(videos.getId())) {
                    multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileStr", videos.getRemarks() + "");
                    multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileid", videos.getId() + "");
                    continue;
                }
                File f = new File(taskUnFindEntity.voides.get(i).getPath());
                if (!f.exists()) {
                    Log.e("视频", f.getAbsolutePath());
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效视频");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileStr", videos.getRemarks() + "")
                        .addFormDataPart("uploadVedio[" + i + "].fileStream", f.getName(), requestImage);
                hasData = true;
            }
        }
        if (null != taskUnFindEntity.taskSamples && !taskUnFindEntity.taskSamples.isEmpty()) {
            multipartBodyBuilder.addFormDataPart("samplecount", taskUnFindEntity.taskSamples.size() + "");
        } else {
            multipartBodyBuilder.addFormDataPart("samplecount", "0");
        }

        {
            File unfindfile = new File(taskUnFindEntity.unfindfile);
            if (unfindfile.exists()) {
                Log.e("file", unfindfile.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, unfindfile);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("unfindfile", unfindfile.getName(), requestFile);//未找到样品单file
            }
        }

        {
            HashMap<String, String> samplingInfoMap = taskUnFindEntity.unfindSampleInfoMap;
            if (null != samplingInfoMap && !samplingInfoMap.isEmpty()) {
                for (String s : samplingInfoMap.keySet()) {
                    if (!s.startsWith("unfind.")) continue;
                    multipartBodyBuilder.addFormDataPart(s, samplingInfoMap.get(s));//未找到样品单字段
                }
            }
        }


        {
            File unfindpicfile = new File(taskUnFindEntity.unfindpicfile);
            if (unfindpicfile.exists()) {
                Log.e("file", unfindpicfile.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, unfindpicfile);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("unfindpicfile", unfindpicfile.getName(), requestFile);//样品单图片
            }
        }


//        if (!hasData) {
//            com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "请先选择图片或者视频");
//            return;
//        }
        showLoadingDialog();
        RetrofitService.createApiService(Request.class)
                .uploadtaskinfo(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        dismissLoadingDialog();
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "添加成功");
                        EventBus.getDefault().post(TaskMessage.getInstance(taskUnFindEntity.id));
                        saveTaskInLocalFile(true);
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

    private void saveTaskInLocalFile(boolean isRemove) {
        try {
            Gson gson = new Gson();
            ArrayList<TaskEntity> listTask = new ArrayList<>();
            String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
            if (!TextUtils.isEmpty(taskListStr)) {
                Type listType = new TypeToken<List<TaskEntity>>() {
                }.getType();
                listTask = gson.fromJson(taskListStr, listType);
                if (null != listTask && !listTask.isEmpty()) {
                    for (int i = 0; i < listTask.size(); i++) {
                        if (listTask.get(i).id.equals(taskUnFindEntity.id)) {
                            listTask.remove(i);
                        }
                    }
                }
            }
            if (!isRemove) {
                listTask.add(taskUnFindEntity);
            }
            SPUtil.put(getActivity(), "tasklist", gson.toJson(listTask));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (isRemove) {
            getActivity().finish();
        } else {
            com.product.sampling.maputil.ToastUtil.show(getActivity(), "保存成功");
        }
    }
}
