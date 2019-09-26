package com.product.sampling.ui;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.os.Handler;
import android.os.Message;
 import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.ui.update.ApkDownLoadService;
import com.product.sampling.ui.update.ApkDownloadTaskInfo;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务信息
 */
public class TaskDetailFragment extends Fragment implements View.OnClickListener {

    private static TaskDetailFragment fragment;
    private final static int FLAG=123;
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
/*    private TextView tv_att;*/
    private LinearLayout linearLayout;
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //此处用handler是为了更新UI中的TextView
                    String content=(String) msg.obj;
                    Log.d("w123e", content);

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


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
        tv_goodscount.setText(taskEntity.goodscount);

        attList=taskEntity.annexfiles;
        if(attList!=null){
            for (int i = 0; i <taskEntity.annexfiles.size(); i++) {

                View view = View.inflate( getActivity(), R.layout.mark_layout, null);
                Map<String,Object> map=attList.get(i);
                String id=(String) map.get("id");
                String fileName=(String) map.get("fileName");
                TextView tv = (TextView) view.findViewById(R.id.textView1);
                tv.setText(fileName);
                tv.setTag(id);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       	TextView tv = (TextView) v.findViewById(R.id.textView1);
                       /* Toast.makeText(getActivity(), "中文"+tv.getText(), Toast.LENGTH_SHORT).show();*/
                        Intent intent = new Intent(getActivity(), ApkDownLoadService.class);
                        intent.putExtra(ApkDownLoadService.DO_WHAT, ApkDownLoadService.ACTION_DOWNLOAD);
                        ApkDownloadTaskInfo apkDownloadTaskInfo = new ApkDownloadTaskInfo();

                        apkDownloadTaskInfo.apkUrl = Constants.IMAGE_DOWNBASE_URL + tv.getTag().toString();

                        int beg=tv.getText().toString().indexOf(".");
                        apkDownloadTaskInfo.fileType=tv.getText().toString().substring(beg+1);






                        File file = new File(getContext().getExternalFilesDir(Environment.getExternalStorageDirectory().getAbsolutePath()), apkDownloadTaskInfo.getApkFileName());
                        apkDownloadTaskInfo.apkLocalPath = file.getAbsolutePath();
                        intent.putExtra("taskInfo", apkDownloadTaskInfo);
                        getContext().startService(intent);


                    }
                });


                linearLayout.addView(view);
            }
        }



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
            ((TaskDetailActivity) getActivity()).checkSelectMenu(2);
        }
    }

   /* private void connectServlet( String tag ) {
        String urlString =  Constants.IMAGE_DOWNBASE_URL +tag;
        //连接网络的权限不要忘记添加
        URL url;
        try {
            //创建URL，传入网址
            url = new URL(urlString);
            //建立URL连接，打开连接
            URLConnection connect = url.openConnection();
            //获得连接的输入流，读取传过来的信息
            InputStream input=connect.getInputStream();
            //封装读取文件
            BufferedReader br=new BufferedReader(new InputStreamReader(input));
            String line =br.readLine();
            StringBuffer builder=new StringBuffer();
            while(line!=null){
                builder.append(line);
                Log.d("qwe", line);
                line=br.readLine();
            }
            //通过Msg来传递消息，在handler中更新线程
            Message msg=handler.obtainMessage();
            msg.what=FLAG;
            msg.obj=builder.toString().trim();
            Log.d("dfdsfsdfe", builder.toString().trim());
            handler.sendMessage(msg);
            br.close();
            input.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/



}
