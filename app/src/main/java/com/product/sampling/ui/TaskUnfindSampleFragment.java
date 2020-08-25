package com.product.sampling.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.Constants;
import com.product.sampling.MainApplication;
import com.product.sampling.R;
import com.product.sampling.adapter.ExceptionServerRecyclerViewAdapter;
import com.product.sampling.adapter.ImageAndTextRecyclerViewAdapter;
import com.product.sampling.adapter.VideoAndTextRecyclerViewAdapter;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.Videos;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.NotCheckForm;
import com.product.sampling.db.tables.NotCheckFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.form.NotCheckFormActivity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.FileDownloader;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.product.sampling.adapter.TaskSampleRecyclerViewAdapter.RequestCodePdf;
import static com.product.sampling.ui.H5WebViewActivity.Intent_Order;
import static com.product.sampling.ui.TaskDetailActivity.TASK_NOT_UPLOAD;

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
    Button btnUploadUnfindPic;
    TextView mTVSheet;

    Button btnSave;
    Button btnSubmit;

    ImageView ivChooseVideo;
    ImageView ivChooseImage;


    public TaskUnfindSampleFragment() {

    }

    public TaskEntity taskUnFindEntity = new TaskEntity();
    EditText etTips;

    public static TaskUnfindSampleFragment newInstance(TaskEntity task) {
        Bundle args = new Bundle();
        args.putSerializable("task", task);
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
        ivChooseImage = view.findViewById(R.id.iv_choose);
        ivChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startGallery(fragment, PictureConfig.MULTIPLE, MediaHelper.REQUEST_IMAGE_CODE);
            }
        });


        // 现场视频选择
        ivChooseVideo = view.findViewById(R.id.iv_choose_video);
        ivChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startVideo(fragment, MediaHelper.REQUEST_VIDEO_CODE);
            }
        });

        // 提交
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("确定提交所有信息吗？", 1);
            }
        });

        // 保存
        btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskUnFindEntity.taskisok = "2";
                taskUnFindEntity.taskstatus = TASK_NOT_UPLOAD;
                LocalTaskListManager.getInstance().update(taskUnFindEntity);
                EventBus.getDefault().postSticky(TaskMessage.getInstance(taskUnFindEntity.id,false));
//                saveTaskInLocalFile(false);
            }
        });
        btnSubmit.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);

        // 编辑单据并打印
        view.findViewById(R.id.btn_edit_spot_check_sheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("确定打开表单吗？", 2);
            }
        });

        btnUploadUnfindPic = view.findViewById(R.id.btn_upload_handling_sheet);
        btnUploadUnfindPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startGallery(fragment, PictureConfig.SINGLE, Unfind_Sample_Result);
            }
        });
        mTVSheet = view.findViewById(R.id.tv_handle_sheet);
        if (!TextUtils.isEmpty(taskUnFindEntity.unfindfile)) {
            mTVSheet.setText(taskUnFindEntity.unfindfile);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskUnFindEntity = (TaskEntity) getArguments().get("task");

        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        taskDetailViewModel.orderDetailLiveData.observe(getViewLifecycleOwner(), new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskRefusedEntityLoadDataModel) {
                if (taskRefusedEntityLoadDataModel.isSuccess()) {
                    taskUnFindEntity = taskRefusedEntityLoadDataModel.getData();
                    initData();
                }
            }
        });
        TaskEntity localEntity = LocalTaskListManager.getInstance().query(taskUnFindEntity);
        if(localEntity != null){
            taskUnFindEntity = localEntity;
            initData();
        }else{
            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskUnFindEntity.id);
        }

//        if (!taskUnFindEntity.isLoadLocalData) {
//            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskUnFindEntity.id);
//        } else {
//            findTaskInLocalFile();
//            initData();
//        }
    }

    private void initData() {
        mTextViewCompanyname.setText(taskUnFindEntity.companyname);
        etTips.setText(taskUnFindEntity.remark);
        if (!taskUnFindEntity.taskisok.equals("2")) {
            if( taskUnFindEntity.pics != null){
                taskUnFindEntity.pics.clear();
            }
            if( taskUnFindEntity.voides != null){
                taskUnFindEntity.voides.clear();
            }

        }
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
        if (null == taskUnFindEntity.unfindSampleInfoMap)
            taskUnFindEntity.unfindSampleInfoMap = new HashMap<>();
        taskUnFindEntity.unfindSampleInfoMap.put("companyname", taskDetailViewModel.taskEntity.companyname);
        taskUnFindEntity.unfindSampleInfoMap.put("companyaddress", taskDetailViewModel.taskEntity.companyaddress);

        if (TextUtils.isEmpty(taskUnFindEntity.unfindpicfile)) {
            btnUploadUnfindPic.setText("(拍照)上传");
        } else {
            btnUploadUnfindPic.setText("已拍照");
        }

        if (!taskUnFindEntity.isUploadedTask()) {
            btnSave.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
        if (!taskUnFindEntity.isUploadedTask()) {
            btnSave.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            ivChooseVideo.setVisibility(View.VISIBLE);
            ivChooseImage.setVisibility(View.VISIBLE);
        } else {
            btnUploadUnfindPic.setVisibility(View.GONE);
            ivChooseVideo.setVisibility(View.GONE);
            ivChooseImage.setVisibility(View.GONE);
        }
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
                    if(taskUnFindEntity.voides == null){
                        taskUnFindEntity.voides = new ArrayList<>();
                    }
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


                    if(taskUnFindEntity.pics == null){
                        taskUnFindEntity.pics = new ArrayList<>();
                    }
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
                            if (!TextUtils.isEmpty(taskUnFindEntity.unfindfile)) {
                                mTVSheet.setText(taskUnFindEntity.unfindfile);
                            }
                            shareBySystem(data.getStringExtra("pdf"));
                            taskDetailViewModel.sendReportRecord(taskUnFindEntity.id, "", "unfind");
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
                        if (!TextUtils.isEmpty(taskUnFindEntity.unfindpicfile)) {
                            btnUploadUnfindPic.setText("已拍照");
                        }
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
        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, false));

    }


    /**
     * @param recyclerView
     * @param task
     */
    private void setupRecyclerViewFromServer(@NonNull RecyclerView recyclerView, List task) {
        if(task == null){
            task = new ArrayList();
        }
        ExceptionServerRecyclerViewAdapter adapter = new ExceptionServerRecyclerViewAdapter(getActivity(), task, taskUnFindEntity.isUploadedTask(),fragment);
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
            if (TextUtils.isEmpty(videos.getId())) continue;
            FileDownloader.downloadFile(RetrofitServiceManager.getInstance().createApiService(Request.class).downloadVideo(videos.getId()), Constants.getPath(), videos.getFileName(), new DownloadProgressHandler() {

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

    /**
     * 从本地找数据
     */
//    private void findTaskInLocalFile() {
//        Gson gson = new Gson();
//        String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
//        if (!TextUtils.isEmpty(taskListStr)) {
//            Type listType = new TypeToken<List<TaskEntity>>() {
//            }.getType();
//            ArrayList<TaskEntity> listTask = gson.fromJson(taskListStr, listType);
//            if (null != listTask && !listTask.isEmpty()) {
//                for (int i = 0; i < listTask.size(); i++) {
//                    if (listTask.get(i).id.equals(taskUnFindEntity.id)) {
//                        taskUnFindEntity = listTask.get(i);
//                    }
//                }
//            }
//
//        }
//    }

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
 public void showUpLoadFail(String failMessage){
        Dialog failDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(failMessage)
                .create();
        failDialog.show();


     btnSubmit.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(failDialog != null && failDialog.isShowing()){
                    failDialog.dismiss();
                }
            }
        },1500);

    }

    /**上传dialog*/
    QMUITipDialog upDialog;
    private void postUnfindData() {
        NotCheckForm notCheckForm =  DbController.getInstance(getContext()).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(taskUnFindEntity.id)).build().unique();
        if(notCheckForm == null || TextUtils.isEmpty(notCheckForm.getPdfPath())|| TextUtils.isEmpty(notCheckForm.getTaskLYID())){
            showUpLoadFail("请检查表单有没有生成PDF文件或者表单中任务来源字段有没有填写。");
            return;
        }
        upDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("异常信息提交中...")
                .create(false);
        upDialog.show();
        Map<String, String> requestText = new HashMap<String, String>();
        Map<String, String> requestFile = new HashMap<String, String>();
        requestText.put("userid", AccountManager.getInstance().getUserId());
        requestText.put("id", taskUnFindEntity.id);
        requestText.put("taskisok", "2");//任务异常状态0正常1拒检2未抽样到单位
        requestText.put("remark", etTips.getText().toString());
        requestText.put("unfind.taskfrom ", notCheckForm.getTaskLYID());
        requestText.put("unfind.checkman ",  taskUnFindEntity.id);
        if (null != taskUnFindEntity.taskSamples && !taskUnFindEntity.taskSamples.isEmpty()) {
            requestText.put("samplecount", taskUnFindEntity.taskSamples.size() + "");
        } else {
            requestText.put("samplecount", "0");
        }
        AMapLocation location = MainApplication.INSTANCE.getMyLocation();
        if (null != location) {
            requestText.put("taskaddress", location.getAddress() + "");
            requestText.put("longitude", location.getLongitude() + "");
            requestText.put("latitude", location.getLatitude() + "");
        }

        if (null != taskUnFindEntity.pics && !taskUnFindEntity.pics.isEmpty()) {
            for (int i = 0; i < taskUnFindEntity.pics.size(); i++) {
                Pics pics = taskUnFindEntity.pics.get(i);
                if (!TextUtils.isEmpty(pics.getId())) {
                    requestText.put("uploadPic[" + i + "].fileStr", pics.getRemarks() + "");
                    requestText.put("uploadPic[" + i + "].fileid", pics.getId() + "");
                    continue;

                }
                String path = pics.getOriginalPath();
                if (TextUtils.isEmpty(path)) continue;
                File f = new File(path);
                if (!f.exists()) {
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
                    continue;
                }
                requestText.put("uploadPic[" + i + "].fileStr", pics.getRemarks() + "");
                requestFile.put("uploadPic[" + i + "].fileStream", path);
            }
        }
        if (null != taskUnFindEntity.voides && !taskUnFindEntity.voides.isEmpty()) {

            for (int i = 0; i < taskUnFindEntity.voides.size(); i++) {
                Videos videos = taskUnFindEntity.voides.get(i);
                if (!TextUtils.isEmpty(videos.getId())) {
                    requestText.put("uploadVedio[" + i + "].fileStr", videos.getRemarks() + "");
                    requestText.put("uploadVedio[" + i + "].fileid", videos.getId() + "");
                    continue;
                }
                File f = new File(videos.getPath());
                if (!f.exists()) {
                    Log.e("视频", f.getAbsolutePath());
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效视频");
                    continue;
                }
                requestText.put("uploadVedio[" + i + "].fileStr", videos.getRemarks() + "");
                requestFile.put("uploadVedio[" + i + "].fileStream", videos.getPath());
            }
        }

        //未找到样品单上传
        {
            String pdfPath = notCheckForm.getPdfPath();
            if(!TextUtils.isEmpty(pdfPath)){
                File unfindfile = new File(pdfPath);
                if(unfindfile.exists()){
                    requestFile.put("unfindfile", pdfPath);//未找到样品单file
                }
            }

        }

        {
            HashMap<String, String> samplingInfoMap = taskUnFindEntity.unfindSampleInfoMap;
            if (null != samplingInfoMap && !samplingInfoMap.isEmpty()) {
                for (String s : samplingInfoMap.keySet()) {
                    if (!s.startsWith("unfind.")) continue;
                    requestText.put(s, samplingInfoMap.get(s));//未找到样品单字段
                }
            }
        }


        {
            File unfindpicfile = new File(taskUnFindEntity.unfindpicfile);
            if (unfindpicfile.exists()) {
                Log.e("file", unfindpicfile.getAbsolutePath());
                requestFile.put("unfindpicfile", taskUnFindEntity.unfindpicfile);//样品单图片
            }
        }


//        if (!hasData) {
//            com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "请先选择图片或者视频");
//            return;
//        }
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        // text
        Iterator<Map.Entry<String, String>> iter = requestText.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String inputName = (String) entry.getKey();
            String inputValue = (String) entry.getValue();
            if (TextUtils.isEmpty(inputValue)) {
                continue;
            }
          multipartBodyBuilder.addFormDataPart(inputName,inputValue);
        }

        // file
        Iterator<Map.Entry<String, String>> iterFile = requestFile.entrySet().iterator();
        while (iterFile.hasNext()) {
            Map.Entry<String, String> entry = iterFile.next();
            String inputName = (String) entry.getKey();
            String inputValue = (String) entry.getValue();
            if (TextUtils.isEmpty(inputValue)) {
                continue;
            }
            File file = new File(inputValue);
            if(file.exists()){
                RequestBody requestFileBody = RequestBody.create(MultipartBody.FORM, file);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart(inputName, file.getName(), requestFileBody);
            }
        }

        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .uploadtaskinfo(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        upDialog.dismiss();
                        showUpLoadSuccess();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        upDialog.dismiss();
                        showUpLoadFail(message);
                    }
                });

    }


    public void showUpLoadSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("信息上传成功！")
                .create();
        sucDialog.show();


        btnSubmit.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sucDialog != null && sucDialog.isShowing()){
                    sucDialog.dismiss();
                }
                //上传成功，删除
                EventBus.getDefault().postSticky(TaskMessage.getInstance(taskUnFindEntity.id,true));
                taskUnFindEntity.taskisok = "2";
                LocalTaskListManager.getInstance().remove(taskUnFindEntity);
//                saveTaskInLocalFile(true);
                ActivityManager.getInstance().currentActivity().finish();

            }
        },1500);

    }

//    private void saveTaskInLocalFile(boolean isRemove) {
//        try {
//            Gson gson = new Gson();
//            ArrayList<TaskEntity> listTask = new ArrayList<>();
//            String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
//            if (!TextUtils.isEmpty(taskListStr)) {
//                Type listType = new TypeToken<List<TaskEntity>>() {
//                }.getType();
//                listTask = gson.fromJson(taskListStr, listType);
//                if (null != listTask && !listTask.isEmpty()) {
//                    for (int i = 0; i < listTask.size(); i++) {
//                        if (listTask.get(i).id.equals(taskUnFindEntity.id)) {
//                            listTask.remove(i);
//                        }
//                    }
//                }
//            }
//            if (!isRemove) {
//                taskUnFindEntity.taskisok = 2 + "";
//                listTask.add(taskUnFindEntity);
//            }
//            SPUtil.put(getActivity(), "tasklist", gson.toJson(listTask));
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        if (isRemove) {
//            getActivity().finish();
//        } else {
//            com.product.sampling.maputil.ToastUtil.show(getActivity(), "保存成功,请到“未上传”中去查看保存内容！");
//        }
//    }


    private void showDialog(String msg, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (index == 1) {
                    postUnfindData();
                } else if (index == 2) {
                    Intent intent = new Intent(getActivity(), NotCheckFormActivity.class);
                    Bundle b = new Bundle();
                    //跳转到处置单
                    String taskId = taskUnFindEntity.id;//任务id
                    String tasktypecount = taskUnFindEntity.tasktypecount;//产品名称
                    String companyname = taskUnFindEntity.companyname;//企业名称
                    b.putString("taskId",taskId);
                    b.putString("tasktypecount",tasktypecount);
                    b.putString("companyname",companyname);
                    intent.putExtras(b);
                    NotCheckForm notCheckForm =  DbController.getInstance(getActivity()).getDaoSession().getNotCheckFormDao().queryBuilder().where(NotCheckFormDao.Properties.Id.eq(taskId)).build().unique();
                    if(notCheckForm != null){
                        //设置当前默认日期
                        Date nowData = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        String nowDateString = format.format(nowData);
                        String date[] = nowDateString.split("-");
                        notCheckForm.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                        NotCheckFormActivity.CYR_YEAR = date[0];
                        NotCheckFormActivity.CYR_MONTH = date[1];
                        NotCheckFormActivity.CYR_DAY = date[2];
                        DBManagerFactory.getInstance().getNotCheckFormManager().update(notCheckForm);
                    }else {
                        //创建数据库
                        NotCheckForm notCheckFormNew = new NotCheckForm();
                        notCheckFormNew.setId(taskId);
                        notCheckFormNew.setQymc(companyname);
                        Date nowData = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        String nowDateString = format.format(nowData);
                        String date[] = nowDateString.split("-");
                        notCheckFormNew.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                        NotCheckFormActivity.CYR_YEAR = date[0];
                        NotCheckFormActivity.CYR_MONTH = date[1];
                        NotCheckFormActivity.CYR_DAY = date[2];
                        DbController.getInstance(getActivity()).getDaoSession().getNotCheckFormDao().insert(notCheckFormNew);

                    }

                    startActivity(intent);
//
//                    Intent intent = new Intent(getActivity(), H5WebViewActivity.class);
//                    Bundle b = new Bundle();
//                    b.putInt(Intent_Order, 4);
//                    b.putSerializable("task", (Serializable) taskUnFindEntity);
//                    b.putSerializable("map", (Serializable) taskUnFindEntity.unfindSampleInfoMap);
//                    b.putBoolean(Intent_Edit, taskUnFindEntity.isUploadedTask());
//                    intent.putExtras(b);
//                    startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
