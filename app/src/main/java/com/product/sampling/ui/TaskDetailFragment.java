package com.product.sampling.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.product.sampling.R;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

/**
 * 任务信息
 */
public class TaskDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private static TaskDetailFragment fragment;

    TaskDetailViewModel taskDetailViewModel;
    private Toolbar toolbar;
    private TextView tvPlanname;
    private TextView tvPlanno;
    private TextView tvTasktypecount;
    private TextView tvPlanfrom;
    private TextView tvStarttime;
    private TextView tvEndtime;
    private TextView tvCompanyname;
    private Button btnSubmit;

    public TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putParcelable("task", task);
        if (fragment == null) {
            fragment = new TaskDetailFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("hhh");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_detail, container, false);

//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
//        }
        initView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        TaskEntity taskEntity = taskDetailViewModel.taskEntity;
        tvPlanname.setText(taskEntity.planname);
        tvPlanno.setText(taskEntity.planno);
        tvTasktypecount.setText(taskEntity.tasktypecount);
        tvPlanfrom.setText(taskEntity.planfrom);
        tvStarttime.setText(taskEntity.starttime);
        tvEndtime.setText(taskEntity.endtime);
        tvCompanyname.setText(taskEntity.companyname);

    }

    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tvPlanname = (TextView) view.findViewById(R.id.tv_planname);
        tvPlanno = (TextView) view.findViewById(R.id.tv_planno);
        tvTasktypecount = (TextView) view.findViewById(R.id.tv_tasktypecount);
        tvPlanfrom = (TextView) view.findViewById(R.id.tv_planfrom);
        tvStarttime = (TextView) view.findViewById(R.id.tv_starttime);
        tvEndtime = (TextView) view.findViewById(R.id.tv_endtime);
        tvCompanyname = (TextView) view.findViewById(R.id.tv_companyname);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
    }
}
