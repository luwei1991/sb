package com.product.sampling.ui;

import android.app.Activity;
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

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.adapter.ImageAndTextRecyclerViewAdapter;
import com.product.sampling.adapter.ImageServerRecyclerViewAdapter;
import com.product.sampling.adapter.VideoAndTextRecyclerViewAdapter;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.bean.Videos;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;

import org.devio.takephoto.model.TImage;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
        setCanNotEdit(companyname);

        companyaddress = rootView.findViewById(R.id.companyaddress);
        setCanNotEdit(companyaddress);

        companytel = rootView.findViewById(R.id.companytel);
        setCanNotEdit(companytel);

        remark = rootView.findViewById(R.id.remark);
        setCanNotEdit(remark);

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
                selectPhoto(10, false, true, false);
            }
        });
        rootView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData();
                saveData();
            }
        });

        rootView.findViewById(R.id.iv_choose_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(TaskSceneFragment.this)
                        .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                        .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(9)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(true)// 是否可预览视频 true or false
                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(false)// 是否裁剪 true or false
                        .compress(true)// 是否压缩 true or false
                        .glideOverride(200, 200)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(3, 4)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                        .isGif(true)// 是否显示gif图片 true or false
                        .compressSavePath(getPath())//压缩图片保存地址
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
//                        .selectionMedia()// 是否传入已选图片 List<LocalMedia> list
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .cropCompressQuality(80)// 裁剪压缩质量 默认90 int
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .cropWH(200, 200)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                        .videoQuality(1)// 视频录制质量 0 or 1 int
                        .videoMaxSecond(6000)// 显示多少秒以内的视频or音频也可适用 int
                        .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
                        .recordVideoSecond(600)//视频秒数录制 默认60s int
                        .isDragFrame(false)// 是否可拖动裁剪框(固定)
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

            }
        });


        return rootView;
    }

    private void saveData() {

        String taskList = (String) SPUtil.get(getActivity(), "tasklist", "");
        if (!TextUtils.isEmpty(taskList)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TaskEntity>>() {
            }.getType();
            List<TaskEntity> list = gson.fromJson(taskList, listType);
            if (null != list && !list.isEmpty()) {
                Observable.fromIterable(list).subscribe(new Consumer<TaskEntity>() {
                    @Override
                    public void accept(TaskEntity entity) throws Exception {
                        if (entity.id == taskDetailViewModel.taskEntity.id) {
                            list.remove(entity);
                        }
                    }
                });
            }
            //to-do

            list.add(null);
        }
    }

    private String getPath() {
        return "/storage/emulated/0/zip";
    }

    private void postData() {

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
        if (!taskDetailViewModel.isImageRequestFromServer && null != taskDetailViewModel.taskEntity.pics && !taskDetailViewModel.taskEntity.pics.isEmpty()) {
            for (int i = 0; i < taskDetailViewModel.taskEntity.pics.size(); i++) {
                File f = new File(taskDetailViewModel.taskEntity.pics.get(i).getOriginalPath());
                if (!f.exists()) {
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("picstrs", taskDetailViewModel.taskEntity.pics.get(i).title)
                        .addFormDataPart("uploadpics", f.getName(), requestImage);
                hasData = true;
            }
        }
        if (!taskDetailViewModel.isVideoRequestFromServer && null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {

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
                multipartBodyBuilder.addFormDataPart("videostrs", taskDetailViewModel.taskEntity.voides.get(i).title)
                        .addFormDataPart("uploadvideos", f.getName(), requestImage);
                hasData = true;
            }
        }
        if (!hasData) {
            com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "请先选择图片或者视频");
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

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, TaskSampleFragment.newInstance(taskDetailViewModel.taskEntity))
                                .commit();
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
        ImageAndTextRecyclerViewAdapter adapter = new ImageAndTextRecyclerViewAdapter(getActivity(), task, true);
        recyclerView.setAdapter(adapter);
    }

    private void setupRecyclerViewFromServer(@NonNull RecyclerView recyclerView, List task) {
        ImageServerRecyclerViewAdapter adapter = new ImageServerRecyclerViewAdapter(getActivity(), task, false);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showResultImages(ArrayList<TImage> images) {

        for (TImage image :
                images) {
            Pics taskImageEntity = new Pics();
            taskImageEntity.setCompressPath(image.getCompressPath());
            taskImageEntity.setOriginalPath(image.getOriginalPath());
            taskImageEntity.setFromType(image.getFromType());
            taskDetailViewModel.taskEntity.pics.add(taskImageEntity);
        }
        taskDetailViewModel.isImageRequestFromServer = false;
        setupRecyclerView(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);

        TaskEntity taskEntity = taskDetailViewModel.taskEntity;
        companyname.setText(taskEntity.companyname);
        companyaddress.setText(taskEntity.companyaddress);
        companytel.setText(taskEntity.companytel);
        remark.setText(taskEntity.remark);

        taskDetailViewModel.requestOrderList(AccountManager.getInstance().getUserId(), taskEntity.id);
        taskDetailViewModel.orderDetailLiveData.observe(this, new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskEntityLoadDataModel) {
                if (taskEntityLoadDataModel.isSuccess()) {
                    taskDetailViewModel.taskEntity = taskEntityLoadDataModel.getData();
                    taskDetailViewModel.isImageRequestFromServer = true;
                    taskDetailViewModel.isVideoRequestFromServer = true;

                    setupRecyclerViewFromServer(mRecyclerViewImageList, taskDetailViewModel.taskEntity.pics);
                    setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
                }
            }
        });
//        setupRecyclerView(mRecyclerViewImageList, taskExecptionViewModel.imageList);
//        setupRecyclerViewVideo(mRecyclerViewVideoList, taskExecptionViewModel.taskEntity.voides);
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
                case PictureConfig.CHOOSE_REQUEST:
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

                    for (int i = 0; i < taskDetailViewModel.taskEntity.voides.size(); i++) {
                        if (!taskDetailViewModel.taskEntity.voides.get(i).isLocal) {
                            taskDetailViewModel.taskEntity.voides.remove(i);
                        }
                    }
                    taskDetailViewModel.isVideoRequestFromServer = false;
                    taskDetailViewModel.taskEntity.voides.addAll(mediaInfos);
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskDetailViewModel.taskEntity.voides);
                    break;
            }
        }

    }


    // help method

    private void setCanNotEdit(EditText mEditText) {
        mEditText.setFocusable(false);
        mEditText.setFocusableInTouchMode(false);
        mEditText.setOnClickListener(null);
    }
}
