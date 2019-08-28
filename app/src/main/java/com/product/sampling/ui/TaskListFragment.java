package com.product.sampling.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps2d.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.product.sampling.R;
import com.product.sampling.adapter.AdapterPaging;
import com.product.sampling.adapter.MyDataSource;
import com.product.sampling.adapter.SpinnerSimpleAdapter;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskBean;
import com.product.sampling.bean.TaskCity;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.TaskProvince;
import com.product.sampling.bean.TaskResultBean;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.disposables.Disposable;

import static com.product.sampling.ui.H5WebViewActivity.Intent_Edit;
import static com.product.sampling.ui.H5WebViewActivity.Intent_Order;

/**
 * 任务列表
 */
public class TaskListFragment extends BaseFragment implements View.OnClickListener {

    public static final String ARG_TASK_STATUS = "taskstatus";

    public static final String ARG_TITLE = "title";

    Disposable disposable;
    RecyclerView recyclerView;
    ImageView mIVDistance;
    View mViewDistance;
    boolean isDistanceFromLowToHigh = true;//    0时间倒叙1时间升序
    TaskDetailViewModel taskDetailViewModel;
    Spinner spinnerProvince;
    Spinner spinnerCity;
    RefreshLayout refreshLayout;
    int mPage = 1;
    List<TaskEntity> listData = new ArrayList<>();

    public static TaskListFragment newInstance(String title, String taskstatus) {
        Bundle args = new Bundle();
        args.putString(ARG_TASK_STATUS, taskstatus);
        args.putString(ARG_TITLE, title);
        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        taskDetailViewModel.cityListLiveData.observe(this, new Observer<LoadDataModel<ArrayList<TaskProvince>>>() {
            @Override
            public void onChanged(LoadDataModel<ArrayList<TaskProvince>> listLoadDataModel) {
                if (listLoadDataModel.isSuccess()) {
                    setCity(listLoadDataModel.getData());
                }
            }
        });
        EventBus.getDefault().register(this);
        if (getArguments().getString(ARG_TASK_STATUS).equals("-1")) {

            assert recyclerView != null;
            List<TaskEntity> list = findTaskInLocalFile();
            if (null != list && !list.isEmpty()) {
                for (TaskEntity taskEntity : list) {
                    taskEntity.isLoadLocalData = true;
                }
                TaskResultBean bean = new TaskResultBean();
                bean.list = list;
                setupRecyclerView(recyclerView, list);
            }
        } else {
            getDataListByPage();
            taskDetailViewModel.dataListLiveData.observe(this, new Observer<LoadDataModel<TaskBean>>() {
                @Override
                public void onChanged(LoadDataModel<TaskBean> taskBeanLoadDataModel) {
                    if (taskBeanLoadDataModel.isSuccess()) {
                        setData(taskBeanLoadDataModel.getData());
                    }
                }
            });
        }

    }

    private void setData(TaskBean data) {
        List<TaskEntity> tasks = data.getList();
        if (mPage == 1) {
            listData.clear();
        }
        if (data.isLastPage()) {
            refreshLayout.finishLoadMore(true);
            refreshLayout.setEnableLoadMore(false);
        } else {
            mPage++;
        }
        List<TaskEntity> localData = findTaskInLocalFile();
        if (null != localData && !localData.isEmpty()) {
            for (TaskEntity taskEntity : localData) {
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).id.equals(taskEntity.id)) {
                        tasks.remove(i);
                        break;
                    }
                }

            }
        }
        listData.addAll(tasks);
        if (null == recyclerView.getAdapter()) {
            setupRecyclerView(recyclerView, listData);
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_task_list, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getArguments().getString(ARG_TITLE));
        }
        recyclerView = rootView.findViewById(R.id.item_image_list);
        refreshLayout = rootView.findViewById(R.id.refreshLayout);

        mIVDistance = rootView.findViewById(R.id.iv_sort_distance);
        mViewDistance = rootView.findViewById(R.id.ll_range);
        mViewDistance.setOnClickListener(this);
        spinnerProvince = rootView.findViewById(R.id.spinner_province);
        spinnerCity = rootView.findViewById(R.id.spinner_area);
        if (getArguments().getString(ARG_TASK_STATUS).equals("-1")) {
            rootView.findViewById(R.id.rl_menu).setVisibility(View.GONE);
            refreshLayout.setEnableLoadMore(false);
            refreshLayout.setEnableRefresh(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    mPage = 1;
                    refreshLayout.setEnableLoadMore(true);
                    getDataListByPage();
                    refreshLayout.finishRefresh(2000);
                }
            });
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(RefreshLayout refreshlayout) {
                    getDataListByPage();
                    refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                }
            });
        }

        return rootView;
    }

    void setCity(ArrayList<TaskProvince> listCity) {

        SpinnerSimpleAdapter coinSpinnerdapter = new SpinnerSimpleAdapter(getActivity(), listCity);
        SpinnerSimpleAdapter areaSpinnerdapter = new SpinnerSimpleAdapter(getActivity(), listCity.get(0).shicitys);

        spinnerProvince.setAdapter(coinSpinnerdapter);
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaSpinnerdapter.setDataList(listCity.get(position).shicitys);
                spinnerCity.setAdapter(areaSpinnerdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPage = 1;
                getDataListByPage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerProvince.setSelection(0);
        spinnerCity.setSelection(0);
        spinnerProvince.setPrompt("省");
        spinnerCity.setPrompt("市");

    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<TaskEntity> task) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter((AppCompatActivity) getActivity(), task, !getArguments().getString(ARG_TASK_STATUS).equals("2")));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_range:
                isDistanceFromLowToHigh = !isDistanceFromLowToHigh;
                if (isDistanceFromLowToHigh) {
                    mIVDistance.setImageResource(R.mipmap.task_triangle2);
                } else {
                    mIVDistance.setImageResource(R.mipmap.task_triangle);
                }
                getDataListByPage();
                break;
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<TaskEntity> mValues;
        private boolean isCanEdit = true;//是否可以修改编辑
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.tv_map) {
                    TaskEntity taskEntity = (TaskEntity) view.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("task", taskEntity);
                    if (taskEntity.companylatitude != 0 && taskEntity.companylongitude != 0) {
                        startGaoDeNavi(view.getContext(), new LatLng(taskEntity.companylatitude, taskEntity.companylongitude), taskEntity.companyaddress);
                    } else if (taskEntity.plantype.equals("2")) {
                        Toast.makeText(view.getContext(), "流通领域无法导航", Toast.LENGTH_SHORT).show();
                    }
                } else if (view.getId() == R.id.tv_fill_info) {
                    TaskEntity taskEntity = (TaskEntity) view.getTag();
                    if (taskEntity.isNeedConfirm()) {
                        showDialog(view.getContext(), taskEntity, 1);
                    } else {
                        TaskDetailActivity.GoTaskDetailActivity(view.getContext(), taskEntity);
                    }
                } else if (view.getId() == R.id.tv_exception) {
                    TaskEntity taskEntity = (TaskEntity) view.getTag();
                    if (taskEntity.isNeedConfirm()) {
                        showDialog(view.getContext(), taskEntity, 2);
                    } else {
                        TaskExceptionActivity.GoExceptionActivity(view.getContext(), taskEntity);
                    }
                }
            }
        };

        SimpleItemRecyclerViewAdapter(AppCompatActivity parent,
                                      List<TaskEntity> items,
                                      boolean canEdit) {
            mValues = items;
            isCanEdit = canEdit;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_task_list_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TaskEntity task = mValues.get(position);
            holder.mTextViewNum.setText(task.taskCode);
            holder.mTextViewAddress.setText(task.companyaddress);

            holder.mTextViewName.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(task.companyname)) {
                holder.mTextViewName.setText(task.companyname);
            } else if (task.plantype.equals("2")) {
                holder.mTextViewName.setText("流通领域任务");
            } else {
                holder.mTextViewName.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(task.taskstatus)) {
                if (task.taskstatus.equals("2")) {

                    holder.mTextViewException.setText("查看异常情况");
                    holder.mTextViewFill.setText("查看信息");
                }

            }

            holder.mTextViewAddress.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(task.companyaddress)) {
                holder.mTextViewAddress.setText(task.companyaddress);
            } else {
                holder.mTextViewAddress.setVisibility(View.GONE);
            }

            holder.mTextViewType.setText("产品名:" + task.tasktypecount);
            holder.mTextViewStartTime.setText("开始时间：" + task.starttime);
            holder.mTextViewEndTime.setText("结束时间：" + task.endtime);
            if (task.leftday >= 0) {
                holder.mTextViewCountDown.setText("剩余" + task.leftday + "天");
                if(task.leftday <= 7){
                    holder.mTextViewCountDown.setBackgroundColor(Color.rgb(255, 0, 0));
                }else if(task.leftday >7&&task.leftday <=15){
                    holder.mTextViewCountDown.setBackgroundColor(Color.rgb(255, 255, 0));
                }


            } else {
                holder.mTextViewCountDown.setBackgroundColor(Color.rgb(204, 204, 204));
                holder.mTextViewCountDown.setText("超时" + Math.abs(task.leftday) + "天");
            }

            holder.itemView.setTag(task);
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.mTextViewMap.setOnClickListener(mOnClickListener);
            holder.mTextViewMap.setTag(task);
            holder.mTextViewFill.setOnClickListener(mOnClickListener);
            holder.mTextViewFill.setTag(task);

            holder.mTextViewException.setOnClickListener(mOnClickListener);
            holder.mTextViewException.setTag(task);

            if (!isCanEdit) {
                holder.mTextViewMap.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTextViewMap;
            final TextView mTextViewNum;
            final TextView mTextViewName;
            final TextView mTextViewAddress;
            final TextView mTextViewType;
            final TextView mTextViewStartTime;
            final TextView mTextViewEndTime;
            final TextView mTextViewCountDown;
            final TextView mTextViewException;
            final TextView mTextViewFill;

            ViewHolder(View view) {
                super(view);
                mTextViewMap = view.findViewById(R.id.tv_map);
                mTextViewNum = view.findViewById(R.id.tv_num);

                mTextViewName = view.findViewById(R.id.tv_name);
                mTextViewAddress = view.findViewById(R.id.tv_address);
                mTextViewType = view.findViewById(R.id.tv_type);
                mTextViewStartTime = view.findViewById(R.id.tv_start_time);
                mTextViewEndTime = view.findViewById(R.id.tv_end_time);
                mTextViewCountDown = view.findViewById(R.id.tv_countdown);
                mTextViewException = view.findViewById(R.id.tv_exception);
                mTextViewFill = view.findViewById(R.id.tv_fill_info);

            }
        }
    }

    List<TaskEntity> findTaskInLocalFile() {

        try {
            String taskListStr = (String) SPUtil.get(getActivity(), "tasklist", "");
            if (!TextUtils.isEmpty(taskListStr)) {
                Type listType = new TypeToken<List<TaskEntity>>() {
                }.getType();
                Gson gson = new Gson();
                List<TaskEntity> listTask = gson.fromJson(taskListStr, listType);
                if (null == listTask || listTask.isEmpty()) return new ArrayList<>();
                for (TaskEntity taskEntity : listTask) {
                    taskEntity.isLoadLocalData = true;
                }
                return listTask;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(TaskMessage message) {
        SimpleItemRecyclerViewAdapter adapter = (SimpleItemRecyclerViewAdapter) recyclerView.getAdapter();
        for (int i = adapter.mValues.size() - 1; i >= 0; i--) {
            if (message.message.equals(adapter.mValues.get(i).id)) {
                adapter.mValues.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public static void startGaoDeNavi(final Context context, LatLng endPoint, String endName) {
        if (!isInstalled(context, "com.autonavi.minimap")) {
            Toast.makeText(context, "请先下载安装高德地图客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("androidamap://route?sourceApplication=").append("amap");

        stringBuffer.append("&dlat=").append(endPoint.latitude) //终点纬度
                .append("&dlon=").append(endPoint.longitude) //终点经度
                .append("&dname=").append(endName) //终点地址
                .append("&dev=").append(0)  //起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
                .append("&t=").append(0);  //t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）

        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
    }

    public static boolean isInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        final List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        if (pinfo != null) {
            for (PackageInfo info : pinfo) {
                if (info.packageName.equals(packageName)) {
                    return true;
                }
                //pName.add(pn);
            }
        }
        return false;
    }

    private void getDataListByPage() {
        Bundle b = getArguments();
        String status = b.getString(ARG_TASK_STATUS);
        TaskCity city = (TaskCity) spinnerCity.getSelectedItem();
        String ordertype = isDistanceFromLowToHigh ? 1 + "" : 0 + "";
        TaskProvince province = (TaskProvince) spinnerProvince.getSelectedItem();
        if (null == city || (null != province && "全部".equals(province.name))) {
            city = new TaskCity();
        }
        ordertype = "";
        taskDetailViewModel.getDataByPage(getActivity(), mPage, status, ordertype, city.id);
    }

    private void showDialog(Context context, TaskEntity taskEntity, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
    /*    builder.setMessage("是否确认执行此任务?");*/
        builder.setMessage("任务确认执行后24小时内必须提交，否则任务将失效，是否确认执行?");
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadtaskinfo(context, taskEntity, index);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public void uploadtaskinfo(Context context, TaskEntity taskEntity, int index) {

        String userid = AccountManager.getInstance().getUserId();
        if (TextUtils.isEmpty(userid)) {
            return;
        }
        RetrofitService.createApiService(Request.class)
                .uploadtaskinfo(userid, taskEntity.id)
                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<BaseHttpResult>() {
                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                    }

                    @Override
                    public void onSuccess(BaseHttpResult result) {
                        if (result.code == 200) {
                            //刷新列表的suredotime
                            mPage = 1;
                            getDataListByPage();
                            if (index == 1) {
                                TaskDetailActivity.GoTaskDetailActivity(context, taskEntity);
                            } else if (index == 2) {
                                TaskExceptionActivity.GoExceptionActivity(context, taskEntity);
                            }
                        } else {
                            ToastUtil.showShortToast(context, result.message);
                        }

                    }
                });
    }
}
