package com.product.sampling.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.product.sampling.R;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.DownAnalisUtil;

import java.util.List;
import java.util.Map;

/**
 * 任务信息
 */
public class TaskDetailFragment extends Fragment implements View.OnClickListener {

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
    private TextView tv_goodscount;
    private List<Map<String,Object>> attList;
    private LinearLayout linearLayout;

    private TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putSerializable("task", task);
        if (fragment == null) {
            fragment = new TaskDetailFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_detail, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        taskDetailViewModel.orderDetailLiveData.observeForever(new Observer<LoadDataModel<TaskEntity>>() {
            @Override
            public void onChanged(LoadDataModel<TaskEntity> taskEntityLoadDataModel) {
                if(taskEntityLoadDataModel.isSuccess()){
                    TaskEntity taskEntity = taskEntityLoadDataModel.getData();
                    tvPlanname.setText(taskEntity.planname);//计划名称
                    tvPlanno.setText(taskEntity.planno);//项目备案号
                    tvTasktypecount.setText(taskEntity.tasktypecount);//抽样产品
                    tvPlanfrom.setText(taskEntity.planfrom);//计划来源
                    tvStarttime.setText(taskEntity.starttime);//创建时间
                    tvEndtime.setText(taskEntity.endtime);//结束时间
                    tvCompanyname.setText(taskEntity.companyname);//企业名称
                    tv_goodscount.setText(taskEntity.goodscount);//批次
                    attList=taskEntity.annexfiles;
                    if(attList!=null){
                        if(linearLayout.getChildCount() > 0){
                            linearLayout.removeAllViews();
                        }
                        for (int i = 0; i <taskEntity.annexfiles.size(); i++) {
                            View view = View.inflate(getActivity(), R.layout.mark_layout, null);
                            Map<String,Object> map=attList.get(i);
                            String id=(String) map.get("id");
                            String fileName=(String) map.get("fileName");
                            TextView tv = (TextView) view.findViewById(R.id.textView1);
                            tv.setText(fileName);
                            tv.setTag(id);
                            tv.setTextColor(Color.parseColor("#1b88ee"));
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextView tv = (TextView) v.findViewById(R.id.textView1);
//                                    String url = Constants.IMAGE_DOWNBASE_URL + tv.getTag().toString();
                                    DownAnalisUtil.downLoadDocx(tv.getTag().toString(),fileName.substring(fileName.indexOf(".")));
                                }
                            });
                            linearLayout.addView(view);
                        }
                    }
                }
            }
        });


    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tvPlanname = view.findViewById(R.id.tv_planname);
        tvPlanno = view.findViewById(R.id.tv_planno);
        tvTasktypecount = view.findViewById(R.id.tv_tasktypecount);
        tvPlanfrom = view.findViewById(R.id.tv_planfrom);
        tvStarttime = view.findViewById(R.id.tv_starttime);
        tvEndtime = view.findViewById(R.id.tv_endtime);
        tvCompanyname = view.findViewById(R.id.tv_companyname);
        btnSubmit = view.findViewById(R.id.btn_submit);
        tv_goodscount = view.findViewById(R.id.tv_goodscount);
        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout1);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (btnSubmit.getId() == v.getId()) {
//            ((TaskDetailActivity) getActivity()).checkSelectMenu(2);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        taskDetailViewModel.orderDetailLiveData.removeObservers(this);
    }
}
