package com.product.sampling.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.product.sampling.MainApplication;
import com.product.sampling.R;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.Feed;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.Risk;
import com.product.sampling.bean.Sampling;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.bean.VerifyTaskStatusVo;
import com.product.sampling.bean.Videos;
import com.product.sampling.bean.Work;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.CheckFormDao;
import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.db.tables.HandleFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.eventmessage.CurDeleteSampleItemMessage;
import com.product.sampling.ui.eventmessage.CurSampleItemMessage;
import com.product.sampling.ui.form.CheckOrRecheckFormActivity;
import com.product.sampling.ui.form.FeedBackFormActivity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import static com.product.sampling.db.manager.CheckFormManager.CHECK_FORM_QUERY_ID;
import static com.product.sampling.db.manager.CheckFormManager.CHECK_FORM_QUERY_SAMPLE_CODE;
import static com.product.sampling.db.manager.CheckFormManager.CHECK_FORM_QUERY_SAMPLE_ID_BY_SAMPLE_CODE;
import static com.product.sampling.ui.H5WebViewActivity.Intent_Order;
import static com.product.sampling.ui.TaskDetailActivity.TASK_CHECK;
import static com.product.sampling.ui.TaskDetailActivity.TASK_NOT_UPLOAD;
import static com.product.sampling.ui.TaskDetailActivity.TASK_UPLOADED;

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
    TextView tvCreateSample;
    /**
     * 是否可以编辑，如果已经上传不允许修改编辑 false为已经上传
     */
    private boolean canEdit = true;
    /**
     * 是否需要 恢复数据
     */
//    private boolean isNeedRevert = true;


    public Handler upHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x01://全部上传成功，删除本地的数据
                    EventBus.getDefault().postSticky(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id,true));
                    LocalTaskListManager.getInstance().remove(taskDetailViewModel.taskEntity);
                    ActivityManager.getInstance().currentActivity().finish();
                    break;
                case CHECK_FORM_QUERY_SAMPLE_CODE:
                    if(revertDialog.isShowing()){
                        revertDialog.dismiss();
                    }
                   CheckForm checkForm = (CheckForm) msg.obj;
                   if(checkForm != null){
//                       checkForm.getTaskCode();
//                       revertTaskSample(checkForm.getTaskCode(),checkForm);
                       showRevertEditTextDialog(checkForm);
                   }else{
                       Toast.makeText(getActivity(),"该任抽样单号不能恢复数据！本地数据库没有该数据！",Toast.LENGTH_LONG).show();
                   }
                   break;
                case CHECK_FORM_QUERY_SAMPLE_ID_BY_SAMPLE_CODE:
                    CheckForm checkFormT = (CheckForm) msg.obj;
                    if(checkFormT == null){
                        if(revertDialog.isShowing()){
                            revertDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "本次查无此抽样单号，无法恢复数据,请重新核对抽样单号！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    boolean isNeedRevert = true;
                    List <TaskSample> taskSamples = taskDetailViewModel.taskEntity.taskSamples;
                    if(taskSamples != null && taskSamples.size() > 0){
                        for(TaskSample taskSample: taskSamples){
                            if(taskSample.getId().equals(checkFormT.getId())){
                                isNeedRevert = false;
                            }
                        }
                    }

                    if(isNeedRevert){
                        requestVerifySampleCodeToTask(checkFormT.getTaskCode());
                    }else {
                        if(revertDialog.isShowing()){
                            revertDialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "该抽样单号无需恢复！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }


        }
    };
    public TaskSampleFragment() {
    }

    public static TaskSampleFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putSerializable("task", task);

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
        EventBus.getDefault().register(this);

        mRecyclerView = rootView.findViewById(R.id.item_image_list);


        btnSave = rootView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnUpload = rootView.findViewById(R.id.btn_save_upload);
        btnUpload.setOnClickListener(this);

        if (null != getArguments() && null != getArguments().getSerializable("task")) {
            TaskEntity task = (TaskEntity) getArguments().getSerializable("task");
            if (task.taskstatus.equals(TASK_UPLOADED)) {
                btnSave.setVisibility(View.GONE);
                btnUpload.setVisibility(View.GONE);
            } else {
                btnSave.setVisibility(View.VISIBLE);
                btnUpload.setVisibility(View.VISIBLE);
            }
        }


        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List list, boolean isLocalData,TaskDetailViewModel taskDetailViewModel) {
        adapter = new TaskSampleRecyclerViewAdapter(R.layout.item_sample_list_content, list, this, isLocalData, canEdit,taskDetailViewModel);
        adapter.addHeaderView(getHeaderView(), 0);
        adapter.addHeaderView(getAddView(), 1);
        adapter.addFooterView(getFootView());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
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
        btnUploadAdvice = view.findViewById(R.id.btn_upload_disposal);
        tvUploadAdvice = view.findViewById(R.id.tv_disposal);

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
        tvCreateSample = view.findViewById(R.id.tv_create);
        tvCreateSample.setOnClickListener(this);
        tvCreateSample.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showCodeDialog();
                return true;
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_create) {
            if(canEdit){
                showEditTextDialog();
            }else{
                Toast.makeText(getActivity(),"已经上传的任务不能添加样品！",Toast.LENGTH_LONG).show();
            }

        } else if (v.getId() == R.id.btn_save) {
//            saveSampleInFile();
            if(canEdit){
                taskDetailViewModel.taskEntity.taskstatus = TASK_NOT_UPLOAD;
                LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
                EventBus.getDefault().postSticky(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id,false));
                Toast.makeText(getActivity(),"保存成功！",Toast.LENGTH_LONG).show();
            }

        } else if (R.id.btn_save_upload == v.getId()) {
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setTitle("提交信息")
                    .setMessage("是否提交所有信息？")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            postScenceData();
                            dialog.dismiss();
                        }
                    })
                    .show();
//            showDialog("是否提交所有信息?", 1);
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
//                    postScenceByHttpURLConnection();
                    postScenceData();
                } else if (index == 2) {
                    Intent intent = new Intent(getActivity(), FeedBackFormActivity.class);
                    Bundle b = new Bundle();
                    String tasktypecount = taskDetailViewModel.taskEntity.tasktypecount;//产品名称
                    String companyname = taskDetailViewModel.taskEntity.companyname;//企业名称
                    b.putString("tasktypecount",tasktypecount);
                    b.putString("companyname",companyname);
                    intent.putExtras(b);
                    TaskSampleFragment.this.startActivity(intent);
                } else if (index == 3) {

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

//    private void saveSampleInFile() {
//        if (taskDetailViewModel.taskEntity.taskSamples != null) {
//            saveTaskInLocalFile(false);
//        }
//    }

    /**
     * 创建新的样品
     * @param text
     */
    private void createNewSample(String text) {
        if (null != text && !text.isEmpty()) {
            TaskSample sample = new TaskSample();
            sample.setSamplename(text);
            sample.list = new ArrayList<>();
            sample.isLocalData = true;
            String id = System.currentTimeMillis() + "";
            sample.setId(id);
            sample.samplingInfoMap = new HashMap<>();
            sample.samplingInfoMap.put("sampling.inspectedname", TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyname) ? "" : taskDetailViewModel.taskEntity.companyname);
            sample.samplingInfoMap.put("sampling.inspectedaddress", TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyaddress) ? "" : taskDetailViewModel.taskEntity.companyaddress);
            //根据id去服务端获取taskcode抽样单编号
            requestTaskCode(sample,AccountManager.getInstance().getUserId(),taskDetailViewModel.taskEntity.id,"sampling", taskDetailViewModel.taskEntity.planno);
        }
    }

    /**
     * 根据抽样单id恢复数据
     */
    private void revertCheckFormBySampleId(List<String> sampleIdList){
        for(String sampleId: sampleIdList){
            DBManagerFactory.getInstance().getCheckFormManager().queryBySampleId(sampleId,upHandler);
        }
    }

    /**
     * 根据抽样单id恢复数据
     */
    private void revertCheckFormBySampleCode(String sampleCode){
        DBManagerFactory.getInstance().getCheckFormManager().queryBySampleCode(sampleCode,upHandler);
    }

    /**
     *
     * @param text 重新输入样品名称
     */
    private void revertTaskSample(String text,CheckForm checkForm){
        TaskSample sample = new TaskSample();
        sample.setSamplename(text);
        sample.setId(checkForm.getId());
        taskDetailViewModel.taskEntity.taskSamples.add(sample);
        HandleForm handleForm = new HandleForm();
        handleForm.setSampleId(checkForm.getId());
        handleForm.setTaskId(taskDetailViewModel.taskEntity.id);
        handleForm.setCydCode(checkForm.getTaskCode());
        //保存
        taskDetailViewModel.taskEntity.taskstatus = TASK_NOT_UPLOAD;
        LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
        EventBus.getDefault().postSticky(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id,false));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, true,taskDetailViewModel);
            }
        });
    }


    /**
     * 获取抽样单编号接口
     * @param taskid
     * @param reporttype
     * @param  planno 项目备案号
     */
    private void requestTaskCode(TaskSample taskSample,String userid,String taskid,String reporttype,String planno){
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getChecFormTaskCode(userid,taskid,taskSample.getId(),reporttype)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String r) {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                taskDetailViewModel.taskEntity.taskSamples.add(taskSample);
                                CheckForm oldCheckForm = DbController.getInstance(getActivity()).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(taskSample.getId())).build().unique();
                                if(oldCheckForm != null){
                                    oldCheckForm.setId(taskSample.getId());
                                    oldCheckForm.setTaskCode(r);
                                    Date nowData = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                                    String nowDateString = format.format(nowData);
                                    String date[] = nowDateString.split("-");
                                    oldCheckForm.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                                    CheckOrRecheckFormActivity.CYR_DATE_YEAR = date[0];
                                    CheckOrRecheckFormActivity.CYR_DATE_MONTH = date[1];
                                    CheckOrRecheckFormActivity.CYR_DATE_DAY = date[2];
                                    oldCheckForm.setAcceptProduceCYRQ(nowDateString);
                                    oldCheckForm.setCheckCompanyName(CheckCompany.company);
                                    oldCheckForm.setCheckCompanyNameId(CheckCompany.companyId);
                                    oldCheckForm.setCheckCompanyAddress(CheckCompany.companyAddress);
                                    oldCheckForm.setCheckCompanyEMS(CheckCompany.EMS);
                                    oldCheckForm.setCheckCompanyConnectName(CheckCompany.connectName);
                                    oldCheckForm.setCheckCompanyConnectTel(CheckCompany.connectTel);
                                    oldCheckForm.setCheckCompanyEmail(CheckCompany.connectEmail);
                                    oldCheckForm.setAcceptProduceJSYDD(CheckCompany.companyAddress);
                                    oldCheckForm.setAcceptProduceJSYJZRQ("/");
                                    oldCheckForm.setBzXMBAH(planno);
                                    DbController.getInstance(getActivity()).getDaoSession().getCheckFormDao().update(oldCheckForm);

                                }else {
                                    CheckForm checkForm = new CheckForm();
                                    checkForm.setId(taskSample.getId());
                                    checkForm.setTaskCode(r);
                                    Date nowData = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                                    String nowDateString = format.format(nowData);
                                    String date[] = nowDateString.split("-");
                                    checkForm.setCyDwRQ(date[0] + " 年" + date[1] + " 月" + date[2] + " 日");
                                    CheckOrRecheckFormActivity.CYR_DATE_YEAR = date[0];
                                    CheckOrRecheckFormActivity.CYR_DATE_MONTH = date[1];
                                    CheckOrRecheckFormActivity.CYR_DATE_DAY = date[2];
                                    checkForm.setAcceptProduceCYRQ(nowDateString);
                                    checkForm.setCheckCompanyName(CheckCompany.company);
                                    checkForm.setCheckCompanyNameId(CheckCompany.companyId);
                                    checkForm.setCheckCompanyAddress(CheckCompany.companyAddress);
                                    checkForm.setCheckCompanyEMS(CheckCompany.EMS);
                                    checkForm.setCheckCompanyConnectName(CheckCompany.connectName);
                                    checkForm.setCheckCompanyConnectTel(CheckCompany.connectTel);
                                    checkForm.setCheckCompanyEmail(CheckCompany.connectEmail);
                                    checkForm.setAcceptProduceJSYDD(CheckCompany.companyAddress);
                                    checkForm.setAcceptProduceJSYJZRQ("/");
                                    checkForm.setBzXMBAH(planno);
                                    DbController.getInstance(getActivity()).getDaoSession().getCheckFormDao().insertOrReplace(checkForm);
                                }

                                HandleForm handleForm = new HandleForm();
                                handleForm.setSampleId(taskSample.getId());
                                handleForm.setTaskId(taskid);
                                handleForm.setCydCode(r);
//                                handleForm.setSjdwmc(taskDetailViewModel.taskEntity.companyname);
//                                handleForm.setCpmc(taskDetailViewModel.taskEntity.tasktypecount);
                                DbController.getInstance(getActivity()).getDaoSession().getHandleFormDao().insertOrReplace(handleForm);
                                //保存
//
                                taskDetailViewModel.taskEntity.taskstatus = TASK_NOT_UPLOAD;
                                LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
                                EventBus.getDefault().postSticky(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id,false));
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, true,taskDetailViewModel);
                                    }
                                });

                            }
                        }.start();

                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        canEdit = !taskDetailViewModel.taskEntity.taskstatus.equals(TASK_UPLOADED);
        TaskEntity localEntity = LocalTaskListManager.getInstance().query(taskDetailViewModel.taskEntity);
        if(localEntity != null){//
            taskDetailViewModel.taskEntity = localEntity;
            setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, true, taskDetailViewModel);
        }else{
            //如果本地没有数据加载网络数据(当为任务为待办任务时候不请求网络直接初始化)
            if(TASK_UPLOADED.equals(taskDetailViewModel.taskEntity.taskstatus) || TASK_CHECK.equals(taskDetailViewModel.taskEntity.taskstatus)){
                taskDetailViewModel.requestTasksamples(AccountManager.getInstance().getUserId(), taskDetailViewModel.taskEntity.id);
            }else {
                setupRecyclerView(mRecyclerView, null, false, taskDetailViewModel);
            }

        }


        taskDetailViewModel.sampleDetailLiveData.observe(getActivity(), new Observer<LoadDataModel<List<TaskSample>>>() {
            @Override
            public void onChanged(LoadDataModel<List<TaskSample>> taskEntityLoadDataModel) {
                if (taskEntityLoadDataModel.isSuccess() && localEntity == null) {
                    taskDetailViewModel.taskEntity.taskSamples = taskEntityLoadDataModel.getData();
                    for (TaskSample taskSample : taskDetailViewModel.taskEntity.taskSamples) {
                        taskSample.samplingInfoMap.put("sampling.inspectedname", TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyname) ? "" : taskDetailViewModel.taskEntity.companyname);
                        taskSample.samplingInfoMap.put("sampling.inspectedaddress", TextUtils.isEmpty(taskDetailViewModel.taskEntity.companyaddress) ? "" : taskDetailViewModel.taskEntity.companyaddress);
                    }
                    setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, false,taskDetailViewModel);
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
                    if (taskDetailViewModel.taskEntity.isCirculationDomain()) {
                        btnSave.setVisibility(View.GONE);
                        btnUpload.setVisibility(View.GONE);
                    } else {
                        btnSave.setVisibility(View.VISIBLE);
                        btnUpload.setVisibility(View.VISIBLE);
                    }

                    if (taskDetailViewModel.taskEntity.isUploadedTask()) {
                        tvCreateSample.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
                        btnUpload.setVisibility(View.GONE);
                        btnUploadFeed.setVisibility(View.GONE);
                        btnUploadAdvice.setVisibility(View.GONE);
                    } else {
                        tvCreateSample.setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.VISIBLE);
                        btnUpload.setVisibility(View.VISIBLE);
                        btnUploadFeed.setVisibility(View.VISIBLE);
                        btnUploadAdvice.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }
    public void showPostFail(View view){
        Dialog failDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("请先生成PDF文件再提交，没有PDF文件不能上传数据！")
                .create();
        failDialog.show();


        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }
    private void postSampleData() {

        for (int i = 0; i < taskDetailViewModel.taskEntity.taskSamples.size(); i++) {
            TaskSample sample = taskDetailViewModel.taskEntity.taskSamples.get(i);

            if (TextUtils.isEmpty(sample.getId())) {
                sample.setId(System.currentTimeMillis() + "");
            }
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            multipartBodyBuilder.setType(MultipartBody.FORM);

            multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
                    .addFormDataPart("taskid", taskDetailViewModel.taskEntity.id);
            multipartBodyBuilder.addFormDataPart("id", sample.getId());//taskSampleId
            multipartBodyBuilder.addFormDataPart("samplename", sample.getSamplename());//

            AMapLocation location = MainApplication.INSTANCE.getMyLocation();
            if (null != location) {
                multipartBodyBuilder.addFormDataPart("taskaddress", location.getAddress() + "")
                        .addFormDataPart("longitude", location.getLongitude() + "")
                        .addFormDataPart("latitude", location.getLatitude() + "");
            }

            if (i == taskDetailViewModel.taskEntity.taskSamples.size() - 1) {
                multipartBodyBuilder.addFormDataPart("islastone", "1");
            }
            //pdf文件
            {
                CheckForm checkForm =  DbController.getInstance(getActivity()).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(sample.getId())).build().unique();
                if(checkForm == null){
                    upDialog.dismiss();
                    showPostFail(btnUpload);
                    return;
                }
                String pdfPath = checkForm.getPdfPath();
                if(!TextUtils.isEmpty(pdfPath)){
                    File samplingfile = new File(pdfPath);
                    if(samplingfile.exists()){
                        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, samplingfile);//把文件与类型放入请求体
                        multipartBodyBuilder.addFormDataPart("samplingfile", samplingfile.getName(), requestFile);//抽样单
                    }
                }else {
                    showPostFail(btnUpload);
                    return;
                }
            }

            //处置单上传
            {
                HandleForm handleForm =  DbController.getInstance(getActivity()).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(sample.getId())).build().unique();
                if(handleForm != null){
                    String pdfPath = handleForm.getPdfPath();
                    if(!TextUtils.isEmpty(pdfPath)){
                        File disposalfile = new File(pdfPath);
                        if(disposalfile.exists()){
                            RequestBody requestFile = RequestBody.create(MultipartBody.FORM, disposalfile);//把文件与类型放入请求体
                            multipartBodyBuilder.addFormDataPart("disposalfile", disposalfile.getName(), requestFile);//处置单
                        }
                    }
                    multipartBodyBuilder.addFormDataPart("advice.companyname", sample.getId());

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

            //处置单上传(拍照)
            {
                File disposalpicfile = new File(sample.handlepicfile);
                if (disposalpicfile.exists()) {
                    Log.e("file", disposalpicfile.getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, disposalpicfile);//把文件与类型放入请求体
                    multipartBodyBuilder.addFormDataPart("disposalpicfile", disposalpicfile.getName(), requestFile);//处置单
                }
            }


            {
                /**将数据抽样单数据上传*/
                CheckForm checkForm =  DbController.getInstance(getActivity()).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(sample.getId())).build().unique();
                appendSampleToBody("taskfrom",multipartBodyBuilder,checkNullString(checkForm.getTaskFromId()));
                appendSampleToBody("tasktype",multipartBodyBuilder,checkNullString(checkForm.getTaskClassId()));
                appendSampleToBody("taskfrommean",multipartBodyBuilder,checkNullString(checkForm.getTaskFrom()));
                appendSampleToBody("tasktypemean",multipartBodyBuilder,checkNullString(checkForm.getTaskClass()));
                appendSampleToBody("inspectedname",multipartBodyBuilder,checkNullString(checkForm.getAcceptCompanyName()));
                appendSampleToBody("inspectedaddress",multipartBodyBuilder,checkNullString(checkForm.getAcceptCompanyAddress()));
                appendSampleToBody("inspectedman",multipartBodyBuilder,checkNullString(checkForm.getAcceptCompanyConnectNanme()));
                appendSampleToBody("inspectedtel",multipartBodyBuilder,checkNullString(checkForm.getAcceptCompanyConnecTel()));
                appendSampleToBody("representative",multipartBodyBuilder,checkNullString(checkForm.getAcceptCompanyFaren()));
                appendSampleToBody("producename",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyName()));
                appendSampleToBody("produceaddress",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyAdress()));
                appendSampleToBody("producezipcode",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyEMS()));
                appendSampleToBody("prepresentative",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyFaren()));

                appendSampleToBody("produceconman",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyConnectName()));
                appendSampleToBody("producetel",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyConnectTEL()));
                appendSampleToBody("producelicense",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyLicense()));
                appendSampleToBody("producecode",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyID()));
                appendSampleToBody("produceoutput",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyDimensionsYYSR()));
                appendSampleToBody("producepcount",multipartBodyBuilder,checkNullString(checkForm.getProductCompanyDimensionsCYRYS()));

                appendSampleToBody("producesamlltype",multipartBodyBuilder,checkNullString(checkForm.getProductCompanySmallType()));
                appendSampleToBody("productnumtype",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceLicenseType()));
                appendSampleToBody("productnum",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceLicenseId()));
                appendSampleToBody("productname",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceCPMC()));
                appendSampleToBody("productmodel",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceGGCH()));
                appendSampleToBody("productbnum",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceSCRQ()));
                appendSampleToBody("productmark",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceSB()));
                appendSampleToBody("samplingcount",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceCYSL()));
                appendSampleToBody("productlevel",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceCPDJ()));
                appendSampleToBody("productpl",multipartBodyBuilder,checkNullString(checkForm.getAcceptProducePL()));
                appendSampleToBody("samplingbaseandbatch",multipartBodyBuilder,checkNullString(checkForm.getAcceptProducePL()));//新批量
                appendSampleToBody("dostandard",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceBZZXBZ()));
                appendSampleToBody("dotime",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceCYRQ()));
                appendSampleToBody("encapsulationstate",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceFYZT()));
                appendSampleToBody("rvandfc",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceBYLFCDD()));
                appendSampleToBody("sendaddress",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceJSYDD()));
                appendSampleToBody("endtime",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceJSYJZRQ()));
                appendSampleToBody("samplingname",multipartBodyBuilder,checkNullString(checkForm.getCheckCompanyNameId()));
                appendSampleToBody("samplingnamemean",multipartBodyBuilder,checkNullString(checkForm.getCheckCompanyName()));
                appendSampleToBody("samplingman",multipartBodyBuilder,checkNullString(checkForm.getCheckCompanyConnectName()));
                appendSampleToBody("samplingaddress",multipartBodyBuilder,checkNullString(checkForm.getCheckCompanyAddress()));
                appendSampleToBody("samplingemail",multipartBodyBuilder,checkNullString(checkForm.getCheckCompanyEmail()));
                appendSampleToBody("samplingcode",multipartBodyBuilder,checkNullString(checkForm.getCheckCompanyEMS()));
                appendSampleToBody("samplingtel",multipartBodyBuilder,checkNullString(checkForm.getCheckCompanyConnectTel()));
                appendSampleToBody("remark",multipartBodyBuilder,checkNullString(checkForm.getBzInfo()));
                appendSampleToBody("unitprice",multipartBodyBuilder,checkNullString(checkForm.getAcceptProduceDJ()));
                appendSampleToBody("recordnumber",multipartBodyBuilder,checkNullString(checkForm.getBzXMBAH()));
                appendSampleToBody("samplecost",multipartBodyBuilder,checkNullString(checkForm.getBzGYF()));
                appendSampleToBody("taskcode",multipartBodyBuilder,checkNullString(checkForm.getTaskCode()));
                appendSampleToBody("reportcode",multipartBodyBuilder,checkNullString(checkForm.getReportId()));
                appendSampleToBody("fillInDate",multipartBodyBuilder,checkNullString(checkForm.getCyDwRQ()));

                //签字图上传
                String acSign = checkForm.getAcceptCompanySignImagePath();
                if(!TextUtils.isEmpty(acSign)){
                    File acSignfile = new File(acSign);
                    if(acSignfile.exists()){
                        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, acSignfile);//把文件与类型放入请求体
                        multipartBodyBuilder.addFormDataPart("sampling.inspectedQM", acSignfile.getName(), requestFile);//抽样单
                    }
                }

                String pcSign = checkForm.getProductCompanySignImagePath();
                if(!TextUtils.isEmpty(pcSign)){
                    File pcSignfile = new File(pcSign);
                    if(pcSignfile.exists()){
                        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, pcSignfile);//把文件与类型放入请求体
                        multipartBodyBuilder.addFormDataPart("sampling.producenQM", pcSignfile.getName(), requestFile);//抽样单
                    }
                }

                String peoSign = checkForm.getCheckPeopleSignImagePath();
                if(!TextUtils.isEmpty(peoSign)){
                    File peoSignfile = new File(peoSign);
                    if(peoSignfile.exists()){
                        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, peoSignfile);//把文件与类型放入请求体
                        multipartBodyBuilder.addFormDataPart("sampling.samplingmanQM", peoSignfile.getName(), requestFile);//抽样单
                    }
                }

            }
//            //风险单上传
//            {
//                File riskfile = new File(sample.riskfile);
//                if (riskfile.exists()) {
//                    Log.e("file", riskfile.getAbsolutePath());
//                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, riskfile);//把文件与类型放入请求体
//                    multipartBodyBuilder.addFormDataPart("riskfile", riskfile.getName(), requestFile);//风险单
//                }
//            }
//            {
//                File riskpicfile = new File(sample.riskpicfile);
//                if (riskpicfile.exists()) {
//                    Log.e("file", riskpicfile.getAbsolutePath());
//                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, riskpicfile);//把文件与类型放入请求体
//                    multipartBodyBuilder.addFormDataPart("riskpicfile", riskpicfile.getName(), requestFile);//风险单
//                }
//            }

//            {
//                HashMap<String, String> adviceInfoMap = sample.riskInfoMap;
//                //没填的时候默认值
//                if (null != adviceInfoMap && !adviceInfoMap.isEmpty()) {
//                    for (String s : adviceInfoMap.keySet()) {
//                        if (!s.startsWith("risk.")) continue;
//                        try {
//                            String value = adviceInfoMap.get(s);
//                            multipartBodyBuilder.addFormDataPart(s, value);//处置单
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//            /
//            {
//                HandleForm handleForm =  DbController.getInstance(getActivity()).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.Id.eq(taskDetailViewModel.taskEntity.id)).build().unique();
//
//                String pdfPath = handleForm.getPdfPath();
//                if(!TextUtils.isEmpty(pdfPath)){
//                    File workgfile = new File(pdfPath);
//                    if(workgfile.exists()){
//                        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, workgfile);//把文件与类型放入请求体
//                        multipartBodyBuilder.addFormDataPart("workfile", workgfile.getName(), requestFile);//抽样单
//                    }
//                }
//            }

//            {
//                File riskpicfile = new File(sample.workpicfile);
//                if (riskpicfile.exists()) {
//                    Log.e("file", riskpicfile.getAbsolutePath());
//                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, riskpicfile);//把文件与类型放入请求体
//                    multipartBodyBuilder.addFormDataPart("workpicfile", riskpicfile.getName(), requestFile);//风险单
//                }
//            }
//
//            {
//                HashMap<String, String> adviceInfoMap = sample.workInfoMap;
//                //没填的时候默认值
//                if (null != adviceInfoMap && !adviceInfoMap.isEmpty()) {
//                    for (String s : adviceInfoMap.keySet()) {
//                        if (!s.startsWith("work.")) continue;
//                        try {
//                            String value = adviceInfoMap.get(s);
//                            multipartBodyBuilder.addFormDataPart(s, value);//抽样单
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//
//            {
//                File qRCodeReportfile = new File(sample.qRCodeReportfile);
//                if (qRCodeReportfile.exists()) {
//                    Log.e("file", qRCodeReportfile.getAbsolutePath());
//                    RequestBody requestFile = RequestBody.create(MultipartBody.FORM, qRCodeReportfile);//把文件与类型放入请求体
//                    multipartBodyBuilder.addFormDataPart("qRCodeReportfile", qRCodeReportfile.getName(), requestFile);//风险单
//                }
//            }

            List<Pics> list = sample.getPics();
            if(list != null && list.size() >0){
                for (int j = 0; j < list.size(); j++) {
                    Pics pics = list.get(j);
                    if (!TextUtils.isEmpty(pics.getId())) {
                        multipartBodyBuilder.addFormDataPart("uploadPic[" + j + "].fileid", pics.getId());
                        multipartBodyBuilder.addFormDataPart("uploadPic[" + j + "].fileStr", pics.getRemarks());
                        continue;
                    }
                    if(TextUtils.isEmpty(pics.getOriginalPath())){
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

            }

            int finalI = i;
            RetrofitServiceManager.getInstance().createApiService(Request.class)
                    .savesampleByBody(multipartBodyBuilder.build())
                    .compose(RxSchedulersHelper.io_main())
                    .compose(RxSchedulersHelper.ObsHandHttpResult())
                    .subscribe(new ZBaseObserver<String>(false) {
                        @Override
                        public void onSuccess(String s) {
                            if (finalI == taskDetailViewModel.taskEntity.taskSamples.size() - 1) {
                                upDialog.dismiss();
                                showUpLoadSuccess();

                            }
                        }

                        @Override
                        public void onSubscribe(Disposable d) {
                            super.onSubscribe(d);
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            super.onFailure(code, message);
                            LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
                            upDialog.dismiss();
                            showUpLoadFail(message);

                        }
                    });

        }

    }


    public void showUpLoadSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(ActivityManager.getInstance().currentActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("样品信息上传成功！")
                .create();
        sucDialog.show();


        btnUpload.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sucDialog != null && sucDialog.isShowing()){
                    sucDialog.dismiss();
                    upHandler.sendEmptyMessage(0x01);
                }

            }
        },1500);

    }
    public void showUpLoadFail(String failMessage){
        Dialog failDialog = new QMUITipDialog.Builder(ActivityManager.getInstance().currentActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(failMessage)
                .create();
        failDialog.show();


        btnUpload.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(failDialog != null && failDialog.isShowing()){
                    failDialog.dismiss();
                }
            }
        },1500);

    }

    /**
     * 将数据拼接成服务端格式
     */
    private void  appendSampleToBody(String string, MultipartBody.Builder builder,String value){
        builder.addFormDataPart("sampling." + string,value);
    }

    /**
     * 用于添加
     */
    private int curPos;//子位置
    private int curTaskPos;//父位置
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCurPosEvent(CurSampleItemMessage message) {
        curPos = message.getCurPost();
        curTaskPos = message.getCurTaskPost();
    }

    /**
     * 用于删除
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCurPosEvent(CurDeleteSampleItemMessage message) {
         int curDeletePos = message.getCurPost();//子位置
         int curDeleteTaskPos = message.getCurTaskPost();//父位置
        if(taskDetailViewModel.taskEntity.taskSamples.size() >0 && taskDetailViewModel.taskEntity.taskSamples.get(curDeleteTaskPos).pics.size() >0){
            if(taskDetailViewModel.taskEntity.taskSamples.get(curDeleteTaskPos).pics.size() == curDeletePos){

            }else{
                taskDetailViewModel.taskEntity.taskSamples.get(curDeleteTaskPos).pics.remove(curDeletePos);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MediaHelper.REQUEST_IMAGE_CODE:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if(selectList.size() == 1){
                        //单选
                        Pics mediaInfo = new Pics();
                        LocalMedia localMedia = selectList.get(0);
                        mediaInfo.setOriginalPath(localMedia.getPath());
                        mediaInfo.setCompressPath(localMedia.getCompressPath());
                        mediaInfo.setCompressed(localMedia.isCompressed());
                        if(taskDetailViewModel.taskEntity.taskSamples.size() >0 && taskDetailViewModel.taskEntity.taskSamples.get(curTaskPos).pics.size() >0){
                            if(taskDetailViewModel.taskEntity.taskSamples.get(curTaskPos).pics.size() == curPos){
                                mediaInfo.setRemarks("其他");
                                taskDetailViewModel.taskEntity.taskSamples.get(curTaskPos).pics.add(mediaInfo);
                            }else{
                                mediaInfo.setRemarks(taskDetailViewModel.taskEntity.taskSamples.get(curTaskPos).pics.get(curPos).getRemarks());
                                taskDetailViewModel.taskEntity.taskSamples.get(curTaskPos).pics.set(curPos,mediaInfo);
                            }
                        }
                    }else {
                        for(LocalMedia localMedia :selectList){
                            Pics mediaInfo = new Pics();
                            mediaInfo.setOriginalPath(localMedia.getPath());
                            mediaInfo.setCompressPath(localMedia.getCompressPath());
                            mediaInfo.setCompressed(localMedia.isCompressed());
                            mediaInfo.setRemarks("其他");
                            taskDetailViewModel.taskEntity.taskSamples.get(curTaskPos).pics.add(mediaInfo);
                        }
                    }
                    setupRecyclerView(mRecyclerView, taskDetailViewModel.taskEntity.taskSamples, true, taskDetailViewModel);
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
                            } else if (pos == 2) {
                                taskDetailViewModel.taskEntity.adviceInfoMap = map;
                                taskDetailViewModel.taskEntity.disposalfile = data.getStringExtra("pdf");
                                tvUploadAdvice.setText(taskDetailViewModel.taskEntity.disposalfile);
                                shareBySystem(data.getStringExtra("pdf"));
                                taskDetailViewModel.sendReportRecord(taskDetailViewModel.taskEntity.id, null, "advice");
                            }

                        }
                    }
                    break;
                case Select_Handle:
                    if (data != null) {
                        List<LocalMedia> selectHandle = PictureSelector.obtainMultipleResult(data);
//                        String sampleId = taskDetailViewModel.taskEntity.taskSamples.get(selectId).getId();
//                        CheckForm checkForm =  DbController.getInstance(getActivity()).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(sampleId)).build().unique();
//                        checkForm.setPdfPath(selectHandle.get(0).getPath());
//                        DbController.getInstance(getActivity()).getDaoSession().getCheckFormDao().update(checkForm);

                        taskDetailViewModel.taskEntity.taskSamples.get(selectId).handlepicfile = selectHandle.get(0).getPath();
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
        }else if(resultCode == CheckOrRecheckFormActivity.RESULT_CODE_PDF_SUCCESS){
            mRecyclerView.getAdapter().notifyDataSetChanged();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(canEdit){
            taskDetailViewModel.taskEntity.taskstatus = TASK_NOT_UPLOAD;
            LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
        }
    }

//    protected void saveTaskInLocalFile(boolean isRemove) {
//        if(!taskDetailViewModel.taskEntity.isUploadedTask()){
//            Gson gson = new Gson();
//            ArrayList<TaskEntity> listTask = new ArrayList<>();
//            String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
//            if (!TextUtils.isEmpty(taskListStr)) {
//                Type listType = new TypeToken<List<TaskEntity>>() {
//                }.getType();
//                listTask = gson.fromJson(taskListStr, listType);
//                if (null != listTask && !listTask.isEmpty()) {
//                    for (int i = 0; i < listTask.size(); i++) {
//                        if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
//                            listTask.remove(i);
//                        }
//                    }
//                }
//            }
//            if (!isRemove) {
//                taskDetailViewModel.taskEntity.isLoadLocalData = true;
//                listTask.add(taskDetailViewModel.taskEntity);
//            }
//            SPUtil.put(getActivity(), "tasklist", gson.toJson(listTask));
//            EventBus.getDefault().postSticky(TaskMessage.getInstance(taskDetailViewModel.taskEntity.id));
//            if (isRemove) {
//                getActivity().finish();
//            }
//        }
//
//    }

//    private void findTaskInLocalFile() {
//        Gson gson = new Gson();
//        String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
//        if (!TextUtils.isEmpty(taskListStr)) {
//            Type listType = new TypeToken<List<TaskEntity>>() {
//            }.getType();
//            ArrayList<TaskEntity> listTask = gson.fromJson(taskListStr, listType);
//            if (null != listTask && !listTask.isEmpty()) {
//                for (int i = 0; i < listTask.size(); i++) {
//                    if (listTask.get(i).id.equals(taskDetailViewModel.taskEntity.id)) {
//                        taskDetailViewModel.taskEntity = listTask.get(i);
//                    }
//                }
//            }
//
//        }
//    }
    private static String checkNullString(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        } else {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

//    private void postSceneData() {
//        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
//        multipartBodyBuilder.setType(MultipartBody.FORM);
//
//        if (!TextUtils.isEmpty(deleteId) && deleteId.endsWith(",")) {
//            deleteId = deleteId.substring(0, deleteId.length() - 1);
//        }
//        multipartBodyBuilder.addFormDataPart("userid", AccountManager.getInstance().getUserId())
//                .addFormDataPart("id", taskDetailViewModel.taskEntity.id)
//                .addFormDataPart("taskisok", "0")
//                .addFormDataPart("sampleids", deleteId);
//
//        AMapLocation location = MainApplication.INSTANCE.getMyLocation();
//        if (null != location) {
//            multipartBodyBuilder.addFormDataPart("taskaddress", location.getAddress() + "")
//                    .addFormDataPart("longitude", location.getLongitude() + "")
//                    .addFormDataPart("latitude", location.getLatitude() + "");
//        }
//
//        if (null != taskDetailViewModel.taskEntity.pics && !taskDetailViewModel.taskEntity.pics.isEmpty()) {
//            for (int i = 0; i < taskDetailViewModel.taskEntity.pics.size(); i++) {
//                Pics pics = taskDetailViewModel.taskEntity.pics.get(i);
//                if (!TextUtils.isEmpty(pics.getId())) {
//                    multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileid", pics.getId());
//                    multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileStr", pics.getRemarks());
//                    continue;
//                }
//                String path = taskDetailViewModel.taskEntity.pics.get(i).getOriginalPath();
//                if (TextUtils.isEmpty(path)) continue;
//                File f = new File(path);
//                if (!f.exists()) {
//                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
//                    continue;
//                }
//                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
//                multipartBodyBuilder.addFormDataPart("uploadPic[" + i + "].fileStr", pics.getRemarks() + "")
//                        .addFormDataPart("uploadPic[" + i + "].fileStream", f.getName(), requestImage);
//            }
//        }
//        if (null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {
//
//            for (int i = 0; i < taskDetailViewModel.taskEntity.voides.size(); i++) {
//                Videos videos = taskDetailViewModel.taskEntity.voides.get(i);
//                if (!TextUtils.isEmpty(videos.getId())) {
//                    multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileid", videos.getId());
//                    multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileStr", videos.getRemarks());
//
//                    continue;
//                }
//                File f = new File(videos.getPath());
//                if (!f.exists()) {
//                    Log.e("视频", f.getAbsolutePath());
//                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效视频");
//                    continue;
//                }
//                byte[] fileBytes = null;
//                try {
//                    FileInputStream fis = new FileInputStream(f);
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    byte[] buf = new byte[1024];
//                    int count;
//                    while ((count = fis.read(buf)) != -1) {
//                        baos.write(buf, 0, count); //实际上，如果文件体积比较大的话，不用转码，在这一步就可能OOM了
//                    }
//                    fileBytes = baos.toByteArray();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                RequestBody requestImage = RequestBody.create(MultipartBody.FORM, fileBytes);//把文件与类型放入请求体
//                multipartBodyBuilder.addFormDataPart("uploadVedio[" + i + "].fileStr", videos.getRemarks() + "")
//                        .addFormDataPart("uploadVedio[" + i + "].fileStream", f.getName(), requestImage);
//            }
//        }
//        {
//            File feedfile = new File(taskDetailViewModel.taskEntity.feedfile);
//            if (feedfile.exists()) {
//                Log.e("file", feedfile.getAbsolutePath());
//                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, feedfile);//把文件与类型放入请求体
//                multipartBodyBuilder.addFormDataPart("feedfile", feedfile.getName(), requestFile);//抽样单
//            }
//        }
//        {
//            File feedpicfile = new File(taskDetailViewModel.taskEntity.feedpicfile);
//            if (feedpicfile.exists()) {
//                Log.e("file", feedpicfile.getAbsolutePath());
//                RequestBody requestFile = RequestBody.create(MultipartBody.FORM, feedpicfile);//把文件与类型放入请求体
//                multipartBodyBuilder.addFormDataPart("feedpicfile", feedpicfile.getName(), requestFile);//抽样单
//            }
//        }
//
//        {
//            HashMap<String, String> feedInfoMap = taskDetailViewModel.taskEntity.feedInfoMap;
//            //没填的时候默认值
//
//            if (null != feedInfoMap && !feedInfoMap.isEmpty()) {
//                for (String s : feedInfoMap.keySet()) {
//                    if (!s.startsWith("feed.")) continue;
//                    try {
//                        String value = feedInfoMap.get(s);
//                        multipartBodyBuilder.addFormDataPart(s, value);//抽样单
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        if (null != taskDetailViewModel.taskEntity.taskSamples && !taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
//            multipartBodyBuilder.addFormDataPart("samplecount", taskDetailViewModel.taskEntity.taskSamples.size() + "");
//        } else {
//            multipartBodyBuilder.addFormDataPart("samplecount", "0");
//        }
//        showLoadingDialog();
//        showLoadingDialog("现场信息提交中");
//        RetrofitServiceManager.getInstance().createApiService(Request.class)
//                .uploadtaskinfo(multipartBodyBuilder.build())
//                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
//                .subscribe(new ZBaseObserver<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "添加现场信息成功");
////                        postSampleData();
//                    }
//
//                    @Override
//                    public void onFailure(int code, String message) {
//                        super.onFailure(code, message);
//                        dismissLoadingDialog();
//                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), message);
//                    }
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        super.onSubscribe(d);
//                    }
//                });
//    }

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }


    /**
     * 提示输入抽样单编号对话框
     */
    private void showCodeDialog(){
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("抽样单恢复")
                .setPlaceholder("请在此输入抽样单编号")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            toVerNeedRevert(text.toString().trim());
                            dialog.dismiss();
                            revertDialog = new QMUITipDialog.Builder(getActivity())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                    .setTipWord("样品恢复检测中...")
                                    .create(false);
                            revertDialog.show();
                        } else {
                            Toast.makeText(getActivity(), "请输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    /**
     * 判断当前有没有tasksample
     * @param sampleCode
     * @return
     */
    private void toVerNeedRevert(String sampleCode){
//        revertCheckFormBySampleCode(sampleCode);
        DBManagerFactory.getInstance().getCheckFormManager().querySampleIdBySampleCode(sampleCode,upHandler);

    }

    /**
     * 根据抽样单号比对是不是该任务下的
     * @param sampleCode
     */
    private void requestVerifySampleCodeToTask(String sampleCode){
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getTaskCodeBySampleCode(AccountManager.getInstance().getUserId(),sampleCode)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<VerifyTaskStatusVo>() {
                    @Override
                    public void onSuccess(VerifyTaskStatusVo verifyTaskStatusVo) {

                        if(verifyTaskStatusVo.getTaskCode().equals(taskDetailViewModel.taskEntity.taskCode) && !verifyTaskStatusVo.getTaskstatus().equals("2")){
                            requestVerifySampleCode(taskDetailViewModel.taskEntity.id,sampleCode);
                        }else{
                            if(revertDialog.isShowing()){
                                revertDialog.dismiss();
                            }
                            String tipMsg = "";
                            if(verifyTaskStatusVo.getTaskstatus().equals("2")){
                                tipMsg = "该抽样单号已经被任务‘" + verifyTaskStatusVo.getTaskCode() + "’"+"已提交，请重新核对！";
                            }else{
                                tipMsg = "该抽样单号不属于该任务，请到‘" + verifyTaskStatusVo.getTaskCode() + "’任务下恢复！";
                            }


                            new QMUIDialog.MessageDialogBuilder(getActivity())
                                    .setTitle("温馨提示")
                                    .setMessage(tipMsg)
                                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                                        @Override
                                        public void onClick(QMUIDialog dialog, int index) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                        @Override
                                        public void onClick(QMUIDialog dialog, int index) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }

                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                    }
                });
    }



    /**
     * 用于验证是否生成过PDF
     * @param taskId
     */
    private void requestVerifySampleCode(String taskId,String sampleCode){
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .getSuccesPDFSampleId(taskId,sampleCode)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String string) {
                        if(revertDialog.isShowing()){
                            revertDialog.dismiss();
                        }
                        if(string.equals("0")){//表示生成过PDF文件，可以恢复数据
                            revertCheckFormBySampleCode(sampleCode);
                        }else{
                            Toast.makeText(getActivity(), "该抽样单号没有生成过PDF文件，无需恢复数据！", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                    }
                });
    }





    /**
     * 提示输入框
     */
    private void showEditTextDialog(){
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("样品信息")
                .setPlaceholder("请在此输入样品名称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            createNewSample(text.toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    /**
     * 提示输入框
     */
    private void showRevertEditTextDialog(CheckForm checkForm){
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("样品信息")
                .setPlaceholder("请在此输入样品名称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            revertTaskSample(text.toString().trim(),checkForm);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
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

    /**
     * 是否开始上传
     * @return
     */

    /**上传dialog*/
    QMUITipDialog upDialog;

    /**恢复检测dialog*/
    QMUITipDialog revertDialog;
    /**
     * 提交现场信息
     */

    private void postScenceData(){
        boolean isReadyPost = true;//默认数据都在，准备好上传
        if (null == taskDetailViewModel.taskEntity.taskSamples || taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
            showUpLoadFail("请创建样品数据!");
            return;
        }

        for(TaskSample taskSample : taskDetailViewModel.taskEntity.taskSamples){
            if(!TextUtils.isEmpty(taskSample.getId())){
                CheckForm checkForm = DBManagerFactory.getInstance().getCheckFormManager().queryBuilder().where(CheckFormDao.Properties.Id.eq(taskSample.getId())).build().unique();
                if(checkForm != null && !TextUtils.isEmpty(checkForm.getPdfPath())){

                }else{
                    isReadyPost = false;
                }
            }else{
                isReadyPost = false;
            }
        }

        if(!isReadyPost){
            showUpLoadFail("任务中存在一个样品或者多个样品未生成PDF文件，请检查好后再上传！");
            return;
        }


        upDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("样品信息提交中...")
                .create(false);
        upDialog.show();

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
                String path = taskDetailViewModel.taskEntity.pics.get(i).getCompressPath();
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

//        //处置单上传
//        {
//            File disposalpicfile = new File(taskDetailViewModel.taskEntity.disposalpicfile);
//            if (disposalpicfile.exists()) {
//                Log.e("file", disposalpicfile.getAbsolutePath());
//                requestFile.put("disposalpicfile", taskDetailViewModel.taskEntity.disposalpicfile);//处置单
//            }
//        }



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
                .subscribe(new ZBaseObserver<String>(false) {
                    @Override
                    public void onSuccess(String s) {
                        postSampleData();

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


//    private void postScenceByHttpURLConnection() {
//
//        upDialog = new QMUITipDialog.Builder(getActivity())
//                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                .setTipWord("样品信息提交中...")
//                .create(false);
//        upDialog.show();
//
//        Map<String, String> requestText = new HashMap<String, String>();
//        Map<String, String> requestFile = new HashMap<String, String>();
//        if (!TextUtils.isEmpty(deleteId) && deleteId.endsWith(",")) {
//            deleteId = deleteId.substring(0, deleteId.length() - 1);
//        }
//        requestText.put("userid", AccountManager.getInstance().getUserId());
//        requestText.put("id", taskDetailViewModel.taskEntity.id);
//        requestText.put("taskisok", "0");
//        requestText.put("sampleids", deleteId);
//        if (null != taskDetailViewModel.taskEntity.taskSamples && !taskDetailViewModel.taskEntity.taskSamples.isEmpty()) {
//            requestText.put("samplecount", taskDetailViewModel.taskEntity.taskSamples.size() + "");
//        } else {
//            requestText.put("samplecount", "0");
//        }
//        AMapLocation location = MainApplication.INSTANCE.getMyLocation();
//        if (null != location) {
//            requestText.put("taskaddress", location.getAddress() + "");
//            requestText.put("longitude", location.getLongitude() + "");
//            requestText.put("latitude", location.getLatitude() + "");
//        }
//        if (null != taskDetailViewModel.taskEntity.pics && !taskDetailViewModel.taskEntity.pics.isEmpty()) {
//            for (int i = 0; i < taskDetailViewModel.taskEntity.pics.size(); i++) {
//                Pics pics = taskDetailViewModel.taskEntity.pics.get(i);
//                if (!TextUtils.isEmpty(pics.getId())) {
//                    requestText.put("uploadPic[" + i + "].fileid", pics.getId());
//                    requestText.put("uploadPic[" + i + "].fileStr", pics.getRemarks());
//                    continue;
//                }
//                String path = taskDetailViewModel.taskEntity.pics.get(i).getOriginalPath();
//                if (TextUtils.isEmpty(path)) continue;
//                File f = new File(path);
//                if (!f.exists()) {
//                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效图片");
//                    continue;
//                }
//                requestText.put("uploadPic[" + i + "].fileStr", pics.getRemarks() + "");
//                requestFile.put("uploadPic[" + i + "].fileStream", path);
//            }
//        }
//        if (null != taskDetailViewModel.taskEntity.voides && !taskDetailViewModel.taskEntity.voides.isEmpty()) {
//
//            for (int i = 0; i < taskDetailViewModel.taskEntity.voides.size(); i++) {
//                Videos videos = taskDetailViewModel.taskEntity.voides.get(i);
//                if (!TextUtils.isEmpty(videos.getId())) {
//                    requestText.put("uploadVedio[" + i + "].fileid", videos.getId());
//                    requestText.put("uploadVedio[" + i + "].fileStr", videos.getRemarks());
//
//                    continue;
//                }
//                File f = new File(videos.getPath());
//                if (!f.exists()) {
//                    Log.e("视频", f.getAbsolutePath());
//                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "无效视频");
//                    continue;
//                }
//
//                requestText.put("uploadVedio[" + i + "].fileStr", videos.getRemarks());
//                requestFile.put("uploadVedio[" + i + "].fileStream", videos.getPath());
//            }
//        }
//        {
//            File feedfile = new File(taskDetailViewModel.taskEntity.feedfile);
//            if (feedfile.exists()) {
//                Log.e("file", feedfile.getAbsolutePath());
//                requestFile.put("feedfile", taskDetailViewModel.taskEntity.feedfile);//抽样单
//            }
//        }
//        {
//            File feedpicfile = new File(taskDetailViewModel.taskEntity.feedpicfile);
//            if (feedpicfile.exists()) {
//                Log.e("file", feedpicfile.getAbsolutePath());
//                requestFile.put("feedfile", taskDetailViewModel.taskEntity.feedpicfile);//抽样单
//            }
//        }
//
//        {
//            HashMap<String, String> feedInfoMap = taskDetailViewModel.taskEntity.feedInfoMap;
//            //没填的时候默认值
//
//            if (null != feedInfoMap && !feedInfoMap.isEmpty()) {
//                for (String s : feedInfoMap.keySet()) {
//                    if (!s.startsWith("feed.")) continue;
//                    try {
//                        String value = feedInfoMap.get(s);
//                        requestText.put(s, value);//抽样单
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
////        //处置单上传
////        {
////            HandleForm handleForm =  DbController.getInstance(getActivity()).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(taskDetailViewModel.taskEntity.id)).build().unique();
////            if(handleForm != null){
////                String pdfPath = handleForm.getPdfPath();
////                if(!TextUtils.isEmpty(pdfPath)){
////                    File disposalfile = new File(pdfPath);
////                    if(disposalfile.exists()){
////                        requestFile.put("disposalfile",pdfPath);//处置单
////                    }
////                }
////            }
////
////            requestText.put("adivce.companyname", taskDetailViewModel.taskEntity.id);
////        }
//
////        {
////            File disposalfile = new File(taskDetailViewModel.taskEntity.disposalfile);
////            if (disposalfile.exists()) {
////                Log.e("disposalfile", disposalfile.getAbsolutePath());
////                requestFile.put("disposalfile", taskDetailViewModel.taskEntity.disposalfile);//抽样单
////            }
////        }
//
//        //处置单上传
//        {
//            File disposalpicfile = new File(taskDetailViewModel.taskEntity.disposalpicfile);
//            if (disposalpicfile.exists()) {
//                Log.e("file", disposalpicfile.getAbsolutePath());
//                requestFile.put("disposalpicfile", taskDetailViewModel.taskEntity.disposalpicfile);//处置单
//            }
//        }
//
//
//
//        {
//            HashMap<String, String> adviceInfoMap = taskDetailViewModel.taskEntity.adviceInfoMap;
//            //没填的时候默认值
//
//            if (null != adviceInfoMap && !adviceInfoMap.isEmpty()) {
//                for (String s : adviceInfoMap.keySet()) {
//                    if (!s.startsWith("advice.")) continue;
//                    try {
//                        String value = adviceInfoMap.get(s);
//                        requestText.put(s, value);//抽样单
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//
//        HttpURLConnectionUtil httpReuqest = new HttpURLConnectionUtil();
//
//        new AsyncTask() {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
////                showLoadingDialog();
////                showLoadingDialog("现场信息提交中");
//            }
//
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                String response = null;
//                try {
//                    response = httpReuqest.formUpload(requestText, requestFile, new HttpURLConnectionUtil.PostCallback() {
//                        @Override
//                        public void progressUpdate(int total, int prgress) {
//                            NumberFormat numberFormat = NumberFormat.getInstance();
//                            // 设置精确到小数点位
//                            numberFormat.setMaximumFractionDigits(0);
//                            String result = numberFormat.format((float) prgress / (float) total * 100);
////                            showLoadingDialog("当前进度 " + result + "%");
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return response;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                if (null != o && !TextUtils.isEmpty(o.toString())) {
//                    try {
//                        JSONObject object = new JSONObject(o.toString());
////                        if (object.has("message") && !TextUtils.isEmpty(object.optString("message"))) {
//////                            showUpLoadFail(object.optString("message"));
//////                            com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), object.optString("message"));
////                        }
//                        if (object.has("code") && object.optInt("code") == 200) {
//                            postSampleData();
//                        } else {
//                            upDialog.dismiss();
//                           showUpLoadFail(object.getString("message"));
//                        }
//                    } catch (JSONException e) {
//                        upDialog.dismiss();
//                        showUpLoadFail("数据提交异常，请稍候再试！");
//                        e.printStackTrace();
//                    }
//                } else {
//                    upDialog.dismiss();
//                    showUpLoadFail("数据提交异常，请稍候再试！");
////                    com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "提交失败,请稍后重试");
////                    dismissLoadingDialog();
//                }
//
//
//            }
//        }.execute();
//    }



    private static class CheckCompany{
        public static String company = "湖南省产商品质量监督检验研究院";
        public static String companyId = "1001";
        public static String companyAddress = "长沙市雨花亭新建西路189号";
        public static String EMS = "410007";
        public static String connectName = "康峰";
        public static String connectTel = "0731-89775245";
        public static String connectEmail = "0731-89775242";
    }


}
