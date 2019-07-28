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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.adapter.ImageAndTextRecyclerViewAdapter;
import com.product.sampling.adapter.ImageServerRecyclerViewAdapter;
import com.product.sampling.adapter.VideoAndTextRecyclerViewAdapter;
import com.product.sampling.bean.Advice;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.Videos;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.FileDownloader;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.SPUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

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


    public TaskSceneFragment() {
    }

    public static TaskSceneFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putParcelable("task", task);
        if (fragment == null) {
            fragment = new TaskSceneFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scene_detail, container, false);

        companyname = rootView.findViewById(R.id.companyname);
        setEditTextEnable(companyname, false);

        companyaddress = rootView.findViewById(R.id.companyaddress);
        setEditTextEnable(companyaddress, false);

        companytel = rootView.findViewById(R.id.companytel);
        setEditTextEnable(companytel, false);

        remark = rootView.findViewById(R.id.remark);
        setEditTextEnable(remark, false);

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
        rootView.findViewById(R.id.iv_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startGallery(TaskSceneFragment.this, PictureConfig.MULTIPLE, MediaHelper.REQUEST_IMAGE_CODE);
            }
        });
        rootView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              postData();
                saveData();
            }
        });

        rootView.findViewById(R.id.iv_choose_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaHelper.startVideo(TaskSceneFragment.this, MediaHelper.REQUEST_VIDEO_CODE);
            }
        });


        return rootView;
    }

    List<TaskEntity> listTask;

    private void saveData() {
        boolean hasData = false;
        if (null != taskDetailViewModel.taskEntity.pics && !taskDetailViewModel.taskEntity.pics.isEmpty()) {
            for (int i = 0; i < taskDetailViewModel.taskEntity.pics.size(); i++) {
                String path = taskDetailViewModel.taskEntity.pics.get(i).getOriginalPath();
                if (TextUtils.isEmpty(path)) continue;
                File f = new File(path);
                if (!f.exists()) {
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
                    continue;
                }
                hasData = true;
            }
        }
        if (null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {

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
                hasData = true;
            }
        }
        if (!hasData) {
            ((TaskDetailActivity) getActivity()).checkSelectMenu(3);
            return;
        }

        listTask = new ArrayList<>();

        Gson gson = new Gson();
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
        listTask.add(taskDetailViewModel.taskEntity);
        String data = gson.toJson(listTask);
        SPUtil.put(getActivity(), "tasklist", data);
        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "保存本地成功");
        if ("2".equals(taskDetailViewModel.taskEntity.plantype)) {
            taskDetailViewModel.taskEntity.companyaddress = companyaddress.getText().toString();
            taskDetailViewModel.taskEntity.companyname = companyname.getText().toString();
        }
        ((TaskDetailActivity) getActivity()).checkSelectMenu(3);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Pics> task) {
        ImageAndTextRecyclerViewAdapter adapter = new ImageAndTextRecyclerViewAdapter(this, task, true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerViewFromServer(@NonNull RecyclerView recyclerView, List task) {
        ImageServerRecyclerViewAdapter adapter = new ImageServerRecyclerViewAdapter(getActivity(), task, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        findTaskInLocalFile();
        //刷新本地图片和视频列表
        if (taskDetailViewModel.taskEntity.isLoadLocalData) {
            if (!taskDetailViewModel.taskEntity.taskisok.equals("0")) {
                taskDetailViewModel.taskEntity.pics.clear();
                taskDetailViewModel.taskEntity.voides.clear();
            }
            setupRecyclerView(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics);
            setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
        } else {
            taskDetailViewModel.requestDetailList(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
        }
        companyname.setText(taskDetailViewModel.taskEntity.companyname);
        companyaddress.setText(taskDetailViewModel.taskEntity.companyaddress);
        companytel.setText(taskDetailViewModel.taskEntity.companytel);
        remark.setText(taskDetailViewModel.taskEntity.remark);


        taskDetailViewModel.orderDetailLiveData.observe(this, new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskEntityLoadDataModel) {
                if (taskEntityLoadDataModel.isSuccess()) {
                    taskDetailViewModel.taskEntity = taskEntityLoadDataModel.getData();
                    taskDetailViewModel.taskEntity.isLoadLocalData = false;
                    if (!taskDetailViewModel.taskEntity.taskisok.equals("0")) {
                        taskDetailViewModel.taskEntity.pics.clear();
                        taskDetailViewModel.taskEntity.voides.clear();
                    }
                    setupRecyclerViewFromServer(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics);
                    setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
                    if (null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {
                        rxPermissionTest();
                    }
                    if ("2".equals(taskDetailViewModel.taskEntity.plantype)) {
                        setEditTextEnable(companyname, true);
                        setEditTextEnable(companyaddress, true);
                        setEditTextEnable(companytel, true);
                        setEditTextEnable(remark, true);
                    }
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

                }
            }
        });

    }


    private void setupRecyclerViewVideo(RecyclerView mRecyclerViewVideoList, List<Videos> videoList) {
        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, true));
    }

    private void setupRecyclerViewVideoFromServer(RecyclerView mRecyclerViewVideoList, List<Videos> videoList) {
        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, false));
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
                    taskDetailViewModel.taskEntity.isEditedTaskScene = true;
                    taskDetailViewModel.taskEntity.voides.addAll(mediaInfos);
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
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
//                    for (int i = taskDetailViewModel.taskEntity.pics.size() - 1; i >= 0; i--) {
//                        Pics pics1 = taskDetailViewModel.taskEntity.pics.get(i);
//                        if (!pics1.isLocal) {
//                            taskDetailViewModel.taskEntity.pics.remove(pics1);
//                        }
//                    }
                    taskDetailViewModel.taskEntity.pics.addAll(mediaInfos);
                    taskDetailViewModel.taskEntity.isEditedTaskScene = true;
                    setupRecyclerView(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics);
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

    void findTaskInLocalFile() {
        Gson gson = new Gson();
        String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
        if (!TextUtils.isEmpty(taskListStr)) {
            Type listType = new TypeToken<List<TaskEntity>>() {
            }.getType();
            listTask = gson.fromJson(taskListStr, listType);
            if (null != listTask && !listTask.isEmpty()) {
                for (int i = 0; i < listTask.size(); i++) {
                    if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
                        taskDetailViewModel.taskEntity = listTask.get(i);
                    }
                }
                taskDetailViewModel.taskEntity.isLoadLocalData = true;
            }
        }

    }

    private void downLoadVideo() {
        for (Videos videos : taskDetailViewModel.taskEntity.voides) {

            FileDownloader.downloadFile(RetrofitService.createApiService(Request.class).downloadVideo(videos.getId()), Constants.getPath(), videos.getFileName(), new DownloadProgressHandler() {


                @Override
                public void onProgress(int progress, long total, long speed) {
                    LogUtils.i("下载文件中:" + progress / total);
                }

                @Override
                public void onCompleted(File file) {
                    videos.setPath(file.getPath());
                    setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
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
}
