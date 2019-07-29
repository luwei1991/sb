package com.product.sampling.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.product.sampling.R;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.Feed;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.Risk;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.bean.Videos;
import com.product.sampling.bean.Work;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.HttpURLConnectionUtil;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.product.sampling.adapter.TaskSampleRecyclerViewAdapter.RequestCodePdf;
import static com.product.sampling.ui.H5WebViewActivity.Intent_Order;

/**
 * 样品信息
 */
public class TaskSampleFragment extends BasePhotoFragment implements View.OnClickListener {
    public static final String TAG = TaskSampleFragment.class.getSimpleName();

    public static final int Select_Check = 101;
    public static final int Select_Handle = 102;
    public static final int Select_Risk = 103;
    public static final int Select_Work = 104;
    public static final int Select_Feed = 105;

    public int selectId = -1;
    public String deleteId = "";

    RecyclerView mRecyclerView;
    TaskDetailViewModel taskDetailViewModel;
    private static TaskSampleFragment fragment;
    TaskSampleRecyclerViewAdapter adapter;
    Button btnUploadFeed;
    TextView tvUploadFeed;
    Button btnUploadAdvice;
    TextView tvUploadAdvice;
    Button btnSave;
    Button btnUpload;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample_detail, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView = rootView.findViewById(R.id.item_image_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        btnSave = rootView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnUpload = rootView.findViewById(R.id.btn_save_upload);
        btnUpload.setOnClickListener(this);

        if (null != getArguments() && null != getArguments().getParcelable("task")) {
            TaskEntity task = getArguments().getParcelable("task");
            if ("2".equals(task.taskstatus)) {
                btnSave.setVisibility(View.GONE);
                btnUpload.setVisibility(View.GONE);
            } else {
                btnSave.setVisibility(View.VISIBLE);
                btnUpload.setVisibility(View.VISIBLE);
            }
        }


        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List list, boolean isLocalData) {
        adapter = new TaskSampleRecyclerViewAdapter(R.layout.item_sample_list_content, list, this, isLocalData);
        adapter.addHeaderView(getHeaderView(), 0);
        adapter.addHeaderView(getAddView(), 1);
        adapter.addFooterView(getFootView());
        recyclerView.setAdapter(adapter);
    }

    private View getFootView() {

        View view = View.inflate(getContext(), R.layout.item_sample_list_footer, null);
        {
            Button edit = view.findViewById(R.id.btn_edit_feed_sheet);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog("是否打开表单?", 2);

                }
            });
            btnUploadFeed = view.findViewById(R.id.btn_upload_feed_sheet);
            tvUploadFeed = view.findViewById(R.id.tv_feed_sheet);

            btnUploadFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskSampleFragment.this.startGalleryForPdf(0, Select_Feed);
                }
            });
            if (!TextUtils.isEmpty(taskDetailViewModel.taskEntity.feedfile)) {
                tvUploadFeed.setText(taskDetailViewModel.taskEntity.feedfile);
            }
            if (!TextUtils.isEmpty(taskDetailViewModel.taskEntity.feedpicfile)) {
                btnUploadFeed.setText("已拍照");
            }
        }


        Button btnDisposal = view.findViewById(R.id.btn_edit_disposal);
        btnDisposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("是否打开表单?", 3);

            }
        });
        btnUploadAdvice = view.findViewById(R.id.btn_upload_feed_sheet);
        tvUploadAdvice = view.findViewById(R.id.tv_feed_sheet);

        btnUploadAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskSampleFragment.this.startGalleryForPdf(0, Select_Handle);
            }
        });
        if (!TextUtils.isEmpty(taskDetailViewModel.taskEntity.disposalfile)) {
            tvUploadAdvice.setText(taskDetailViewModel.taskEntity.disposalfile);
        }
        if (!TextUtils.isEmpty(taskDetailViewModel.taskEntity.disposalpicfile)) {
            btnUploadAdvice.setText("已拍照");
        }

        return view;
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
            saveSampleInFile();
        } else if (R.id.btn_save_upload == v.getId()) {
            showDialog("是否提交所有信息?", 1);
        }
    }

    private void showDialog(String msg, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (index == 1) {
//                    postSceneData();
                    postScenceByHttpURLConnection();
                } else if (index == 2) {
                    Intent intent = new Intent(getActivity(), H5WebViewActivity.class);
                    Bundle b = new Bundle();
                    b.putInt(Intent_Order, 7);
                    b.putSerializable("map", taskDetailViewModel.taskEntity.feedInfoMap);
                    intent.putExtras(b);
                    TaskSampleFragment.this.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
                } else if (index == 3) {
                    Intent intent = new Intent(getActivity(), H5WebViewActivity.class);
                    Bundle b = new Bundle();
                    b.putInt(Intent_Order, 2);
                    b.putSerializable("map", taskDetailViewModel.taskEntity.adviceInfoMap);
                    intent.putExtras(b);
                    TaskSampleFragment.this.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
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

    private void saveSampleInFile() {
//        for (int i = 0; i < taskDetailViewModel.taskEntity.taskSamples.size(); i++) {
//            if (!taskDetailViewModel.taskEntity.taskSamples.get(i).isLocalData) continue;
//            File file = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).disposalfile);
//            if (file.exists()) {
//            }
//            File fileHandle = new File(taskDetailViewModel.taskEntity.taskSamples.get(i).samplingfile);
//            if (fileHandle.exists()) {
//            }
//        }
//        if (!taskDetailViewModel.taskEntity.isLoadLocalData) return;
        if (!taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
            saveTaskInLocalFile(false);
        }
    }

    void createNewSample(String text) {
        if (null != text && !text.isEmpty()) {
            TaskSample sample = new TaskSample();
            sample.setSamplename(text);
            sample.list = new ArrayList<>();
            sample.isLocalData = true;
            sample.setId(System.currentTimeMillis() + "");
            sample.samplingInfoMap = new HashMap<>();
            sample.samplingInfoMap.put("sampling.inspectedname",TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyname)?"":taskDetailViewModel.taskEntity.companyname);
            sample.samplingInfoMap.put("sampling.inspectedaddress", TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyaddress)?"":taskDetailViewModel.taskEntity.companyaddress);
            taskDetailViewModel.taskEntity.taskSamples.add(sample);
            setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);

        if (taskDetailViewModel.taskEntity.isLoadLocalData) {
            findTaskInLocalFile();
            setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, true);
        } else {
            taskDetailViewModel.requestTasksamples(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
        }

        taskDetailViewModel.sampleDetailLiveData.observe(this, new Observer<LoadDataModel<List<TaskSample>>>() {
            @Override
            public void onChanged(LoadDataModel<List<TaskSample>> taskEntityLoadDataModel) {
                if (taskEntityLoadDataModel.isSuccess()) {

                    taskDetailViewModel.taskEntity.taskSamples = taskEntityLoadDataModel.getData();
                    for (TaskSample taskSample : taskDetailViewModel.taskEntity.taskSamples) {
//                        taskSample.workInfoMap.put("companyname", taskDetailViewModel.taskEntity.companyname);
//                        taskSample.workInfoMap.put("companyaddress", taskDetailViewModel.taskEntity.companyaddress);
//
//                        taskSample.riskInfoMap.put("companyname", taskDetailViewModel.taskEntity.companyname);
//                        taskSample.riskInfoMap.put("companyaddress", taskDetailViewModel.taskEntity.companyaddress);
//
//                        taskSample.adviceInfoMap.put("companyname", taskDetailViewModel.taskEntity.companyname);
//                        taskSample.adviceInfoMap.put("companyaddress", taskDetailViewModel.taskEntity.companyaddress);
//
                        taskSample.samplingInfoMap.put("sampling.inspectedname",TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyname)?"":taskDetailViewModel.taskEntity.companyname);
                        taskSample.samplingInfoMap.put("sampling.inspectedaddress", TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyaddress)?"":taskDetailViewModel.taskEntity.companyaddress);
                    }
                    setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, false);
                    for (TaskSample taskSample : taskDetailViewModel.taskEntity.taskSamples) {
                        {
                            Sampling sampling = taskSample.sampling;
                            if (null != sampling) {
                                Gson gson = new Gson();
                                String obj1 = gson.toJson(sampling);
                                JsonObject object = new JsonParser().parse(obj1).getAsJsonObject();
                                HashMap<String, String> map = new HashMap();
                                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                                    map.put("sampling." + entry.getKey(), entry.getValue().getAsString());
                                }
                                taskSample.samplingInfoMap = map;
                            }
                        }

                        {
                            Risk risk = taskSample.risk;
                            if (null != risk) {
                                Gson gson = new Gson();
                                String obj2 = gson.toJson(risk);
                                JsonObject object = new JsonParser().parse(obj2).getAsJsonObject();
                                HashMap<String, String> map = new HashMap();
                                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                                    map.put("risk." + entry.getKey(), entry.getValue().getAsString());
                                }
                                taskSample.riskInfoMap = map;
                            }
                        }

                        {
                            Work work = taskSample.work;
                            if (null != work) {
                                Gson gson = new Gson();
                                String obj2 = gson.toJson(work);
                                JsonObject object = new JsonParser().parse(obj2).getAsJsonObject();
                                HashMap<String, String> map = new HashMap();
                                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                                    map.put("work." + entry.getKey(), entry.getValue().getAsString());
                                }
                                taskSample.workInfoMap = map;
                            }
                        }

                    }
                    Feed feed = taskDetailViewModel.taskEntity.feed;
                    if (null != feed) {
                        Gson gson = new Gson();
                        String obj1 = gson.toJson(feed);
                        JsonObject object = new JsonParser().parse(obj1).getAsJsonObject();
                        HashMap<String, String> map = new HashMap();
                        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                            map.put("feed." + entry.getKey(), entry.getValue().getAsString());
                        }
                        taskDetailViewModel.taskEntity.feedInfoMap = map;
                    }
                    if ("2".equals(taskDetailViewModel.taskEntity.taskstatus)) {
                        btnSave.setVisibility(View.GONE);
                        btnUpload.setVisibility(View.GONE);
                    } else {
                        btnSave.setVisibility(View.VISIBLE);
                        btnUpload.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        EventBus.getDefault().register(this);
    }

    private void postSampleData() {
        if (null == taskDetailViewModel.taskEntity.taskSamples || taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
            dismissLoadingDialog();
            com.product.sampling.maputil.ToastUtil.show(getActivity(), "请创建样品数据");
            return;
        }
        for (int i = 0; i < taskDetailViewModel.taskEntity.taskSamples.size(); i++) {
            TaskSample sample = taskDetailViewModel.taskEntity.taskSamples.get(i);

            if (TextUtils.isEmpty(sample.getId())) {
                sample.setId(System.currentTimeMillis() + "");
            }
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            multipartBodyBuilder.setType(MultipartBody.FORM);

            multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                    .addFormDataPart("taskid", taskDetailViewModel.taskEntity.id);
            multipartBodyBuilder.addFormDataPart("id", sample.getId());
            multipartBodyBuilder.addFormDataPart("samplename", sample.getSamplename());

            if (i == taskDetailViewModel.taskEntity.taskSamples.size() - 1) {
                multipartBodyBuilder.addFormDataPart("islastone", "1");
            }
            {
                File samplingfile = new File(sample.samplingfile);
                if (samplingfile.exists()) {
                    Log.e("file", samplingfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, samplingfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("samplingfile", samplingfile.getName(), requestFile);//抽样单
                }
            }
            {
                File samplingpicfile = new File(sample.samplingpicfile);
                if (samplingpicfile.exists()) {
                    Log.e("file", samplingpicfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, samplingpicfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("samplingpicfile", samplingpicfile.getName(), requestFile);//抽样单
                }
            }

            {
                HashMap<String, String> samplingInfoMap = sample.samplingInfoMap;
                //没填的时候默认值

                if (null != samplingInfoMap && !samplingInfoMap.isEmpty()) {
                    for (String s : samplingInfoMap.keySet()) {
                        if (!s.startsWith("sampling.")) continue;
                        try {
                            String value = samplingInfoMap.get(s);
                            multipartBodyBuilder.addFormDataPart(s, value);//抽样单
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            {
                File riskfile = new File(sample.riskfile);
                if (riskfile.exists()) {
                    Log.e("file", riskfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, riskfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("riskfile", riskfile.getName(), requestFile);//处置单
                }
            }
            {
                File riskpicfile = new File(sample.riskpicfile);
                if (riskpicfile.exists()) {
                    Log.e("file", riskpicfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, riskpicfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("riskpicfile", riskpicfile.getName(), requestFile);//风险单
                }
            }

            {
                HashMap<String, String> adviceInfoMap = sample.riskInfoMap;
                //没填的时候默认值
                if (null != adviceInfoMap && !adviceInfoMap.isEmpty()) {
                    for (String s : adviceInfoMap.keySet()) {
                        if (!s.startsWith("risk.")) continue;
                        try {
                            String value = adviceInfoMap.get(s);
                            multipartBodyBuilder.addFormDataPart(s, value);//抽样单
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            {
                File workfile = new File(sample.workfile);
                if (workfile.exists()) {
                    Log.e("file", workfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, workfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("workfile", workfile.getName(), requestFile);//处置单
                }
            }
            {
                File riskpicfile = new File(sample.workpicfile);
                if (riskpicfile.exists()) {
                    Log.e("file", riskpicfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, riskpicfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("workpicfile", riskpicfile.getName(), requestFile);//风险单
                }
            }

            {
                HashMap<String, String> adviceInfoMap = sample.workInfoMap;
                //没填的时候默认值
                if (null != adviceInfoMap && !adviceInfoMap.isEmpty()) {
                    for (String s : adviceInfoMap.keySet()) {
                        if (!s.startsWith("work.")) continue;
                        try {
                            String value = adviceInfoMap.get(s);
                            multipartBodyBuilder.addFormDataPart(s, value);//抽样单
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            {
                File qRCodeReportfile = new File(sample.qRCodeReportfile);
                if (qRCodeReportfile.exists()) {
                    Log.e("file", qRCodeReportfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, qRCodeReportfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("qRCodeReportfile", qRCodeReportfile.getName(), requestFile);//风险单
                }
            }

            List<Pics> list = sample.getPics();

            if (null == list || list.isEmpty()) {
                dismissLoadingDialog();
                ToastUtil.show(getActivity(), "请选择图片");
                continue;
            }
            for (int j = 0; j < list.size(); j++) {
                Pics pics = list.get(j);
                if (!TextUtils.isEmpty(pics.getId())) {
                    multipartBodyBuilder.addFormDataPart("uploadPic[" + j + "].fileid", pics.getId());
                    multipartBodyBuilder.addFormDataPart("uploadPic[" + j + "].fileStr", pics.getRemarks());
                    continue;
                }
                File f = new File(pics.getOriginalPath());
                if (!f.exists()) {
                    Log.e("file", f.getAbsolutePath());
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("uploadPic[" + j + "].fileStr", pics.getRemarks() + "")
                        .addFormDataPart("uploadPic[" + j + "].fileStream", f.getName(), requestImage);

            }

            int finalI = i;
            showLoadingDialog("样品信息提交中");
            RetrofitService.createApiService(Request.class)
                    .savesampleByBody(multipartBodyBuilder.build())
                    .compose(RxSchedulersHelper.io_main())
                    .compose(RxSchedulersHelper.ObsHandHttpResult())
                    .subscribe(new ZBaseObserver<String>() {
                        @Override
                        public void onSuccess(String s) {
                            dismissLoadingDialog();
                            if (finalI == taskDetailViewModel.taskEntity.taskSamples.size() - 1) {
                                com.product.sampling.maputil.ToastUtil.show(getActivity(), "上传样品成功");
                                EventBus.getDefault().post(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id));
                                saveTaskInLocalFile(true);
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MediaHelper.REQUEST_IMAGE_CODE:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    if (selectId != -1) {
                        List<Pics> mediaInfos = new ArrayList<>();
                        for (LocalMedia media :
                                selectList) {
                            Pics mediaInfo = new Pics();
                            mediaInfo.setOriginalPath(media.getPath());
                            mediaInfo.setCompressPath(media.getCompressPath());
                            mediaInfo.setCompressed(media.isCompressed());
                            mediaInfos.add(mediaInfo);
                        }
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).isLocalData = true;
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).pics.addAll(mediaInfos);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    break;
                case RequestCodePdf:
                    if (data != null) {
                        int index = data.getIntExtra("task", -1);
                        if (index != -1) {

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
                                    }
                                }
                                int pos = data.getIntExtra(Intent_Order, 1);
                                String reporttype = "";
                                if (pos == 1) {
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).samplingfile = data.getStringExtra("pdf");
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).samplingInfoMap = map;
                                    shareBySystem(data.getStringExtra("pdf"));
                                    reporttype = "sampling";
                                } else if (pos == 2) {
                                    taskDetailViewModel.taskEntity.adviceInfoMap = map;
                                    taskDetailViewModel.taskEntity.disposalfile = data.getStringExtra("pdf");
                                    shareBySystem(data.getStringExtra("pdf"));
                                    reporttype = "advice";
                                } else if (pos == 5) {
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).riskInfoMap = map;
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).riskfile = data.getStringExtra("pdf");
                                    shareBySystem(data.getStringExtra("pdf"));
                                    reporttype = "risk";
                                } else if (pos == 6) {
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).workInfoMap = map;
                                    taskDetailViewModel.taskEntity.taskSamples.get(index).workfile = data.getStringExtra("pdf");
                                    shareBySystem(data.getStringExtra("pdf"));
                                    reporttype = "work";
                                } else if (pos == 7) {
                                    taskDetailViewModel.taskEntity.feedInfoMap = map;
                                    taskDetailViewModel.taskEntity.feedfile = data.getStringExtra("pdf");
                                    tvUploadFeed.setText(taskDetailViewModel.taskEntity.feedfile);
                                    shareBySystem(data.getStringExtra("pdf"));
                                    reporttype = "feed";
                                }
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                                if (pos == 7) {
                                    taskDetailViewModel.sendReportRecord(taskDetailViewModel.taskEntity.id, null, reporttype);
                                } else {
                                    taskDetailViewModel.sendReportRecord(taskDetailViewModel.taskEntity.id, taskDetailViewModel.taskEntity.taskSamples.get(index).getId(), reporttype);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("para", para);
                        } else {
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
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int pos = data.getIntExtra(Intent_Order, 1);
                            if (pos == 7) {
                                taskDetailViewModel.taskEntity.feedInfoMap = map;
                                taskDetailViewModel.taskEntity.feedfile = data.getStringExtra("pdf");
                                tvUploadFeed.setText(taskDetailViewModel.taskEntity.feedfile);
                                shareBySystem(data.getStringExtra("pdf"));
                                taskDetailViewModel.sendReportRecord(taskDetailViewModel.taskEntity.id, null, "feed");
                            }

                        }
                    }
                    break;
                case Select_Handle:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskEntity.disposalpicfile = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    break;
                case Select_Check:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).samplingpicfile = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    break;

                case Select_Work:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).workpicfile = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    break;
                case Select_Risk:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).riskpicfile = selectHandle.get(0).getPath();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                    break;
                case Select_Feed:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
                        taskDetailViewModel.taskEntity.feedpicfile = selectHandle.get(0).getPath();
                        btnUploadFeed.setText("已拍照");
                    }
                    break;

            }
        }
    }


    /**
     * 取pdf截图
     *
     * @param requstCode
     */
    public void startGalleryForPdf(int index, int requstCode) {
        MediaHelper.startGallery(TaskSampleFragment.this, PictureConfig.SINGLE, requstCode);
    }

    private void saveTaskInLocalFile(boolean isRemove) {
        Gson gson = new Gson();
        ArrayList<TaskEntity> listTask = new ArrayList<>();
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
        if (!isRemove) {
            taskDetailViewModel.taskEntity.isLoadLocalData = true;
            listTask.add(taskDetailViewModel.taskEntity);
        }
        SPUtil.put(getActivity(), "tasklist", gson.toJson(listTask));
        if (isRemove) {
            getActivity().finish();
        } else {
            com.product.sampling.maputil.ToastUtil.show(getActivity(), "保存成功");
        }
    }

    void findTaskInLocalFile() {
        Gson gson = new Gson();
        String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
        if (!TextUtils.isEmpty(taskListStr)) {
            Type listType = new TypeToken<List<TaskEntity>>() {
            }.getType();
            ArrayList<TaskEntity> listTask = gson.fromJson(taskListStr, listType);
            if (null != listTask && !listTask.isEmpty()) {
                for (int i = 0; i < listTask.size(); i++) {
                    if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
                        taskDetailViewModel.taskEntity = listTask.get(i);
                    }
                }
//                if (null != taskDetailViewModel.taskEntity.taskSamples && !taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
//                    for (int j = 0; j < taskDetailViewModel.taskEntity.taskSamples.size(); j++) {
//                        TaskSample taskSample = taskDetailViewModel.taskEntity.taskSamples.get(j);
//                        if (!taskSample.isLocalData) {
//                            taskDetailViewModel.taskEntity.taskSamples.remove(taskSample);
//                        }
//                    }
//                }

            }

        }
    }

    private void postSceneData() {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        if (!TextUtils.isEmpty(deleteId) && deleteId.endsWith(",")) {
            deleteId = deleteId.substring(0, deleteId.length() - 1);
        }
        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("id", taskDetailViewModel.taskEntity.id)
                .addFormDataPart("taskisok", "0")
                .addFormDataPart("sampleids", deleteId);

        AMapLocation location = MainApplication.INSTANCE.getMyLocation();
        if (null != location) {
            multipartBodyBuilder.addFormDataPart("taskaddress", location.getAddress() + "")
                    .addFormDataPart("longitude", location.getLongitude() + "")
                    .addFormDataPart("latitude", location.getLatitude() + "");
        }

        if (null != taskDetailViewModel.taskEntity.pics && !taskDetailViewModel.taskEntity.pics.isEmpty()) {
            for (int i = 0; i < taskDetailViewModel.taskEntity.pics.size(); i++) {
                Pics pics = taskDetailViewModel.taskEntity.pics.get(i);
                if (!TextUtils.isEmpty(pics.getId())) {
                    multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileid", pics.getId());
                    multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileStr", pics.getRemarks());
                    continue;
                }
                String path = taskDetailViewModel.taskEntity.pics.get(i).getOriginalPath();
                if (TextUtils.isEmpty(path)) continue;
                File f = new File(path);
                if (!f.exists()) {
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
                    continue;
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileStr", pics.getRemarks() + "")
                        .addFormDataPart("uploadPic[" + i + "].fileStream", f.getName(), requestImage);
            }
        }
        if (null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {

            for (int i = 0; i < taskDetailViewModel.taskEntity.voides.size(); i++) {
                Videos videos = taskDetailViewModel.taskEntity.voides.get(i);
                if (!TextUtils.isEmpty(videos.getId())) {
                    multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileid", videos.getId());
                    multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileStr", videos.getRemarks());

                    continue;
                }
                File f = new File(videos.getPath());
                if (!f.exists()) {
                    Log.e("视频", f.getAbsolutePath());
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效视频");
                    continue;
                }
                byte[] fileBytes = null;
                try {
                    FileInputStream fis = new FileInputStream(f);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int count;
                    while ((count = fis.read(buf)) != -1) {
                        baos.write(buf, 0, count); //实际上，如果文件体积比较大的话，不用转码，在这一步就可能OOM了
                    }
                    fileBytes = baos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, fileBytes);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileStr", videos.getRemarks() + "")
                        .addFormDataPart("uploadVedio[" + i + "].fileStream", f.getName(), requestImage);
            }
        }
        {
            File feedfile = new File(taskDetailViewModel.taskEntity.feedfile);
            if (feedfile.exists()) {
                Log.e("file", feedfile.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, feedfile);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("feedfile", feedfile.getName(), requestFile);//抽样单
            }
        }
        {
            File feedpicfile = new File(taskDetailViewModel.taskEntity.feedpicfile);
            if (feedpicfile.exists()) {
                Log.e("file", feedpicfile.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, feedpicfile);//把文件与类型放入请求体
                multipartBodyBuilder.addFormDataPart("feedpicfile", feedpicfile.getName(), requestFile);//抽样单
            }
        }

        {
            HashMap<String, String> feedInfoMap = taskDetailViewModel.taskEntity.feedInfoMap;
            //没填的时候默认值

            if (null != feedInfoMap && !feedInfoMap.isEmpty()) {
                for (String s : feedInfoMap.keySet()) {
                    if (!s.startsWith("feed.")) continue;
                    try {
                        String value = feedInfoMap.get(s);
                        multipartBodyBuilder.addFormDataPart(s, value);//抽样单
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (null != taskDetailViewModel.taskEntity.taskSamples && !taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
            multipartBodyBuilder.addFormDataPart("samplecount", taskDetailViewModel.taskEntity.taskSamples.size() + "");
        } else {
            multipartBodyBuilder.addFormDataPart("samplecount", "0");
        }
        showLoadingDialog();
        showLoadingDialog("现场信息提交中");
        RetrofitService.createApiService(Request.class)
                .uploadtaskinfo(multipartBodyBuilder.build())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "添加现场信息成功");
//                        postSampleData();
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

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public void findPrintShare(String path) {
   /* Intent intent = new Intent(this, DayinActivity.class);http://xzc.197746.com/printershare11165.apk
    startActivity(intent);*/
        if (isAvilible(getActivity(), "org.mopria.printplugin")) {
            File doc = new File(path);
            ComponentName comp = new ComponentName("org.mopria.printplugin", "org.mopria.printplugin.DocumentRenderingActivity");
            // ComponentName comp = new ComponentName("com.dynamixsoftware.printershare","com.dynamixsoftware.printershare.ActivityPrintDocuments");
            Intent intent = new Intent();
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
            intent.setType("application/doc");
//            intent.setData(Uri.fromFile(doc));
//            startActivity(intent);


            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.product.sampling.fileprovider", doc);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            startActivity(intent);


        } else {
            String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath() + File.separator + "printershare11165.apk";
//            new DownloadTask(this).execute("http://xzc.197746.com/printershare11165.apk",destPath);
        }
    }

    private boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.contains("canon")) {
                    Log.e("canon", pn);
                }
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Throwable throwable) {
        if (null != throwable) {
            dismissLoadingDialog();
            com.product.sampling.maputil.ToastUtil.show(getActivity(), "视频太大,请重新选择上传");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void postScenceByHttpURLConnection() {

        Map<String, String> requestText = new HashMap<String, String>();
        Map<String, String> requestFile = new HashMap<String, String>();

        if (!TextUtils.isEmpty(deleteId) && deleteId.endsWith(",")) {
            deleteId = deleteId.substring(0, deleteId.length() - 1);
        }
        requestText.put("userid", AccountManager.getInstance().getUserId());
        requestText.put("id", taskDetailViewModel.taskEntity.id);
        requestText.put("taskisok", "0");
        requestText.put("sampleids", deleteId);
        if (null != taskDetailViewModel.taskEntity.taskSamples && !taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
            requestText.put("samplecount", taskDetailViewModel.taskEntity.taskSamples.size() + "");
        } else {
            requestText.put("samplecount", "0");
        }
        AMapLocation location = MainApplication.INSTANCE.getMyLocation();
        if (null != location) {
            requestText.put("taskaddress", location.getAddress() + "");
            requestText.put("longitude", location.getLongitude() + "");
            requestText.put("latitude", location.getLatitude() + "");
        }
        if (null != taskDetailViewModel.taskEntity.pics && !taskDetailViewModel.taskEntity.pics.isEmpty()) {
            for (int i = 0; i < taskDetailViewModel.taskEntity.pics.size(); i++) {
                Pics pics = taskDetailViewModel.taskEntity.pics.get(i);
                if (!TextUtils.isEmpty(pics.getId())) {
                    requestText.put("uploadPic[" + i + "].fileid", pics.getId());
                    requestText.put("uploadPic[" + i + "].fileStr", pics.getRemarks());
                    continue;
                }
                String path = taskDetailViewModel.taskEntity.pics.get(i).getOriginalPath();
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
        if (null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {

            for (int i = 0; i < taskDetailViewModel.taskEntity.voides.size(); i++) {
                Videos videos = taskDetailViewModel.taskEntity.voides.get(i);
                if (!TextUtils.isEmpty(videos.getId())) {
                    requestText.put("uploadVedio[" + i + "].fileid", videos.getId());
                    requestText.put("uploadVedio[" + i + "].fileStr", videos.getRemarks());

                    continue;
                }
                File f = new File(videos.getPath());
                if (!f.exists()) {
                    Log.e("视频", f.getAbsolutePath());
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效视频");
                    continue;
                }

                requestText.put("uploadVedio[" + i + "].fileStr", videos.getRemarks());
                requestFile.put("uploadVedio[" + i + "].fileStream", videos.getPath());
            }
        }
        {
            File feedfile = new File(taskDetailViewModel.taskEntity.feedfile);
            if (feedfile.exists()) {
                Log.e("file", feedfile.getAbsolutePath());
                requestFile.put("feedfile", taskDetailViewModel.taskEntity.feedfile);//抽样单
            }
        }
        {
            File feedpicfile = new File(taskDetailViewModel.taskEntity.feedpicfile);
            if (feedpicfile.exists()) {
                Log.e("file", feedpicfile.getAbsolutePath());
                requestFile.put("feedfile", taskDetailViewModel.taskEntity.feedpicfile);//抽样单
            }
        }

        {
            HashMap<String, String> feedInfoMap = taskDetailViewModel.taskEntity.feedInfoMap;
            //没填的时候默认值

            if (null != feedInfoMap && !feedInfoMap.isEmpty()) {
                for (String s : feedInfoMap.keySet()) {
                    if (!s.startsWith("feed.")) continue;
                    try {
                        String value = feedInfoMap.get(s);
                        requestText.put(s, value);//抽样单
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        {
            File disposalfile = new File(taskDetailViewModel.taskEntity.disposalfile);
            if (disposalfile.exists()) {
                Log.e("disposalfile", disposalfile.getAbsolutePath());
                requestFile.put("disposalfile", taskDetailViewModel.taskEntity.disposalfile);//抽样单
            }
        }
        {
            File disposalpicfile = new File(taskDetailViewModel.taskEntity.disposalpicfile);
            if (disposalpicfile.exists()) {
                Log.e("file", disposalpicfile.getAbsolutePath());
                requestFile.put("disposalpicfile", taskDetailViewModel.taskEntity.disposalpicfile);//抽样单
            }
        }

        {
            HashMap<String, String> adviceInfoMap = taskDetailViewModel.taskEntity.adviceInfoMap;
            //没填的时候默认值

            if (null != adviceInfoMap && !adviceInfoMap.isEmpty()) {
                for (String s : adviceInfoMap.keySet()) {
                    if (!s.startsWith("advice.")) continue;
                    try {
                        String value = adviceInfoMap.get(s);
                        requestText.put(s, value);//抽样单
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        HttpURLConnectionUtil httpReuqest = new HttpURLConnectionUtil();

        new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog();
                showLoadingDialog("现场信息提交中");
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                String response = null;
                try {
                    response = httpReuqest.formUpload(requestText, requestFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (null != o && !TextUtils.isEmpty(o.toString())) {
                    try {
                        JSONObject object = new JSONObject(o.toString());
                        if (object.has("message") && !TextUtils.isEmpty(object.optString("message"))) {
                            com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), object.optString("message"));
                        }
                        if (object.has("code") && object.optInt("code") == 200) {
                            postSampleData();
                        } else {
                            dismissLoadingDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "提交失败,请稍后重试");
                    dismissLoadingDialog();
                }


            }
        }.execute();
    }
}
