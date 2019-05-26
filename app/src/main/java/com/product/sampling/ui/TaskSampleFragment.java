package com.product.sampling.ui;

import android.app.Activity;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.product.sampling.R;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.product.sampling.Constants.IMAGE_BASE_URL;

/**
 * 样品信息
 */
public class TaskSampleFragment extends BasePhotoFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

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
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
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
        mRecyclerView = rootView.findViewById(R.id.item_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        rootView.findViewById(R.id.btn_save).setOnClickListener(this);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
        recyclerView.setAdapter(new TaskSampleRecyclerViewAdapter((AppCompatActivity) getActivity(), this, task, false));
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
//            postSample();
            postSampleByBody();

        }
    }


    private void postSample() {
        File file = new File("/storage/emulated/0/table.pdf");
        if (!file.exists()) {
            Log.e("file", file.getAbsolutePath());
            ToastUtil.showToast(getActivity(), "无文件");
            return;
        }
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);//把文件与类型放入请求体
        MultipartBody.Part part = MultipartBody.Part
                .createFormData("userid", AccountManager.getInstance().getUserId())
                .createFormData("taskid", taskDetailViewModel.taskEntity.id)
                .createFormData("id", "1111")
                .createFormData("islastone", "1")
                .createFormData("advice.companyname", "1")
                .createFormData("samplingfile", file.getName(), requestFile)
                .createFormData("sampling.taskfrom", "1")
                .createFormData("disposalfile", file.getName(), requestFile)

                .createFormData("picstrs", "1111")
                .createFormData("uploadpics", file.getName(), requestFile);

        Map<String, MultipartBody> array = new HashMap<>();
        Map<String, MultipartBody> arrayStr = new HashMap<>();

//        for (int i = 0; i < taskDetailViewModel.imageList.size(); i++) {
//            TaskImageEntity taskImageEntity = taskDetailViewModel.imageList.get(i);
//            File f = new File(taskImageEntity.getOriginalPath());
//            if (!f.exists()) {
//                continue;
//            }
//
//            RequestBody imageBody = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
////            requestBody.addFormDataPart("uploadpics", f.getName(), imageBody);//把文件与类型放入请求体)
////            requestBody.addFormDataPart("picstrs", "鞍山");//把文件与类型放入请求体)
//            MultipartBody.Builder imgRequestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("uploadpics", f.getName(), imageBody);
//
//            array.put("uploadpics", imgRequestBody.build());
//
//            MultipartBody.Builder strRequestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("picstrs", f.getName(), RequestBody.create(MultipartBody.FORM, "sss"));
//            arrayStr.put("picstrs", strRequestBody.build());
//
//        }

        RequestBody userid = RequestBody.create(null, AccountManager.getInstance().getUserId());
        RequestBody taskid = RequestBody.create(null, taskDetailViewModel.taskEntity.id);
        RequestBody id = RequestBody.create(null, "111111111");

        RetrofitService.createApiService(Request.class)
                .savesample(id, taskid, userid, part)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "添加成功" + s);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), message);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
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

//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("userid", AccountManager.getInstance().getUserId())
//                .addFormDataPart("taskid", taskDetailViewModel.taskEntity.id)
//                .addFormDataPart("id", "1111")
//                .addFormDataPart("islastone", "1")
//                .addFormDataPart("advice.companyname", "1")
//                .addFormDataPart("samplingfile", file.getName(), requestFile)
//                .addFormDataPart("sampling.taskfrom", "1")
//                .addFormDataPart("disposalfile", file.getName(), requestFile)
//                .addFormDataPart("picstrs", "1111")
//                .addFormDataPart("uploadpics", file.getName(), requestFile).build();


        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        Map<String, RequestBody> array = new HashMap<>();
        Map<String, RequestBody> arrayStr = new HashMap<>();
        //遍历map中所有参数到builder

        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key


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

        for (int i = 0; i < taskDetailViewModel.taskList.get(0).list.size(); i++) {
            List<TaskImageEntity> list = taskDetailViewModel.taskList.get(0).list;
            if (null == list || list.isEmpty()) {
                ToastUtil.showToast(getActivity(), "请选择图片");
                return;
            }
            File f = new File(list.get(i).getOriginalPath());
            if (!f.exists()) {
                Log.e("file", f.getAbsolutePath());
                ToastUtil.showToast(getActivity(), "无效图片");
                return;
            }
            RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
            multipartBodyBuilder.addFormDataPart("picstrs", "8888")
                    .addFormDataPart("uploadpics", f.getName(), requestImage);
        }


        RetrofitService.createApiService(Request.class)
                .savesampleByBody(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
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
                        com.product.sampling.maputil.ToastUtil.show(getActivity(), message + "");
                    }
                });
    }
}
