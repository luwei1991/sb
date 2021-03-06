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
import com.product.sampling.db.tables.RefuseForm;
import com.product.sampling.db.tables.RefuseFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.form.RefuseFormActivity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.FileDownloader;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
public class TaskCompanyRefusedFragment extends BasePhotoFragment {

    TaskDetailViewModel taskDetailViewModel;

    RecyclerView mRecyclerViewImageList;
    RecyclerView mRecyclerViewVideoList;
    public static final int Refused_Sample_Result = 103;
    TextView mTextViewCompanyname;
    EditText etTips;
    public TaskEntity taskRefusedEntity = new TaskEntity();
    Button btnUploadRefusedPic;
    TextView mTVSheet;

    Button btnSubmit;
    Button btnSave;
    ImageView ivChooseImage;
    ImageView ivChooseVideo;

    public TaskCompanyRefusedFragment() {

    }

    static TaskCompanyRefusedFragment fragment;

    public static TaskCompanyRefusedFragment newInstance(TaskEntity task) {
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        if (fragment == null) {
            fragment = new TaskCompanyRefusedFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_company_refused, container, false);
        initView(rootView);
        return rootView;
    }

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
                MediaHelper.startGallery(fragment, PictureConfig.MULTIPLE, MediaHelper.REQUEST_IMAGE_CODE_REFUSED);
            }
        });

        ivChooseVideo = view.findViewById(R.id.iv_choose_video);
        // 现场视频选择
        ivChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startVideo(fragment, MediaHelper.REQUEST_VIDEO_CODE);
            }
        });
        btnSubmit = view.findViewById(R.id.btn_submit);

        // 并提交
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("确定提交所有信息吗？", 1);
            }
        });

        btnSave = view.findViewById(R.id.btn_save);
        // 保存
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskRefusedEntity.taskisok = "2";
                taskRefusedEntity.taskstatus = TASK_NOT_UPLOAD;
                LocalTaskListManager.getInstance().update(taskRefusedEntity);
                EventBus.getDefault().postSticky(TaskMessage.getInstance(taskRefusedEntity.id,false));
//                saveTaskInLocalFile(false);
            }
        });

        btnSave.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        // 编辑单据并打印
        view.findViewById(R.id.btn_edit_spot_check_sheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("确定打开表单吗？", 2);
            }
        });
        btnUploadRefusedPic = view.findViewById(R.id.btn_upload_handling_sheet);
        btnUploadRefusedPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startGallery(fragment, PictureConfig.SINGLE, Refused_Sample_Result);
            }
        });
        mTVSheet = view.findViewById(R.id.tv_handle_sheet);
        if (!TextUtils.isEmpty(taskRefusedEntity.refusefile)) {
            mTVSheet.setText(taskRefusedEntity.refusefile);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskRefusedEntity = (TaskEntity) getArguments().get("task");

        taskDetailViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);
        taskDetailViewModel.orderDetailLiveData.observe(getActivity(), new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskRefusedEntityLoadDataModel) {
                if (taskRefusedEntityLoadDataModel.isSuccess()) {
                    taskRefusedEntity = taskRefusedEntityLoadDataModel.getData();
                    initData();
                }

            }
        });
        TaskEntity localEntity = LocalTaskListManager.getInstance().query(taskRefusedEntity);
        if(localEntity != null){
            taskRefusedEntity = localEntity;
            initData();

        }else{
            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskRefusedEntity.id);
        }
//        if (!taskRefusedEntity.isLoadLocalData) {
//            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskRefusedEntity.id);
//        } else {
//            findTaskInLocalFile();
//            initData();
//        }
    }

    private void initData() {
        mTextViewCompanyname.setText(taskRefusedEntity.companyname);
        etTips.setText(taskRefusedEntity.remark);
        if (!taskRefusedEntity.taskisok.equals("1")) {
            if(taskRefusedEntity.pics != null){
                taskRefusedEntity.pics.clear();
            }
            if(taskRefusedEntity.voides != null){
                taskRefusedEntity.voides.clear();
            }
        }
        setupRecyclerViewFromServer(mRecyclerViewImageList, taskRefusedEntity.pics);
        setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskRefusedEntity.voides);
        if (null != taskRefusedEntity.voides && !taskRefusedEntity.voides.isEmpty()) {
            rxPermissionTest();
        }
        if (null != taskRefusedEntity.refuse) {
            Gson gson = new Gson();
            String obj1 = gson.toJson(taskRefusedEntity.refuse);
            JsonObject object = new JsonParser().parse(obj1).getAsJsonObject();
            HashMap<String, String> map = new HashMap();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                map.put("refuse." + entry.getKey(), entry.getValue().getAsString());
            }
            taskRefusedEntity.refuseInfoMap = map;
        }
        if (null == taskRefusedEntity.refuseInfoMap)
            taskRefusedEntity.refuseInfoMap = new HashMap<>();
        taskRefusedEntity.refuseInfoMap.put("companyname", taskDetailViewModel.taskEntity.companyname);
        taskRefusedEntity.refuseInfoMap.put("companyaddress", taskDetailViewModel.taskEntity.companyaddress);

        if (TextUtils.isEmpty(taskRefusedEntity.refusefile)) {
            btnUploadRefusedPic.setText("(拍照)上传");
        } else {
            btnUploadRefusedPic.setText("已拍照");
        }

        if (!taskRefusedEntity.isUploadedTask()) {
            btnSave.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            ivChooseVideo.setVisibility(View.VISIBLE);
            ivChooseImage.setVisibility(View.VISIBLE);
        } else {
            btnUploadRefusedPic.setVisibility(View.GONE);
            ivChooseVideo.setVisibility(View.GONE);
            ivChooseImage.setVisibility(View.GONE);
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
//                    if (listTask.get(i).id.equals(taskRefusedEntity.id)) {
//                        taskRefusedEntity = listTask.get(i);
//                    }
//                }
////                if (null != taskDetailViewModel.taskEntity.taskSamples && !taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
////                    for (int j = 0; j < taskDetailViewModel.taskEntity.taskSamples.size(); j++) {
////                        TaskSample taskSample = taskDetailViewModel.taskEntity.taskSamples.get(j);
////                        if (!taskSample.isLocalData) {
////                            taskDetailViewModel.taskEntity.taskSamples.remove(taskSample);
////                        }
////                    }
////                }
//
//            }
//
//        }
//    }
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

//                    for (int i = taskRefusedEntity.voides.size() - 1; i >= 0; i--) {
//                        if (!taskRefusedEntity.voides.get(i).isLocal) {
//                            taskRefusedEntity.voides.remove(i);
//                        }
//                    }

                    if(taskRefusedEntity.voides == null){
                        taskRefusedEntity.voides = new ArrayList<>();
                    }
                    taskRefusedEntity.isLoadLocalData = true;
                    taskRefusedEntity.isEditedTaskScene = true;
                    taskRefusedEntity.voides.addAll(mediaInfos);
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskRefusedEntity.voides);
                }
                break;
                case MediaHelper.REQUEST_IMAGE_CODE_REFUSED: {


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
                    if(taskRefusedEntity.pics == null){
                        taskRefusedEntity.pics = new ArrayList<>();
                    }
                    taskRefusedEntity.pics.addAll(mediaInfos);
                    taskRefusedEntity.isLoadLocalData = true;
                    taskRefusedEntity.isEditedTaskScene = true;
                    setupRecyclerView(mRecyclerViewImageList, taskRefusedEntity.pics);


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
                            taskRefusedEntity.refusefile = data.getStringExtra("pdf");
                            taskRefusedEntity.refuseInfoMap = map;
                            if (!TextUtils.isEmpty(taskRefusedEntity.refusefile)) {
                                mTVSheet.setText(taskRefusedEntity.refusefile);
                            }
                            shareBySystem(data.getStringExtra("pdf"));
                            taskDetailViewModel.sendReportRecord(taskRefusedEntity.id, "", "refuse");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("para", para);
                    }
                    break;
                case Refused_Sample_Result:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskRefusedEntity.refusepicfile = selectHandle.get(0).getPath();
                        if (!TextUtils.isEmpty(taskRefusedEntity.refusepicfile)) {
                            btnUploadRefusedPic.setText("已拍照");
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
        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, taskRefusedEntity.isUploadedTask()));

    }


    /**
     * @param recyclerView
     * @param task
     */
    private void setupRecyclerViewFromServer(@NonNull RecyclerView recyclerView, List task) {
        if(task == null){
            task = new ArrayList();
        }
        ExceptionServerRecyclerViewAdapter adapter = new ExceptionServerRecyclerViewAdapter(getActivity(), task, taskRefusedEntity.isUploadedTask(),fragment);
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
        for (Videos videos : taskRefusedEntity.voides) {
            if (TextUtils.isEmpty(videos.getId())) continue;
            FileDownloader.downloadFile(RetrofitServiceManager.getInstance().createApiService(Request.class).downloadVideo(videos.getId()), Constants.getPath(), videos.getFileName(), new DownloadProgressHandler() {


                @Override
                public void onProgress(int progress, long total, long speed) {
                    LogUtils.i("下载文件中:" + progress / total);
                }

                @Override
                public void onCompleted(File file) {
                    videos.setPath(file.getPath());
                    setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskRefusedEntity.voides);
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
    QMUITipDialog upDialog;
    private void postRefuseData() {
        RefuseForm refuseForm =  DbController.getInstance(getContext()).getDaoSession().getRefuseFormDao().queryBuilder().where(RefuseFormDao.Properties.Id.eq(taskRefusedEntity.id)).build().unique();
        if(refuseForm == null || TextUtils.isEmpty(refuseForm.getRefusePdf())){
            showUpLoadFail("请检表单有没有生成PDF文件。");
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
        requestText.put("id", taskRefusedEntity.id);
        requestText.put("taskisok", "1");//任务异常状态0正常1拒检2未抽样到单位
        requestText.put("remark", etTips.getText().toString());
        requestText.put("refuse.producename",taskRefusedEntity.id);


        if (null != taskRefusedEntity.taskSamples && !taskRefusedEntity.taskSamples.isEmpty()) {
            requestText.put("samplecount", taskRefusedEntity.taskSamples.size() + "");
        } else {
            requestText.put("samplecount", "0");
        }
        AMapLocation location = MainApplication.INSTANCE.getMyLocation();
        if (null != location) {
            requestText.put("taskaddress", location.getAddress() + "");
            requestText.put("longitude", location.getLongitude() + "");
            requestText.put("latitude", location.getLatitude() + "");
        }
        if (null != taskRefusedEntity.pics && !taskRefusedEntity.pics.isEmpty()) {

            for (int i = 0; i < taskRefusedEntity.pics.size(); i++) {
                Pics pics = taskRefusedEntity.pics.get(i);
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
        if (null != taskRefusedEntity.voides && !taskRefusedEntity.voides.isEmpty()) {

            for (int i = 0; i < taskRefusedEntity.voides.size(); i++) {
                Videos videos = taskRefusedEntity.voides.get(i);
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


        {
            String path = refuseForm.getRefusePdf();
            File refusefile = new File(path);
            if (refusefile.exists()) {
                requestFile.put("refusefile", path);//抽样单
            }
        }

        {
            HashMap<String, String> refuseInfoMap = taskRefusedEntity.refuseInfoMap;
            if (null != refuseInfoMap && !refuseInfoMap.isEmpty()) {
                for (String s : refuseInfoMap.keySet()) {
                    if (!s.startsWith("refuse.")) continue;
                    requestText.put(s, refuseInfoMap.get(s) + "");//抽样单
                }
            }
        }


        {
            File refusepicfile = new File(taskRefusedEntity.refusepicfile);
            if (refusepicfile.exists()) {
                Log.e("file", refusepicfile.getAbsolutePath());
                requestFile.put("refusepicfile", taskRefusedEntity.refusepicfile);//处置单
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
                EventBus.getDefault().postSticky(TaskMessage.getInstance(taskRefusedEntity.id,true));
                LocalTaskListManager.getInstance().remove(taskRefusedEntity);
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
//                        if (listTask.get(i).id.equals(taskRefusedEntity.id)) {
//                            listTask.remove(i);
//                        }
//                    }
//                }
//            }
//            if (!isRemove) {
//                taskRefusedEntity.taskisok = 1 + "";
//                listTask.add(taskRefusedEntity);
//            }
//            SPUtil.put(getActivity(), "tasklist", gson.toJson(listTask));
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        if (isRemove) {
//            getActivity().finish();
//        } else {
//            com.product.sampling.maputil.ToastUtil.show(getActivity(), "保存成功");
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
                    postRefuseData();
                } else if (index == 2) {
                    Intent intent = new Intent(getActivity(), RefuseFormActivity.class);
                    Bundle b = new Bundle();
                    //跳转到处置单
                    String taskId = taskRefusedEntity.id;//任务id
                    String tasktypecount = taskRefusedEntity.tasktypecount;//产品名称
                    String companyname = taskRefusedEntity.companyname;//企业名称
                    b.putString("taskId",taskId);
                    b.putString("tasktypecount",tasktypecount);
                    b.putString("companyname",companyname);
                    intent.putExtras(b);
                    RefuseForm refuseForm = DBManagerFactory.getInstance().getRefuseFormManager().queryBuilder().where(RefuseFormDao.Properties.Id.eq(taskId)).build().unique();
                    if(refuseForm == null){
                        //创建数据库
                        RefuseForm refuseFormNew = new RefuseForm();
                        refuseFormNew.setId(taskId);
                        refuseFormNew.setRefuseQymc(companyname);
                        DbController.getInstance(getActivity()).getDaoSession().getRefuseFormDao().insert(refuseFormNew);
                    }
                    startActivity(intent);
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
}