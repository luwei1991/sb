package com.product.sampling.ui;

import android.app.Activity;
import android.content.Context;
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

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.product.sampling.R;
import com.product.sampling.adapter.SpinnerSimpleAdapter;
import com.product.sampling.bean.Task;
import com.product.sampling.dummy.DummyContent;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class TaskListFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    Disposable disposable;
    View recyclerView;

    public static TaskListFragment newInstance() {

        Bundle args = new Bundle();

        TaskListFragment fragment = new TaskListFragment();
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
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_task_list, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        }
        recyclerView = rootView.findViewById(R.id.item_list);
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
                getData();
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
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    private void getData() {
        disposable = NetWorkManager.getRequest().getTaskList()
                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(tasks -> {
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView, tasks);
                }, throwable -> {
                    ToastUtil.showToast(getActivity(), ((ApiException) throwable).getDisplayMessage());

                    Log.e("throwable", "" + ((ApiException) throwable).getDisplayMessage());
                });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter((AppCompatActivity) getActivity(), task, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_range:
                getData();
                break;
            case R.id.tv_date:
                getData();
                break;

        }
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Task> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTwoPane) {
                }
                view.getContext().startActivity(new Intent(view.getContext(), TaskDetailActivity.class).putExtra("task", (Task) view.getTag()));
            }
        };

        SimpleItemRecyclerViewAdapter(AppCompatActivity parent,
                                      List<Task> items,
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
            Task task = mValues.get(position);
            holder.mTextViewNum.setText(task.task_id);
            holder.mTextViewName.setText(task.comp_name);
            holder.mTextViewAddress.setText(task.comp_addr);
            holder.mTextViewType.setText("产品类型:" + task.pro_type);
//            holder.mTextViewType.setText(task.task_type);
            holder.mTextViewStartTime.setText("开始时间：" + task.start_date);
            holder.mTextViewEndTime.setText("结束时间：" + task.end_date);
            holder.mTextViewCountDown.setText("还有" + task.free_date + "天结束");

            holder.itemView.setTag(task);
            holder.itemView.setOnClickListener(mOnClickListener);
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

            ViewHolder(View view) {
                super(view);
                mTextViewMap = view.findViewById(R.id.tv_map);
                mTextViewNum = (TextView) view.findViewById(R.id.tv_num);

                mTextViewName = (TextView) view.findViewById(R.id.tv_name);
                mTextViewAddress = (TextView) view.findViewById(R.id.tv_address);
                mTextViewType = (TextView) view.findViewById(R.id.tv_type);
                mTextViewStartTime = (TextView) view.findViewById(R.id.tv_start_time);
                mTextViewEndTime = (TextView) view.findViewById(R.id.tv_end_time);
                mTextViewCountDown = (TextView) view.findViewById(R.id.tv_countdown);
            }
        }
    }
}
