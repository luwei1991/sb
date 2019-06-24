package com.product.sampling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.adapter.SpinnerSimpleAdapter;
import com.product.sampling.bean.TaskCity;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskMessage;
import com.product.sampling.bean.TaskProvince;
import com.product.sampling.bean.TaskResultBean;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 任务列表
 */
public class TaskListFragment extends BaseFragment implements View.OnClickListener {

    public static final String ARG_TASK_STATUS = "taskstatus";

    public static final String ARG_TITLE = "title";

    int ordertype = 1;//    0时间倒叙1时间升序

    Disposable disposable;
    RecyclerView recyclerView;
    ImageView mIVDistance;
    View mViewDistance;
    boolean isDistanceFromLowToHigh = true;//    0时间倒叙1时间升序
    TaskDetailViewModel taskDetailViewModel;
    Spinner spinnerProvince;
    Spinner spinnerCity;

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
            String taskList = (String) SPUtil.get(getActivity(), "tasklist", "");
            if (!TextUtils.isEmpty(taskList)) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<TaskEntity>>() {
                }.getType();
                List<TaskEntity> list = gson.fromJson(taskList, listType);
                if (null != list && !list.isEmpty()) {
                    TaskResultBean bean = new TaskResultBean();
                    bean.list = list;
                    setupRecyclerView((RecyclerView) recyclerView, list);
                }
            }
        } else {
            getData();
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
        mIVDistance = rootView.findViewById(R.id.iv_sort_distance);
        mViewDistance = rootView.findViewById(R.id.ll_range);
        mViewDistance.setOnClickListener(this);
        spinnerProvince = rootView.findViewById(R.id.spinner_type);
        spinnerCity = rootView.findViewById(R.id.spinner_area);
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
                // TODO 选择地区刷新列表
                if (!Constants.DEBUG) {
                    getData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerProvince.setSelection(0);
        spinnerCity.setSelection(0);
    }

    private void getData() {
        Bundle b = getArguments();
        String status = b.getString(ARG_TASK_STATUS);
        TaskProvince province = (TaskProvince) spinnerProvince.getSelectedItem();
        TaskCity city = (TaskCity) spinnerCity.getSelectedItem();
        String ordertype = isDistanceFromLowToHigh ? "1" : "0";
        if (null == province) {
            province = new TaskProvince();
        }
        if (null == city) {
            city = new TaskCity();
        }
        // TODO 根据条件筛选任务
        if (Constants.DEBUG) {
            province = new TaskProvince();
            city = new TaskCity();
            ordertype = "";
        }

        RetrofitService.createApiService(Request.class)
                .taskList(AccountManager.getInstance().getUserId(), status, ordertype, province.id, city.id)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<List<TaskEntity>>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        if (isVisible()) {
                            showToast(message);
                        }
                    }

                    @Override
                    public void onSuccess(List<TaskEntity> tasks) {
                        assert recyclerView != null;
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
                        setupRecyclerView(recyclerView, tasks);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
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
                getData();
                break;
        }
    }

    public static class SimpleItemRecyclerViewAdapter
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
                    view.getContext().startActivity(new Intent(view.getContext(), BasicMapActivity.class).putExtras(bundle));
                } else if (view.getId() == R.id.tv_fill_info) {
                    TaskEntity taskEntity = (TaskEntity) view.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("task", taskEntity);
                    view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtras(bundle));
                } else if (view.getId() == R.id.tv_exception) {
                    TaskEntity taskEntity = (TaskEntity) view.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("task", taskEntity);
                    view.getContext().startActivity(new Intent(view.getContext(), TaskExceptionActivity.class).putExtras(bundle));
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
            holder.mTextViewName.setText(task.companyname);
            holder.mTextViewAddress.setText(task.companyaddress);
            holder.mTextViewType.setText("产品类型:" + task.tasktypecount);
            holder.mTextViewStartTime.setText("开始时间：" + task.starttime);
            holder.mTextViewEndTime.setText("结束时间：" + task.endtime);
            if (task.leftday >= 0) {
                holder.mTextViewCountDown.setText("剩余" + task.leftday + "天");
            } else {
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
                holder.mTextViewException.setVisibility(View.INVISIBLE);
                holder.mTextViewFill.setVisibility(View.INVISIBLE);
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
                return listTask;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
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

}

