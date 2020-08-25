package com.product.sampling.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.adapter.ImageServerRecyclerViewAdapter;
import com.product.sampling.adapter.VideoAndTextRecyclerViewAdapter;
import com.product.sampling.bean.Advice;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.Videos;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.eventmessage.CurItemMessage;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.FileDownloader;
import com.product.sampling.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.product.sampling.ui.TaskDetailActivity.TASK_NOT_UPLOAD;
import static com.product.sampling.ui.TaskDetailActivity.TASK_UPLOADED;

/**
 * 现场信息
 */
public class TaskSceneFragment extends BasePhotoFragment {

    RecyclerView mRecyclerViewImageList;
    RecyclerView mRecyclerViewVideoList;
    static TaskSceneFragment fragment;
    TaskDetailViewModel taskDetailViewModel;
    EditText companyname;
    EditText companyaddress;
    EditText companytel;
    EditText remark;
    ImageView ivChooseVideo;
    ImageView ivChooseImage;
    Button btnSubmit;
//    public static final String TASK_STATUS_NOT_UPLOAD = "-1";
    //已上传不能编辑
    private boolean canEdit = true;
    private int curPos;
    private  ImageServerRecyclerViewAdapter imageAdapter;
    public static final String [] defaultRemarks = new String []{
            "大门照",
            "告知场景",
            "核查证照",
            "成品仓/待销区",
            "随机抽样",
            "受检单位签字",
            "合照"
    };
    private List<Pics> defaultImgList = new ArrayList<>();
    public TaskSceneFragment() {

    }

    public static TaskSceneFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putSerializable("task", task);
        if (fragment == null) {
            fragment = new TaskSceneFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scene_detail, container, false);
        companyname = rootView.findViewById(R.id.companyname);

        companyaddress = rootView.findViewById(R.id.companyaddress);

        companytel = rootView.findViewById(R.id.companytel);

        remark = rootView.findViewById(R.id.remark);

        TextView tv = rootView.findViewById(R.id.tv_step_2);
        tv.setBackgroundResource(R.drawable.bg_circle_blue);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerViewImageList = rootView.findViewById(R.id.item_image_list);
        mRecyclerViewImageList.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerViewVideoList = rootView.findViewById(R.id.item_video_list);
        mRecyclerViewVideoList.setLayoutManager(linearLayoutManager);
        ivChooseImage = rootView.findViewById(R.id.iv_choose);


        btnSubmit = rootView.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canEdit){//如果能够编辑就保存
                    saveData(true);
                }

            }
        });
        ivChooseVideo = rootView.findViewById(R.id.iv_choose_video);
        ivChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startVideo(TaskSceneFragment.this, MediaHelper.REQUEST_VIDEO_CODE);
            }
        });


        return rootView;
    }

    /**
     *
     * @param isShowToast 是否提示保存 true:提示，false：不提示
     */
    protected void saveData(boolean isShowToast) {
        taskDetailViewModel.taskEntity.companyaddress = companyaddress.getText().toString();
        taskDetailViewModel.taskEntity.companyname = companyname.getText().toString();
        taskDetailViewModel.taskEntity.companytel = companytel.getText().toString();
        taskDetailViewModel.taskEntity.remark = remark.getText().toString();
        taskDetailViewModel.taskEntity.taskstatus = TASK_NOT_UPLOAD;
        taskDetailViewModel.taskEntity.pics = imageAdapter.getData();
        LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
        EventBus.getDefault().postSticky(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id,false));
        if(isShowToast){
            Toast.makeText(getActivity(),"保存成功！",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(canEdit){//如果能够编辑就保存
            saveData(false);
        }

    }


    //查看图片代码
    private void setupRecyclerViewFromServer(@NonNull RecyclerView recyclerView, List<Pics> task, Fragment fragment) {
        if(task == null){
            task = new ArrayList();
        }
        //将非必须填写的图片放在最后一位
        ArrayList<Pics> tempMust = new ArrayList<>();
        ArrayList<Pics> tempNotMust = new ArrayList<>();
        for(int i = 0 ; i < task.size(); i++){
            String remarks = task.get(i).getRemarks();
            if(remarks.contains(defaultRemarks[0]) || remarks.contains(defaultRemarks[1]) || remarks.contains(defaultRemarks[2]) || remarks.contains(defaultRemarks[3]) ||
                    remarks.contains(defaultRemarks[4]) ||remarks.contains(defaultRemarks[5])||remarks.contains(defaultRemarks[6])){
                tempMust.add(task.get(i));
            }else{
                tempNotMust.add(task.get(i));
            }
        }
        tempMust.addAll(tempNotMust);//将其他图片添加到末尾
        if(imageAdapter == null){
            imageAdapter = new ImageServerRecyclerViewAdapter(getActivity(), tempMust, canEdit,fragment);//如果canEdit为false就是不能编辑
            recyclerView.setAdapter(imageAdapter);

        }else{
            imageAdapter.setData(tempMust,canEdit);
            recyclerView.setAdapter(imageAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onGetCurPosEvent(CurItemMessage message) {
      curPos = message.getCurPost();
    }


    @SuppressLint("FragmentLiveDataObserve")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        canEdit = !taskDetailViewModel.taskEntity.taskstatus.equals(TASK_UPLOADED);
        setEditTextEnable(companyname, canEdit);
        setEditTextEnable(companyaddress, canEdit);
        setEditTextEnable(companytel, canEdit);
        setEditTextEnable(remark, canEdit);
        TaskEntity localEntity = LocalTaskListManager.getInstance().query(taskDetailViewModel.taskEntity);
        if(localEntity != null){//本地查询有数据
            taskDetailViewModel.taskEntity = localEntity;
            if (!taskDetailViewModel.taskEntity.taskisok.equals("0")) {//异常任务（未抽到，拒检）
                taskDetailViewModel.taskEntity.pics.clear();
                taskDetailViewModel.taskEntity.voides.clear();
            }
            setupRecyclerViewFromServer(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics,fragment);
            setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
            companyname.setText(taskDetailViewModel.taskEntity.companyname);
            companyaddress.setText(taskDetailViewModel.taskEntity.companyaddress);
            companytel.setText(taskDetailViewModel.taskEntity.companytel);
            remark.setText(taskDetailViewModel.taskEntity.remark);
//            if (taskDetailViewModel.taskEntity.isCirculationDomain()) {
//                boolean canEdit = !taskDetailViewModel.taskEntity.isUploadedTask();
//                setEditTextEnable(companyname, true && canEdit);
//                setEditTextEnable(companyaddress, true && canEdit);
//                setEditTextEnable(companytel, true && canEdit);
//                setEditTextEnable(remark, true && canEdit);
//            }
        }else{//本地查询无数据
            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
        }
//        findTaskInLocalFile();
        //刷新本地图片和视频列表
//        if (taskDetailViewModel.taskEntity.isLoadLocalData) {
//            if (!taskDetailViewModel.taskEntity.taskisok.equals("0")) {//异常任务（未抽到，拒检）
//                taskDetailViewModel.taskEntity.pics.clear();
//                taskDetailViewModel.taskEntity.voides.clear();
//            }
//            setupRecyclerViewFromServer(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics,fragment);
//            setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
//            companyname.setText(taskDetailViewModel.taskEntity.companyname);
//            companyaddress.setText(taskDetailViewModel.taskEntity.companyaddress);
//            companytel.setText(taskDetailViewModel.taskEntity.companytel);
//            remark.setText(taskDetailViewModel.taskEntity.remark);
//            if (taskDetailViewModel.taskEntity.isCirculationDomain()) {
//                boolean canEdit = !taskDetailViewModel.taskEntity.isUploadedTask();
//                setEditTextEnable(companyname, true && canEdit);
//                setEditTextEnable(companyaddress, true && canEdit);
//                setEditTextEnable(companytel, true && canEdit);
//                setEditTextEnable(remark, true && canEdit);
//            }
//        } else {
//            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
//        }


        taskDetailViewModel.orderDetailLiveData.observeForever(new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskEntityLoadDataModel) {
                //如果服务端有数据，本地数据为空，则显示服务端数据
                if (taskEntityLoadDataModel.isSuccess() && localEntity == null) {
                    taskDetailViewModel.taskEntity = taskEntityLoadDataModel.getData();
                    taskDetailViewModel.taskEntity.isLoadLocalData = false;
                    if (!taskDetailViewModel.taskEntity.taskisok.equals("0")) {
                        taskDetailViewModel.taskEntity.pics.clear();
                        taskDetailViewModel.taskEntity.voides.clear();
                    }
                    if(taskDetailViewModel.taskEntity.pics.size() == 0){
                        defaultImgList.clear();
                        for(int i = 0; i < defaultRemarks.length; i++){
                            Pics pics = new Pics();
                            pics.setRemarks(defaultRemarks[i]);
                            defaultImgList.add(pics);
                        }
                        taskDetailViewModel.taskEntity.pics.addAll(defaultImgList);
                    }
                    setupRecyclerViewFromServer(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics,TaskSceneFragment.this);
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
                    if (null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {
                        rxPermissionTest();
                    }
//                    if (taskDetailViewModel.taskEntity.isCirculationDomain()) {
//                        boolean canEdit = !taskDetailViewModel.taskEntity.isUploadedTask();
//                        setEditTextEnable(companyname, true && canEdit);
//                        setEditTextEnable(companyaddress, true && canEdit);
//                        setEditTextEnable(companytel, true && canEdit);
//                        setEditTextEnable(remark, true && canEdit);
//                    }
                    companyname.setText(taskDetailViewModel.taskEntity.companyname);
                    companyaddress.setText(taskDetailViewModel.taskEntity.companyaddress);
                    companytel.setText(taskDetailViewModel.taskEntity.companytel);
                    remark.setText(taskDetailViewModel.taskEntity.remark);

                    Advice advice = taskDetailViewModel.taskEntity.advice;
                    if (null != advice) {
                        Gson gson = new Gson();
                        String obj2 = gson.toJson(advice);
                        JsonObject object = new JsonParser().parse(obj2).getAsJsonObject();
                        HashMap<String, String> map = new HashMap();
                        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                            map.put("advice." + entry.getKey(), entry.getValue().getAsString());
                        }
                        taskDetailViewModel.taskEntity.adviceInfoMap = map;
                    }
                    if (taskDetailViewModel.taskEntity.isUploadedTask()) {
                        ivChooseImage.setVisibility(View.GONE);
                        ivChooseVideo.setVisibility(View.GONE);
                    }

                }
            }
        });

    }


    private void setupRecyclerViewVideo(RecyclerView mRecyclerViewVideoList, List<Videos> videoList) {
        if(videoList != null){
            mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, taskDetailViewModel.taskEntity.isUploadedTask()));
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

                    taskDetailViewModel.taskEntity.isEditedTaskScene = true;
                    taskDetailViewModel.taskEntity.voides.addAll(mediaInfos);
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
                }
                break;
                case MediaHelper.REQUEST_IMAGE_CODE: {
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if(selectList.size() == 1){//为单选的时候
                        Pics mediaInfo = new Pics();
                        LocalMedia localMedia = selectList.get(0);
                        mediaInfo.setOriginalPath(localMedia.getPath());
                        mediaInfo.setCompressPath(localMedia.getCompressPath());
                        mediaInfo.setCompressed(localMedia.isCompressed());
                        if(taskDetailViewModel.taskEntity.pics.size() == curPos){
                            mediaInfo.setRemarks("其他");
                            taskDetailViewModel.taskEntity.pics.add(mediaInfo);
                        }else{
                            if(curPos > taskDetailViewModel.taskEntity.pics.size()){
                                return;
                            }
                            mediaInfo.setRemarks(taskDetailViewModel.taskEntity.pics.get(curPos).getRemarks());
                            taskDetailViewModel.taskEntity.pics.set(curPos,mediaInfo);
                        }
                    }else{
                        //图片为多选的时候
                        for(LocalMedia localMedia :selectList){
                            Pics mediaInfo = new Pics();
                            mediaInfo.setOriginalPath(localMedia.getPath());
                            mediaInfo.setCompressPath(localMedia.getCompressPath());
                            mediaInfo.setCompressed(localMedia.isCompressed());
                            mediaInfo.setRemarks("其他");
                            taskDetailViewModel.taskEntity.pics.add(mediaInfo);
                        }

                    }
                    setupRecyclerViewFromServer(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics,fragment);

                }
                break;
            }
        }

    }


    // help method

    private void setEditTextEnable(EditText mEditText, boolean canEdit) {
        mEditText.setFocusable(canEdit);
        mEditText.setFocusableInTouchMode(canEdit);
        if (!canEdit) {
            mEditText.setOnClickListener(null);
        }
    }

//    private void findTaskInLocalFile() {
//        Gson gson = new Gson();
//        String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
//        if (!TextUtils.isEmpty(taskListStr)) {
//            Type listType = new TypeToken<List<TaskEntity>>() {
//            }.getType();
//            listTask = gson.fromJson(taskListStr, listType);
//            if (null != listTask && !listTask.isEmpty()) {
//                for (int i = 0; i < listTask.size(); i++) {
//                    if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
//                        taskDetailViewModel.taskEntity = listTask.get(i);
//                        taskDetailViewModel.taskEntity.isLoadLocalData = true;
//                    }
//                }
//            }
//        }
//    }

    private void downLoadVideo() {
        for (Videos videos : taskDetailViewModel.taskEntity.voides) {

            FileDownloader.downloadFile(RetrofitServiceManager.getInstance().createApiService(Request.class).downloadVideo(videos.getId()), Constants.getPath(), videos.getFileName(), new DownloadProgressHandler() {


                @Override
                public void onProgress(int progress, long total, long speed) {
                    LogUtils.i("下载文件中:" + progress / total);
                }

                @Override
                public void onCompleted(File file) {
                    videos.setPath(file.getPath());
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
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
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        downLoadVideo();
                    } else {
                        // At least one permission is denied
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        taskDetailViewModel.orderDetailLiveData.removeObservers(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
