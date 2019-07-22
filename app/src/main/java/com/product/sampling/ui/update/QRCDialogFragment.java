package com.product.sampling.ui.update;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.product.sampling.R;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.ZXingUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author zhouhao
 * @create 2019-06-31 11:07
 */
public class QRCDialogFragment extends DialogFragment {


    private ImageView mIvQrc;
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvName;
    private TextView mTvModel;
    private TextView mTvCount;
    private TextView mTvDate;
    private TextView mTvPersonName;
    private MaterialButton mBtnSubmit;


    private TaskSample taskSample;


    public static QRCDialogFragment newInstance(TaskSample taskSample) {

        Bundle args = new Bundle();
        args.putSerializable("taskSample", taskSample);
        QRCDialogFragment fragment = new QRCDialogFragment();
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
        View view = inflater.inflate(R.layout.qrc_layout, container, false);
        mIvQrc = view.findViewById(R.id.iv_qrc);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvContent = view.findViewById(R.id.tv_content);
        mTvName = view.findViewById(R.id.tv_name);
        mTvModel = view.findViewById(R.id.tv_model);
        mTvCount = view.findViewById(R.id.tv_count);
        mTvDate = view.findViewById(R.id.tv_date);
        mTvPersonName = view.findViewById(R.id.tv_person_name);
        mBtnSubmit = view.findViewById(R.id.btn_submit);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (taskSample == null) dismiss();
        mTvTitle.setText("湖南省商品质量监督检查研究院");
        if (TextUtils.isEmpty(taskSample.getId())) {
            taskSample.setId(System.currentTimeMillis() + "");
        }
        mTvContent.setText("样品编码:" + taskSample.getId());
        TaskDetailViewModel taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);

        mTvPersonName.setText("抽样人:" + taskDetailViewModel.taskEntity.doman);
        if (null != taskSample.samplingInfoMap && taskSample.samplingInfoMap.size() > 0) {
            String value;
            for (String s : taskSample.samplingInfoMap.keySet()) {
                if (!s.startsWith("sampling.")) continue;
                value = taskSample.samplingInfoMap.get(s);
                if (s.equals("sampling.productname")) {
                    mTvName.setText(null == value ? "" : ("样品名称:" + value));
                } else if (s.equals("sampling.taskcode")) {
                    mTvContent.setText(null == value ? "" : ("样品编码:" + value));
                } else if (s.equals("sampling.productmodel")) {
                    mTvModel.setText(null == value ? "" : ("样品型号:" + value));
                } else if (s.equals("sampling.samplingcount")) {
                    mTvCount.setText(null == value ? "" : ("抽样数量:" + value));
                } else if (s.equals("sampling.fillInDate")) {
                    mTvDate.setText(null == value ? "" : ("抽样日期:" + value));
//                } else if (s.equals("sampling.productname")) {
//                    mTvPersonName.setText(null == value ? "" : ("抽样人:" + value));
                }
            }
        }


        Bitmap bitmap = ZXingUtils.createQRImage(taskSample.getId(), 100, 100);
        mIvQrc.setImageBitmap(bitmap);
        mBtnSubmit.setOnClickListener(v -> {
            if (null != bitmap) {
                Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .map(b -> saveBitmap("qrc", bitmap))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(path -> {
                            LogUtils.e("saveImage", path);
                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
                            shareBySystem(path);
                            dismiss();
                        }, e -> ToastUtil.showShortToast(getActivity(), e != null ? e.getMessage() : "error"));
            }
        });
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
        Bundle bundle = getArguments();
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);

    }
}
