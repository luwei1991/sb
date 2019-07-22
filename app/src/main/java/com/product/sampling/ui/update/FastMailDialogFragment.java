package com.product.sampling.ui.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.product.sampling.R;
import com.product.sampling.adapter.SpinnerSimpleAdapter;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskProvince;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.bean.UserInfoBean;
import com.product.sampling.httpmoudle.BaseHttpResult;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.httpmoudle.error.ExecptionEngin;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.ActivityUtils;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.ZXingUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author zhouhao
 * @create 2019-06-31 11:07
 */
public class FastMailDialogFragment extends androidx.fragment.app.DialogFragment {


    private TaskSample taskSample;
    private EditText mMailcode;
    private EditText mSendman;
    private EditText mSendtel;
    private EditText mSendcity;
    private Spinner mSendsheng;
    private Spinner mSendshi;
    private Spinner mSendqu;
    private EditText mSendaddress;
    private EditText mReceivedman;
    private EditText mReceivedtel;
    private EditText mReceivedcity;
    private Spinner mReceivedsheng;
    private Spinner mReceivedshi;
    private Spinner mReceivedqu;
    private EditText mReceivedaddress;
    private MaterialButton mBtnSubmit;
    TaskEntity taskEntity;

    public static FastMailDialogFragment newInstance(TaskSample taskSample) {

        Bundle args = new Bundle();
        args.putSerializable("taskSample", taskSample);
        FastMailDialogFragment fragment = new FastMailDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        taskSample = (TaskSample) getArguments().getSerializable("taskSample");

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fastmail_layout, container, false);
        mMailcode = view.findViewById(R.id.mailcode);
        mSendman = view.findViewById(R.id.sendman);
        mSendtel = view.findViewById(R.id.sendtel);
        mSendcity = view.findViewById(R.id.sendcity);
        mSendsheng = view.findViewById(R.id.sendsheng);
        mSendshi = view.findViewById(R.id.sendshi);
        mSendqu = view.findViewById(R.id.sendqu);
        mSendaddress = view.findViewById(R.id.sendaddress);

        mReceivedman = view.findViewById(R.id.receivedman);
        mReceivedtel = view.findViewById(R.id.receivedtel);
        mReceivedcity = view.findViewById(R.id.receivedcity);
        mReceivedsheng = view.findViewById(R.id.receivedsheng);
        mReceivedshi = view.findViewById(R.id.receivedshi);
        mReceivedqu = view.findViewById(R.id.receivedqu);
        mReceivedaddress = view.findViewById(R.id.receivedaddress);
        mBtnSubmit = view.findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postInfo();
            }
        });
        return view;
    }

    private void postInfo() {
        showLoadingDialog();
        HashMap<String, String> map = new HashMap();
        map.put("userid", AccountManager.getInstance().getUserId());
        map.put("taskid", taskEntity.id);
        map.put("sampleid", taskSample.getId());


        map.put("mailcode", mMailcode.getText().toString());
        map.put("sendman", mSendman.getText().toString());
        map.put("sendtel", mSendtel.getText().toString());
        map.put("sendcity", mSendcity.getText().toString());
        map.put("sendsheng", mSendsheng.getSelectedItem().toString());
        map.put("sendshi", mSendshi.getSelectedItem().toString());
        map.put("sendqu", mSendqu.getSelectedItem().toString());
        map.put("sendaddress", mSendaddress.getText().toString());

        map.put("receivedman", mReceivedman.getText().toString());
        map.put("receivedtel", mReceivedtel.getText().toString());
        map.put("receivedcity", mReceivedcity.getText().toString());
        map.put("receivedsheng", mReceivedsheng.getSelectedItem().toString());
        map.put("receivedshi", mReceivedshi.getSelectedItem().toString());
        map.put("receivedqu", mReceivedqu.getSelectedItem().toString());
        map.put("receivedaddress", mReceivedaddress.getText().toString());

        RetrofitService.createApiService(Request.class)
                .updateFastMail(map)
                .compose(RxSchedulersHelper.io_main())
//                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<BaseHttpResult>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        dismissLoadingDialog();
                        if (!ExecptionEngin.isNetWorkError(code)) {
                            ToastUtil.showShortToast(getActivity(), message);
                        }
                    }

                    @Override
                    public void onSuccess(BaseHttpResult result) {
                        dismissLoadingDialog();
                        ToastUtil.showShortToast(getActivity(), result.message);
                        dismiss();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (taskSample == null) dismiss();
        TaskDetailViewModel taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        taskEntity = taskDetailViewModel.taskEntity;
        if (null != taskDetailViewModel.cityListLiveData.getValue() && null != taskDetailViewModel.cityListLiveData.getValue().getData()) {
            setCity(taskDetailViewModel.cityListLiveData.getValue().getData());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
    }

    public String saveBitmap(String bitName, Bitmap mBitmap) {
        File f = new File("/sdcard/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    public void shareBySystem(String path) {
        File doc = new File(path);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(doc));

        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.product.sampling.fileprovider", doc);
//        share.setDataAndType(contentUri, "application/vnd.android.package-archive");
        share.setDataAndType(contentUri, "application/pdf");

        startActivity(Intent.createChooser(share, "分享文件"));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);

    }

    void setCity(ArrayList<TaskProvince> listCity) {

        SpinnerSimpleAdapter privoceSpinnerdapter = new SpinnerSimpleAdapter(getActivity(), listCity);
        SpinnerSimpleAdapter citySpinnerdapter = new SpinnerSimpleAdapter(getActivity(), listCity.get(0).shicitys);
        SpinnerSimpleAdapter areaSpinnerdapter = new SpinnerSimpleAdapter(getActivity(), listCity.get(0).shicitys.get(0).qucitys);

        mSendsheng.setAdapter(privoceSpinnerdapter);
        mSendsheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySpinnerdapter.setDataList(listCity.get(position).shicitys);
                mSendshi.setAdapter(citySpinnerdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSendshi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaSpinnerdapter.setDataList(listCity.get(mSendsheng.getSelectedItemPosition()).shicitys.get(position).qucitys);
                mSendqu.setAdapter(areaSpinnerdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSendsheng.setSelection(0);
        mSendshi.setSelection(0);
        mSendqu.setSelection(0);

        SpinnerSimpleAdapter privoceSpinnerdapter1 = new SpinnerSimpleAdapter(getActivity(), listCity);
        SpinnerSimpleAdapter citySpinnerdapter1 = new SpinnerSimpleAdapter(getActivity(), listCity.get(0).shicitys);
        SpinnerSimpleAdapter areaSpinnerdapter1 = new SpinnerSimpleAdapter(getActivity(), listCity.get(0).shicitys.get(0).qucitys);

        mReceivedsheng.setAdapter(privoceSpinnerdapter1);
        mReceivedsheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySpinnerdapter1.setDataList(listCity.get(position).shicitys);
                mReceivedshi.setAdapter(citySpinnerdapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mReceivedshi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaSpinnerdapter1.setDataList(listCity.get(mSendsheng.getSelectedItemPosition()).shicitys.get(position).qucitys);
                mReceivedqu.setAdapter(areaSpinnerdapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mReceivedsheng.setSelection(0);
        mReceivedshi.setSelection(0);
        mReceivedqu.setSelection(0);
    }

    private AlertDialog alertDialog;

    public void showLoadingDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(true);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        alertDialog.show();
        alertDialog.setContentView(R.layout.loading_alert);
        alertDialog.setCanceledOnTouchOutside(true);
    }

    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
