package com.product.sampling.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.LocalMediaInfo;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.dummy.DummyContent;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.ToastUtil;

import org.devio.takephoto.model.TImage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

    public static final String ARG_ITEM_ID = "item_id";
    public static final int Select_Check = 101;
    public static final int Select_Handle = 102;

    public static final String TAG = TaskSampleFragment.class.getSimpleName();
    public int selectId = -1;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView = rootView.findViewById(R.id.item_image_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.btn_save).setOnClickListener(this);
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
    public void showResultImages(ArrayList<TImage> images, int pos) {
        List<Pics> imageList = new ArrayList<>();
        for (TImage image :
                images) {
            Pics pics = new Pics();
            pics.setCompressPath(image.getCompressPath());
            pics.setOriginalPath(image.getOriginalPath());
            pics.setFromType(image.getFromType());
            imageList.add(pics);
        }
        if (pos > -1 && taskDetailViewModel.taskList.size() > pos) {
            taskDetailViewModel.taskList.get(pos).setPics(imageList);
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
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
            postSampleByBody();
        } else if (R.id.btn_save_upload == v.getId()) {
            postSampleByBody();
        }
    }

    void createNewSample(String text) {
        if (null != text && !text.isEmpty()) {
            TaskSample sample = new TaskSample();
            sample.setRemarks(text);
            sample.list = new ArrayList<>();
            sample.isLocalData = true;
            taskDetailViewModel.taskList.add(sample);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        if (taskDetailViewModel.taskList.isEmpty()) {
            TaskSample sample = new TaskSample();
            sample.setRemarks("北京");
            sample.list = new ArrayList<>();
            taskDetailViewModel.taskList.add(sample);
        }
        setupRecyclerView(mRecyclerView, taskDetailViewModel.taskList, true);

        taskDetailViewModel.requestTasksamples(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
        taskDetailViewModel.sampleDetailLiveData.observe(this, new Observer<LoadDataModel<List<TaskSample>>>() {
            @Override
            public void onChanged(LoadDataModel<List<TaskSample>> taskEntityLoadDataModel) {
                if (taskEntityLoadDataModel.isSuccess()) {
                    taskDetailViewModel.taskList = taskEntityLoadDataModel.getData();
                    setupRecyclerView(mRecyclerView, taskDetailViewModel.taskList, false);
                }
            }
        });
    }

    private void postSampleByBody() {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("taskid", taskDetailViewModel.taskEntity.id)
                .addFormDataPart("id", System.currentTimeMillis() + "")
                .addFormDataPart("islastone", "1");
        if (null == taskDetailViewModel.taskList || taskDetailViewModel.taskList.isEmpty()) {
            ToastUtil.showToast(getActivity(), "请创建样品数据");
            return;
        }
        for (int i = 0; i < taskDetailViewModel.taskList.size(); i++) {
            if (!taskDetailViewModel.taskList.get(i).isLocalData) continue;
            File file = new File(taskDetailViewModel.taskList.get(i).checkSheet);
            if (file.exists()) {
                Log.e("file", file.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("samplingfile", file.getName(), requestFile);//抽样单
                HashMap<String, String> map = taskDetailViewModel.taskList.get(i).checkInfo;
                for (String s : map.keySet()) {
                    multipartBodyBuilder.addFormDataPart(s, map.get(s));//抽样单
                }
            }
            File fileHandle = new File(taskDetailViewModel.taskList.get(i).handleSheet);
            if (fileHandle.exists()) {
                Log.e("file", fileHandle.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, fileHandle);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("disposalfile", file.getName(), requestFile);//处置单
                HashMap<String, String> map = taskDetailViewModel.taskList.get(i).handleInfo;
                for (String s : map.keySet()) {
                    multipartBodyBuilder.addFormDataPart(s, map.get(s));//抽样单
                }
            }

            List<Pics> list = taskDetailViewModel.taskList.get(i).getPics();

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
//            List<LocalMediaInfo> listVideo = taskDetailViewModel.taskList.get(i).videoList;

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
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    if (selectId != -1) {
                        List<LocalMediaInfo> mediaInfos = new ArrayList<>();
                        for (LocalMedia media :
                                selectList) {
                            LocalMediaInfo mediaInfo = new LocalMediaInfo();
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

                            mediaInfos.add(mediaInfo);
                        }
                        taskDetailViewModel.taskList.get(selectId).videoList.addAll(mediaInfos);
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
                                    taskDetailViewModel.taskList.get(index).checkSheet = data.getStringExtra("pdf");
                                    taskDetailViewModel.taskList.get(index).checkInfo = map;
                                } else if (pos == 2) {
                                    taskDetailViewModel.taskList.get(index).handleInfo = map;
                                    taskDetailViewModel.taskList.get(index).handleSheet = data.getStringExtra("pdf");
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
                        int index = data.getIntExtra("task", -1);
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskList.get(selectId).handleSheet = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }

                    break;
                case Select_Check:
                    if (data != null) {
                        int index = data.getIntExtra("task", -1);
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskList.get(selectId).checkSheet = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    break;


            }
        }
    }

    /**
     * 取选择图片
     *
     * @param index      当前第几个样品
     * @param requstCode
     */
    public void startGallery(int index, int requstCode) {
        PictureSelector.create(TaskSampleFragment.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                        .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(false)// 是否压缩 true or false
                .glideOverride(200, 200)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(3, 4)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(true)// 是否显示gif图片 true or false
//                .compressSavePath(getPath())//压缩图片保存地址
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
                .forResult(requstCode);//结果回调onActivityResult code
    }
}
