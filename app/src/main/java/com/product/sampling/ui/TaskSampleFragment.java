package com.product.sampling.ui;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.LocalMediaInfo;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.dummy.DummyContent;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.ToastUtil;

import org.devio.takephoto.model.TImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

/**
 * 样品信息
 */
public class TaskSampleFragment extends BasePhotoFragment implements View.OnClickListener {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = TaskSampleFragment.class.getSimpleName();
    public int selectId = -1;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    RecyclerView mRecyclerView;
    TaskDetailViewModel taskDetailViewModel;
    private static TaskSampleFragment fragment;
    Button btnSave;

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
        rootView.findViewById(R.id.tv_create).setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView = rootView.findViewById(R.id.item_image_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        rootView.findViewById(R.id.btn_save).setOnClickListener(this);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List list) {
        recyclerView.setAdapter(new TaskSampleRecyclerViewAdapter(getActivity(), this, list, false));
    }

    @Override
    public void showResultImages(ArrayList<TImage> images, int pos) {
        ArrayList<TaskImageEntity> imageList = new ArrayList<>();
        for (TImage image :
                images) {
            TaskImageEntity taskImageEntity = new TaskImageEntity();
            taskImageEntity.setCompressPath(image.getCompressPath());
            taskImageEntity.setOriginalPath(image.getOriginalPath());
            taskImageEntity.setFromType(image.getFromType());
            imageList.add(taskImageEntity);
        }
        if (pos > -1 && taskDetailViewModel.taskList.size() > pos) {
            taskDetailViewModel.taskList.get(pos).list = imageList;
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

        }
    }

    void createNewSample(String text) {
        if (null != text && !text.isEmpty()) {
            TaskSample sample = new TaskSample();
            sample.title = text;
            sample.list = new ArrayList<>();
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
            sample.title = "北京";
            sample.list = new ArrayList<>();
            taskDetailViewModel.taskList.add(sample);
            setupRecyclerView(mRecyclerView, taskDetailViewModel.taskList);
        }
    }

    private void postSampleByBody() {
        File file = new File("/storage/emulated/0/table.pdf");
        if (!file.exists()) {
            Log.e("file", file.getAbsolutePath());
            ToastUtil.showToast(getActivity(), "无效文件");
            return;
        }
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);//把文件与类型放入请求体

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("taskid", taskDetailViewModel.taskEntity.id)
                .addFormDataPart("id", "7777")
                .addFormDataPart("islastone", "1")
                .addFormDataPart("advice.companyname", "1")
                .addFormDataPart("samplingfile", file.getName(), requestFile)
                .addFormDataPart("sampling.taskfrom", "1")
                .addFormDataPart("disposalfile", file.getName(), requestFile);

        if (null == taskDetailViewModel.taskList || taskDetailViewModel.taskList.isEmpty()) {
            ToastUtil.showToast(getActivity(), "请创建样品数据");
            return;
        }
        for (int i = 0; i < taskDetailViewModel.taskList.size(); i++) {
            List<TaskImageEntity> list = taskDetailViewModel.taskList.get(i).list;

            if (null == list || list.isEmpty()) {
                ToastUtil.showToast(getActivity(), "请选择图片");
                continue;
            }
            for (TaskImageEntity entity : list) {
                File f = new File(entity.getOriginalPath());
                if (!f.exists()) {
                    Log.e("file", f.getAbsolutePath());
                    ToastUtil.showToast(getActivity(), "无效图片");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("picstrs", entity.title)
                        .addFormDataPart("uploadpics", f.getName(), requestImage);
            }
            List<LocalMediaInfo> listVideo = taskDetailViewModel.taskList.get(i).videoList;

            for (LocalMediaInfo localMedia :
                    listVideo) {
                File f = new File(localMedia.getPath());
                if (!f.exists()) {
                    Log.e("file", f.getAbsolutePath());
                    ToastUtil.showToast(getActivity(), "无效视频");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("videostrs", localMedia.title)
                        .addFormDataPart("uploadvideos", f.getName(), requestImage);
            }
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
                            com.product.sampling.maputil.ToastUtil.show(getActivity(), s);
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
                case 1:
                    if (data != null) {
                        int index = data.getIntExtra("task", -1);
                        if (index != -1) {
                            //把生成的pdf 赋值给列表
                            taskDetailViewModel.taskList.get(index).checkSheet = data.getStringExtra("pdf");
                            taskDetailViewModel.taskList.get(index).handleSheet = data.getStringExtra("pdf");
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    }
}
