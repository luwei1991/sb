package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.MainApplication;
import com.product.sampling.R;
import com.product.sampling.adapter.SimpleItemRecyclerViewAdapter;
import com.product.sampling.bean.TaskBean;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.ui.base.BaseFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.product.sampling.ui.TaskDetailActivity.TASK_NOT_UPLOAD;


/**
 * 一个菜逼北京开发写的（真的垃圾）
 * 任务列表
 */
public class TaskListFragment extends BaseFragment {
    private static final String TAG = "TaskListFragment";
    private static TaskListFragment fragment;
    Toolbar toolbar;

    QMUITipDialog tipDialog;

    RecyclerView recyclerView;
    ImageView mIVDistance;
    View mViewDistance;
    boolean isDistanceFromLowToHigh = true;//    0时间倒叙1时间升序
    TaskDetailViewModel taskDetailViewModel;
    RefreshLayout refreshLayout;
    EditText name;
    EditText code;
    EditText entName;
    int mPage = 1;
    TextView tvTaskListNums;//任务个数
    TextView tvTaskListStatus;
    List<TaskEntity> listData = new ArrayList<>();
    /**当前被点击的fragment*/
    public static TaskListFragment getInstance(){
        if (fragment == null){
            fragment = new TaskListFragment();
        }
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        Button btn=getView().findViewById(R.id.submit);//根据按钮id获取按钮
        //然后给按钮添加事件9
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(taskDetailViewModel.taskListTag.getValue().equals("-1")){
                    Toast.makeText(getActivity(),"未上传中不支持搜索过滤",Toast.LENGTH_LONG).show();
                }else{
                    getDataListByPage(taskDetailViewModel.taskListTag.getValue(),true);
                }

            }
        });

        taskDetailViewModel.dataListLiveData.observeForever(new Observer<LoadDataModel<TaskBean>>() {
            @Override
            public void onChanged(LoadDataModel<TaskBean> taskBeanLoadDataModel) {
                if (taskBeanLoadDataModel.isSuccess()) {
                    dissmissDialog();
                    setData(taskBeanLoadDataModel.getData());
                }else {
                    dissmissDialog();
                }
            }
        });


        taskDetailViewModel.taskListTag.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mPage = 1;
                if(toolbar != null){
                    switch (s){
                        case "0":
                            toolbar.setTitle("代办任务");
                            break;
                        case "1":
                            toolbar.setTitle("信息复核");
                            break;
                        case "2":
                            toolbar.setTitle("已上传");
                            break;

                        case "-1":
                            toolbar.setTitle("未上传");
                            tvTaskListStatus.setText("未上传任务总数:");
                            List<TaskEntity> localData = LocalTaskListManager.getInstance().query();
                            if(localData != null){
                                tvTaskListNums.setText(String.valueOf(localData.size()));
                            }else{
                                tvTaskListNums.setText("0");
                            }

                            break;
                    }
                }

                if (s.equals("-1")) {
                    refreshLayout.setEnableLoadMore(false);
                    refreshLayout.setEnableRefresh(false);
                } else {
                    refreshLayout.setEnableLoadMore(true);
                    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                        @Override
                        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                            mPage = 1;
                            refreshLayout.setEnableLoadMore(true);
                            getDataListByPage(taskDetailViewModel.taskListTag.getValue(),false);
                            refreshLayout.finishRefresh(2000);
                        }
                    });
                    refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore(RefreshLayout refreshlayout) {
                            getDataListByPage(taskDetailViewModel.taskListTag.getValue(),false);
                            refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                        }
                    });
                }


                if(!s.equals("-1")){
                    getDataListByPage(s,false);
                }else{
                    List<TaskEntity> list = LocalTaskListManager.getInstance().query();
                    setupRecyclerView(recyclerView, list == null?new ArrayList<>():list);
//                    List<TaskEntity> list = findTaskInLocalFile();
//                    if (null != list && !list.isEmpty()) {
//                        for (TaskEntity taskEntity : list) {
//                            taskEntity.isLoadLocalData = true;
//                        }
//                        TaskResultBean bean = new TaskResultBean();
//                        bean.list = list;
//                        setupRecyclerView(recyclerView, list);
//                    }else{
//                        setupRecyclerView(recyclerView, list ==null?new ArrayList<>():list);
//                    }
                }
            }
        });


    }

    /**
     * 隐藏dialog
     */
    private void dissmissDialog(){
        if(tipDialog != null && tipDialog.isShowing()){
            tipDialog.dismiss();
        }
    }


    private void setData(TaskBean data) {
        List<TaskEntity> tasks = data.getList();//接口返回的list
//        List<TaskEntity> localData = findTaskInLocalFile();//本地的list就是未上传的list
        List<TaskEntity> localData = LocalTaskListManager.getInstance().query();

        if (mPage == 1) {
            listData.clear();
        }
        if (data.isLastPage()) {
            refreshLayout.finishLoadMore(true);
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
            mPage++;
        }

        if (null != localData && localData.size() > 0) {
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
        //显示任务个数
        String status = taskDetailViewModel.taskListTag.getValue();
        switch (status){
            case "0":
                tvTaskListStatus.setText("待办任务任务总数:");//接口返回减去本地的
                tvTaskListNums.setText(String.valueOf(listData.size()));
                break;
            case "1":
                tvTaskListStatus.setText("信息复核任务总数:");
                tvTaskListNums.setText(String.valueOf(listData.size()));
                break;
            case "2":
                tvTaskListStatus.setText("已上传任务总数:");
                tvTaskListNums.setText(String.valueOf(listData.size()));
                break;

        }
        if (null == recyclerView.getAdapter()) {
            setupRecyclerView(recyclerView, listData);
        } else {
            simpleItemRecyclerViewAdapter.setData(tasks);
//            ((SimpleItemRecyclerViewAdapter)recyclerView.getAdapter()).setData();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_task_list, container, false);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        toolbar = rootView.findViewById(R.id.toolbar);
        recyclerView = rootView.findViewById(R.id.item_image_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(getActivity(),null,taskDetailViewModel.taskListTag.getValue().equals("2"));
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
        refreshLayout = rootView.findViewById(R.id.refreshLayout);
        mIVDistance = rootView.findViewById(R.id.iv_sort_distance);
        tvTaskListNums = rootView.findViewById(R.id.tv_task_nums);
        tvTaskListStatus = rootView.findViewById(R.id.tv_task_status);

        return rootView;
    }

    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<TaskEntity> task) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        if(simpleItemRecyclerViewAdapter == null){
          simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(getActivity(),task,taskDetailViewModel.taskListTag.getValue().equals("2"));
          simpleItemRecyclerViewAdapter.setData(task);
        }else{
            simpleItemRecyclerViewAdapter.setData(task);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPage = 1;
        EventBus.getDefault().unregister(this);
        taskDetailViewModel.dataListLiveData.removeObservers(this);
        taskDetailViewModel.taskListTag.removeObservers(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onGetMessage(TaskMessage message) {
        SimpleItemRecyclerViewAdapter adapter = (SimpleItemRecyclerViewAdapter) recyclerView.getAdapter();
        if(adapter != null){
            for (int i = adapter.getItemCount()- 1; i >= 0; i--) {
                //2种情况删除,1从代办种保存的删除，
                if (message.message.equals(adapter.getItem(i).id) && !adapter.getItem(i).taskstatus.equals(TASK_NOT_UPLOAD)) {
                    adapter.remove(i);
                    String size = tvTaskListNums.getText().toString().trim();
                    int curSize = Integer.parseInt(size)-1;
                    tvTaskListNums.setText(String.valueOf(curSize));
                    break;
                }
                //2:提交完成的message.isRemove为true就是提交删除。
                if (message.message.equals(adapter.getItem(i).id) && adapter.getItem(i).taskstatus.equals(TASK_NOT_UPLOAD) && message.isRemove) {
                    adapter.remove(i);
                    String size = tvTaskListNums.getText().toString().trim();
                    int curSize = Integer.parseInt(size)-1;
                    tvTaskListNums.setText(String.valueOf(curSize));
                    break;
                }
            }
        }

    }


    /**
     *
     * @param status
     * @param isSearch 是否点击了搜索 true：点击了，false：未点击
     */
    private void getDataListByPage(String status,boolean isSearch) {
        try {
            tipDialog = new QMUITipDialog.Builder(getActivity() == null?MainApplication.INSTANCE:getActivity())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("加载中......")
                    .create(true);
            tipDialog.show();
        }catch (WindowManager.BadTokenException e){

        }

        String ordertype = isDistanceFromLowToHigh ? 1 + "" : 0 + "";
        ordertype = "";
        name=getView().findViewById(R.id.name);
        code=getView().findViewById(R.id.taskCode);
        entName=getView().findViewById(R.id.entName);
        taskDetailViewModel.getDataByPage(getActivity(), mPage, status, ordertype, "",name.getText().toString(),code.getText().toString(),entName.getText().toString(),isSearch);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tag",taskDetailViewModel.taskListTag.getValue());
    }
}
