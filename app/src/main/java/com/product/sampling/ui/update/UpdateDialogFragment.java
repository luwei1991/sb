package com.product.sampling.ui.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.bean.UpdateEntity;
import com.product.sampling.maputil.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author zhouhao
 * @create 2019-06-31 11:07
 */
public class UpdateDialogFragment extends DialogFragment {


    @BindView(R.id.web_title)
    TextView webTitle;
    @BindView(R.id.tv_update_content)
    TextView tvUpdateContent;
    @BindView(R.id.dialog_btn_cancel)
    MaterialButton dialogBtnCancel;
    @BindView(R.id.dialog_btn_sure)
    MaterialButton dialogBtnSure;

    @BindView(R.id.ll_middle)
    View line;


    private UpdateEntity mUpdateEntity;


    public static UpdateDialogFragment newInstance(UpdateEntity mUpdateEntity) {

        Bundle args = new Bundle();
        args.putSerializable("mUpdateEntity", mUpdateEntity);
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUpdateEntity = (UpdateEntity) getArguments().getSerializable("mUpdateEntity");

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updata_dialog, container, false);
        ButterKnife.bind(this, view);
//        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mUpdateEntity == null) dismiss();
        webTitle.setText("升级提醒");
        tvUpdateContent.setText(mUpdateEntity.getRemark());
        dialogBtnCancel.setOnClickListener(v -> dismiss());
        dialogBtnSure.setOnClickListener(v -> startUpdate());
        //强制升级
        if ("1".equals(mUpdateEntity.getIsforce())) {
            dialogBtnCancel.setVisibility(View.GONE);
            setCancelable(false);
            line.setVisibility(View.GONE);
        } else {
            dialogBtnCancel.setVisibility(View.VISIBLE);
            setCancelable(true);
            line.setVisibility(View.VISIBLE);
        }
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void updateProgressChange(DownloadApkProgressEvent event) {
//        dialogBtnSure.setText(updateStr+"("+event.progress+"%)");
//    }

    private void startUpdate() {

        Intent intent = new Intent(getActivity(), ApkDownLoadService.class);
        intent.putExtra(ApkDownLoadService.DO_WHAT, ApkDownLoadService.ACTION_DOWNLOAD);
        ApkDownloadTaskInfo apkDownloadTaskInfo = new ApkDownloadTaskInfo();

        apkDownloadTaskInfo.apkUrl = Constants.IMAGE_DOWNBASE_URL + mUpdateEntity.getAppfileid();
        apkDownloadTaskInfo.apkVer = mUpdateEntity.getId();

        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkDownloadTaskInfo.getApkFileName());
        apkDownloadTaskInfo.apkLocalPath = file.getAbsolutePath();
        intent.putExtra("taskInfo", apkDownloadTaskInfo);
        getContext().startService(intent);

        ToastUtil.show(getActivity(), "更新中...");
        dismiss();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
    }


}
