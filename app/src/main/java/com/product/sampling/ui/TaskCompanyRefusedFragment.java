package com.product.sampling.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.product.sampling.R;
import com.product.sampling.bean.TaskEntity;

/**
 * 未抽到样品
 */
public class TaskCompanyRefusedFragment extends Fragment {

    public TaskCompanyRefusedFragment() {

    }

    static TaskCompanyRefusedFragment fragment;

    public static Fragment newInstance(TaskEntity task) {
        Bundle args = new Bundle();
        args.putParcelable("task", task);
        if (fragment == null) {
            fragment = new TaskCompanyRefusedFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_company_refused, container, false);

        return rootView;
    }
}
