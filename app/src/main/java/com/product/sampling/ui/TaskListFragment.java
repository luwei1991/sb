package com.product.sampling.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.adapter.SpinnerSimpleAdapter;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskResultBean;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;

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
    View recyclerView;
    ImageView mIVdistance;
    ImageView mIVTime;
    boolean isTimeFromLowToHigh = true;
    boolean isDistanceFromLowToHigh = true;


    public static TaskListFragment newInstance(String title, String taskstatus) {
        Bundle args = new Bundle();
        args.putString(ARG_TASK_STATUS, taskstatus);
        args.putString(ARG_TITLE, title);
        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_TITLE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
        }
        getMenuData();
        getData();
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
        mIVdistance = rootView.findViewById(R.id.iv_sort_distance);
        mIVTime = rootView.findViewById(R.id.iv_sort_time);
        rootView.findViewById(R.id.tv_range).setOnClickListener(this);
        rootView.findViewById(R.id.tv_date).setOnClickListener(this);
        ArrayList type = new ArrayList();
        type.add("食品");
        type.add("化工");
        SpinnerSimpleAdapter coinSpinnerdapter = new SpinnerSimpleAdapter(getActivity(), type);
        Spinner spinner = rootView.findViewById(R.id.spinner_type);
        spinner.setAdapter(coinSpinnerdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinnerArea = rootView.findViewById(R.id.spinner_area);
        ArrayList area = new ArrayList();
        area.add("北京");
        area.add("海南");
        SpinnerSimpleAdapter areaSpinnerdapter = new SpinnerSimpleAdapter(getActivity(), area);
        spinnerArea.setAdapter(areaSpinnerdapter);
        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    private void getData() {
        Bundle b = getArguments();
        String status = b.getString(ARG_TASK_STATUS);

        disposable = NetWorkManager.getRequest().taskList(AccountManager.getInstance().getUserId(), status, null)
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(tasks -> {
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView, tasks);
                }, throwable -> {
                    if (isVisible()) {
                        showToast(((ApiException) throwable).getDisplayMessage());
                    }
                    Log.e("throwable", "" + ((ApiException) throwable).getDisplayMessage());
                });
    }

    private void getMenuData() {
        disposable = NetWorkManager.getRequest().getArea(null, null)
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(tasks -> {
                    Log.e("tasks", tasks.toString());
                }, throwable -> {
                    if (isVisible()) {
                        showToast(((ApiException) throwable).getDisplayMessage());
                    }

                    Log.e("throwable", "" + ((ApiException) throwable).getDisplayMessage());
                });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, TaskResultBean task) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter((AppCompatActivity) getActivity(), task.list, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_range:
                isDistanceFromLowToHigh = !isDistanceFromLowToHigh;
                if (isDistanceFromLowToHigh) {
                    mIVdistance.setImageResource(R.mipmap.task_triangle2);
                } else {
                    mIVdistance.setImageResource(R.mipmap.task_triangle);
                }
                getData();
                break;
            case R.id.tv_date:
                isTimeFromLowToHigh = !isTimeFromLowToHigh;
                if (isTimeFromLowToHigh) {
                    mIVTime.setImageResource(R.mipmap.task_triangle2);
                } else {
                    mIVTime.setImageResource(R.mipmap.task_triangle);
                }
                getData();
                break;

        }
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<TaskEntity> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTwoPane) {
                }
                if (view.getId() == R.id.tv_map) {
                    view.getContext().startActivity(new Intent(view.getContext(), BasicMapActivity.class));
                } else if (view.getId() == R.id.tv_fill_info) {
                    view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtra("task", (TaskEntity) view.getTag()));
                }
            }
        };

        SimpleItemRecyclerViewAdapter(AppCompatActivity parent,
                                      List<TaskEntity> items,
                                      boolean twoPane) {
            mValues = items;
            mTwoPane = twoPane;
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
            holder.mTextViewFill.setOnClickListener(mOnClickListener);
            holder.mTextViewFill.setTag(task);

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
}
