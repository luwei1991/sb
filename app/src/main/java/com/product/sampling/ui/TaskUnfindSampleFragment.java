package com.product.sampling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.adapter.ImageAndTextRecyclerViewAdapter;
import com.product.sampling.adapter.ImageServerRecyclerViewAdapter;
import com.product.sampling.adapter.VideoAndTextRecyclerViewAdapter;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.bean.Videos;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.viewmodel.TaskExecptionViewModel;

import org.devio.takephoto.model.TImage;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 未抽到样品
 */
public class TaskUnfindSampleFragment extends BasePhotoFragment {

    RecyclerView mRecyclerViewImageList;
    RecyclerView mRecyclerViewVideoList;
    static TaskUnfindSampleFragment fragment;
    TaskExecptionViewModel taskExecptionViewModel;

    public TaskUnfindSampleFragment() {

    }
    public static Fragment newInstance(TaskEntity task) {
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

        // add
        initView(rootView);
        return rootView;
    }


    /**
     * init view.
     * @param view
     */
    private void initView(View view){

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


        // 现场照片选择
        view.findViewById(R.id.iv_choose).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                selectPhoto(10,false,true,false);
            }
        });



        // 现场视频选择
        view.findViewById(R.id.iv_choose_video).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                PictureSelector.create(TaskUnfindSampleFragment.this)
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


        // TODO 保存并提交
        // 保存并提交
        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

            }
        });



        // TODO 保存
        // 保存
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

            }
        });


        // TODO 编辑单据并打印
        // 编辑单据并打印
        view.findViewById(R.id.btn_edit_handling_sheet).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

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

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskExecptionViewModel = ViewModelProviders.of(getActivity()).get(TaskExecptionViewModel.class);

        TaskEntity taskEntity = taskExecptionViewModel.taskEntity;

        taskExecptionViewModel.requestOrderList(AccountManager.getInstance().getUserId(), taskEntity.id);
        taskExecptionViewModel.orderDetailLiveData.observe(this, new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskEntityLoadDataModel) {
                taskExecptionViewModel.taskEntity = taskEntityLoadDataModel.getData();
                taskExecptionViewModel.isImageRequestFromServer = true;
                taskExecptionViewModel.isVideoRequestFromServer = true;

                if (taskExecptionViewModel.taskEntity != null) {
                    setupRecyclerViewFromServer(mRecyclerViewImageList, taskExecptionViewModel.taskEntity.pics);
                    setupRecyclerViewVideoFromServer(mRecyclerViewVideoList, taskExecptionViewModel.taskEntity.voides);
                }
            }
        });
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

                    for (LocalMedia media:selectList){
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

                    for (int i = 0; i < taskExecptionViewModel.taskEntity.voides.size(); i++) {
                        if (!taskExecptionViewModel.taskEntity.voides.get(i).isLocal) {
                            taskExecptionViewModel.taskEntity.voides.remove(i);
                        }
                    }
                    taskExecptionViewModel.isVideoRequestFromServer = false;
                    taskExecptionViewModel.taskEntity.voides.addAll(mediaInfos);
                    setupRecyclerViewVideo(mRecyclerViewVideoList, taskExecptionViewModel.taskEntity.voides);
                    break;

            }
        }
    }

    /**
     * show image.
     * @param images
     */
    @Override
    public void showResultImages(ArrayList<TImage> images) {
        super.showResultImages(images);

        for (TImage image:
                images) {
            TaskImageEntity taskImageEntity = new TaskImageEntity();
            taskImageEntity.setCompressPath(image.getCompressPath());
            taskImageEntity.setOriginalPath(image.getOriginalPath());
            taskImageEntity.setFromType(image.getFromType());

            taskExecptionViewModel.imageList.add(taskImageEntity);
        }
        taskExecptionViewModel.isImageRequestFromServer = false;
        setupRecyclerView(mRecyclerViewImageList, taskExecptionViewModel.imageList);

    }

    // help method.

    /**
     * display images
     * @param recyclerView
     * @param task
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
//        ImageAndTextRecyclerViewAdapter adapter = new ImageAndTextRecyclerViewAdapter(this, task, true);
//        recyclerView.setAdapter(adapter);
    }


    /**
     * display video
     * @param mRecyclerViewVideoList
     * @param videoList
     */
    private void setupRecyclerViewVideo(RecyclerView mRecyclerViewVideoList, List<Videos> videoList) {
//        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(this, videoList, this, true));
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
     *
     * @param recyclerView
     * @param task
     */
    private void setupRecyclerViewFromServer(@NonNull RecyclerView recyclerView, List task) {
        ImageServerRecyclerViewAdapter adapter = new ImageServerRecyclerViewAdapter(getActivity(), task, false);
        recyclerView.setAdapter(adapter);
    }

    /**
     *
     * @param mRecyclerViewVideoList
     * @param videoList
     */
    private void setupRecyclerViewVideoFromServer(RecyclerView mRecyclerViewVideoList, List<Videos> videoList) {
//        mRecyclerViewVideoList.setAdapter(new VideoAndTextRecyclerViewAdapter(getActivity(), videoList, this, false));
    }


}
