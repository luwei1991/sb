package com.product.sampling.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.config.PictureConfig;
import com.product.sampling.R;
import com.product.sampling.bean.Pics;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.CheckForm;
import com.product.sampling.db.tables.CheckFormDao;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.MediaHelper;
import com.product.sampling.thread.ThreadManager;
import com.product.sampling.ui.H5WebViewActivity;
import com.product.sampling.ui.LocalTaskListManager;
import com.product.sampling.ui.TaskSampleFragment;
import com.product.sampling.ui.form.CheckOrRecheckFormActivity;
import com.product.sampling.ui.form.HandleLandFormActivity;
import com.product.sampling.ui.masterplate.MasterplterMainActivity;
//import com.product.sampling.ui.update.QRCDialogFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.PicPreViewUtil;
import com.product.sampling.utils.RxSchedulersHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.product.sampling.ui.H5WebViewActivity.Intent_Edit;
import static com.product.sampling.ui.H5WebViewActivity.Intent_Order;
import static com.product.sampling.ui.TaskDetailActivity.TASK_NOT_UPLOAD;
import static com.product.sampling.ui.TaskSampleFragment.Select_Check;
import static com.product.sampling.ui.TaskSampleFragment.Select_Handle;

//import com.product.sampling.ui.update.FastMailDialogFragment;

public class TaskSampleRecyclerViewAdapter extends BaseQuickAdapter<TaskSample, TaskSampleRecyclerViewAdapter.ViewHolder> {

    public static final int RequestCodePdf = 99;
    private TaskSampleFragment fragment;
    boolean isLocalData;
    boolean canEdit;
    private TaskDetailViewModel taskDetailViewModel;
    public static final String [] defaultRemarks = new String []{
            "产品外观",
            "检样",
            "备样",
            "检备样合照"
    };
    private List<Pics> defaultImgList = new ArrayList<>();

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_add) {
                fragment.selectId = (int) view.getTag();
                MediaHelper.startGallery(fragment, PictureConfig.MULTIPLE, MediaHelper.REQUEST_IMAGE_CODE);
            } else if (R.id.iv_reduce == view.getId() || R.id.iv_reduce_video == view.getId()) {
            } else if (R.id.btn_edit_check_sheet == view.getId()) {//监督抽查单
                new QMUIDialog.MessageDialogBuilder(view.getContext())
                        .setTitle("监督抽查单")
                        .setMessage("是否需要编辑监督抽查单？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                toCheckForm(view.getContext(), (Integer) view.getTag());
                                dialog.dismiss();
                            }
                        })
                        .show();
//                showDialog(view.getContext(), 1, (int) view.getTag());
            } else if (R.id.btn_edit_risk_sheet == view.getId()) {//处置单编辑
                showDialog(view.getContext(), 3, (int) view.getTag());

            } else if (R.id.btn_edit_work_sheet == view.getId()) {//处置单编辑
                showDialog(view.getContext(), 4, (int) view.getTag());

            } else if (R.id.btn_upload_check_sheet == view.getId()) {//检查单上传
                fragment.selectId = (int) view.getTag();
                fragment.startGalleryForPdf((int) view.getTag(), Select_Check);

            }  else if (R.id.iv_add_video == view.getId()) {
                fragment.selectId = (int) view.getTag();
                MediaHelper.startVideo(fragment, PictureConfig.CHOOSE_REQUEST);
            } else if (R.id.btn_qrc == view.getId()) {
                int index = (int) view.getTag();
//                QRCDialogFragment.newInstance(mData.get(index), index).show(fragment.getFragmentManager(), "update");
            } else if (R.id.btn_fastmail == view.getId()) {
                int index = (int) view.getTag();
//                FastMailDialogFragment.newInstance(mData.get(index)).show(fragment.getFragmentManager(), "update");
            }else if(R.id.btn_edit_disposal == view.getId()){
                new QMUIDialog.MessageDialogBuilder(view.getContext())
                        .setTitle("处置单")
                        .setMessage("是否需要编辑处置单？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                toHandleForm(view.getContext(), (Integer) view.getTag());
                                dialog.dismiss();
                            }
                        })
                        .show();

            }else if(R.id.tv_delete == view.getId()){//删除样品
                if(canEdit){
                    int indexTage  = (int) view.getTag();
                    new QMUIDialog.MessageDialogBuilder(mContext)
                            .setTitle("删除")
                            .setMessage("确认删除样品信息吗？")
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction("确定", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {

                                    //调用删除id接口
                                    deleteSampling(mData.get(indexTage).getId(),indexTage);
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    Toast.makeText(mContext,"已经上传的样品不能删除",Toast.LENGTH_LONG).show();
                }


            }else if (R.id.btn_upload_disposal == view.getId()) {//处置单
                fragment.selectId = (int) view.getTag();
                fragment.startGalleryForPdf((int) view.getTag(), Select_Handle);

            }
        }
    };




    public void shareBySystem(String path) {
        File doc = new File(path);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(doc));
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(mContext, "com.product.sampling.fileprovider", doc);
        share.setDataAndType(contentUri, "application/pdf");
        mContext.startActivity(Intent.createChooser(share, "分享文件"));
    }

    /**
     * 删除样品信息接口
     */
    private void deleteSampling(String id,int indexTage){
        String userid = AccountManager.getInstance().getUserId();
        String repoType = MasterplterMainActivity.REPORT_TYPE_SAMPLIG;
        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .deleteSamplingCode(userid,id,repoType)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String r) {
                        //按下确定键后的事件
                        fragment.deleteId += mData.get(indexTage).getId() + ",";
                        mData.remove(indexTage);
                        notifyDataSetChanged();
//                        notifyItemRemoved(indexTage);
//                        notifyItemRangeChanged(indexTage,1);
                        DBManagerFactory.getInstance().getCheckFormManager().deleteBySampleId(id);
//                        CheckForm checkForm =  DbController.getInstance(mContext).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(id)).build().unique();
//                        if(checkForm != null){
//                            String pdfPath = checkForm.getPdfPath();
//                            if(!TextUtils.isEmpty(pdfPath)){
//                                File pdfFile = new File(pdfPath);
//                                if(pdfFile.exists()){
//                                    pdfFile.delete();
//                                }
//                            }
//
//                            String sign1 = checkForm.getAcceptCompanySignImagePath();
//                            if(!TextUtils.isEmpty(sign1)){
//                                File sign1File = new File(sign1);
//                                if(sign1File.exists()){
//                                    sign1File.delete();
//                                }
//                            }
//
//                            String sign2 = checkForm.getCheckPeopleSignImagePath_1();
//                            if(!TextUtils.isEmpty(sign2)){
//                                File sign2File = new File(sign2);
//                                if(sign2File.exists()){
//                                    sign2File.delete();
//                                }
//                            }
//
//                            String sign4 = checkForm.getCheckPeopleSignImagePath_2();
//                            if(!TextUtils.isEmpty(sign4)){
//                                File sign2File = new File(sign4);
//                                if(sign2File.exists()){
//                                    sign2File.delete();
//                                }
//                            }
//
//                            String sign3 = checkForm.getProductCompanySignImagePath();
//                            if(!TextUtils.isEmpty(sign3)){
//                                File sign3File = new File(sign3);
//                                if(sign3File.exists()){
//                                    sign3File.delete();
//                                }
//                            }
//                            DbController.getInstance(mContext).getDaoSession().getCheckFormDao().delete(checkForm);
//                        }

                        if(taskDetailViewModel.taskEntity.taskSamples != null && taskDetailViewModel.taskEntity.taskSamples.size() >0){
                            for(int i= 0; i < taskDetailViewModel.taskEntity.taskSamples.size(); i++){
                                if(id.equals(taskDetailViewModel.taskEntity.taskSamples.get(i).getId())){
                                    taskDetailViewModel.taskEntity.taskSamples.remove(i);
                                }
                            }
                        }
                        //保存
                        taskDetailViewModel.taskEntity.taskstatus = TASK_NOT_UPLOAD;
                        LocalTaskListManager.getInstance().update(taskDetailViewModel.taskEntity);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                    }
                });


    }

    @Override
    protected void convert(ViewHolder holder, TaskSample task) {

        int position = holder.getAdapterPosition() - 1 >= 0 ? holder.getAdapterPosition() - 1 : 0;

        holder.mTextViewTitle.setText(TextUtils.isEmpty(task.getSamplename()) ? " " : task.getSamplename());
        CheckForm checkForm =  DbController.getInstance(mContext).getDaoSession().getCheckFormDao().queryBuilder().where(CheckFormDao.Properties.Id.eq(task.getId())).build().unique();

       if(checkForm != null){
           String pdfPath = checkForm.getPdfPath();
           if(!TextUtils.isEmpty(pdfPath)){
               holder.mTextViewCheckSheet.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
               holder.mTextViewCheckSheet.setText("（已填写）");
               holder.mTextViewCheckSheet.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       shareBySystem(pdfPath);
                   }
               });
           }
       }

        holder.mTextViewWorkSheet.setText(task.workfile + "");
        holder.mTextViewRiskSheet.setText(task.riskfile + "");

        if (TextUtils.isEmpty(task.samplingpicfile)) {
            holder.mBtnUploadCheck.setText("(拍照)上传");
        } else {
            holder.mBtnUploadCheck.setText("已拍照");
        }

        if (TextUtils.isEmpty(task.handlepicfile)) {
            holder.mBtnPhotoHandle.setText("(拍照)上传");
        } else {
            holder.mBtnPhotoHandle.setText("已拍照");
        }



        holder.mImageViewAdd.setVisibility(View.GONE);
        holder.mBtnEditCheck.setVisibility(View.VISIBLE);
        holder.mBtnUploadCheck.setVisibility(View.VISIBLE);

        holder.mBtnPhotoHandle.setOnClickListener(mOnClickListener);
        holder.mBtnPhotoHandle.setTag(position);
        holder.mImageViewAdd.setTag(position);
        holder.mImageViewAdd.setOnClickListener(mOnClickListener);
        holder.mImageViewReduce.setTag(position);
        holder.mImageViewReduce.setOnClickListener(mOnClickListener);

        holder.mBtnEditCheck.setTag(position);
        holder.mBtnEditCheck.setOnClickListener(mOnClickListener);

        holder.mBtnUploadCheck.setTag(position);
        holder.mBtnUploadCheck.setOnClickListener(mOnClickListener);

        holder.mBtnUploadCheck.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!TextUtils.isEmpty(task.samplingpicfile)){
                    PicPreViewUtil.showPreView(holder.mBtnUploadCheck,mContext,task.samplingpicfile);
                }else{
                    Toast.makeText(mContext,"请先选择照片！",Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        holder.mBtnEditRisk.setTag(position);
        holder.mBtnEditRisk.setOnClickListener(mOnClickListener);

        holder.mBtnUploadRisk.setTag(position);
        holder.mBtnUploadRisk.setOnClickListener(mOnClickListener);

        holder.mBtnEditWork.setTag(position);
        holder.mBtnEditWork.setOnClickListener(mOnClickListener);

        holder.mBtnUploadWork.setTag(position);
        holder.mBtnUploadWork.setOnClickListener(mOnClickListener);

        holder.mIVReduceVideo.setTag(position);
        holder.mIVReduceVideo.setOnClickListener(mOnClickListener);

        holder.mIVAddVideo.setTag(position);
        holder.mIVAddVideo.setOnClickListener(mOnClickListener);

        holder.mBtnQRC.setTag(position);
        holder.mBtnQRC.setOnClickListener(mOnClickListener);

        holder.mBtnFastMail.setTag(position);
        holder.mBtnFastMail.setOnClickListener(mOnClickListener);
        holder.mBtnHandle.setTag(position);
        holder.mBtnHandle.setOnClickListener(mOnClickListener);

        if (canEdit) {
            holder.mImageViewAdd.setVisibility(View.GONE);
            holder.mImageViewReduce.setVisibility(View.GONE);
            holder.mBtnUploadCheck.setVisibility(View.VISIBLE);
            holder.mBtnUploadRisk.setVisibility(View.VISIBLE);
            holder.mBtnUploadWork.setVisibility(View.VISIBLE);

        } else {
            holder.mIVAddVideo.setVisibility(View.GONE);
            holder.mIVReduceVideo.setVisibility(View.GONE);
            holder.mBtnUploadCheck.setVisibility(View.VISIBLE);
            holder.mBtnUploadRisk.setVisibility(View.VISIBLE);
            holder.mBtnUploadWork.setVisibility(View.VISIBLE);
        }
        holder.mTextViewDeleteSample.setVisibility(View.VISIBLE);
        holder.mTextViewDeleteSample.setOnClickListener(mOnClickListener);
        holder.mTextViewDeleteSample.setTag(position);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.mRecyclerViewImage.setLayoutManager(linearLayoutManager);
        //如果样品图像没有则创建默认的图片
        if(task.getPics().size() == 0){
            defaultImgList.clear();
            for(int i = 0; i < defaultRemarks.length; i++){
                Pics pics = new Pics();
                pics.setRemarks(defaultRemarks[i]);
                defaultImgList.add(pics);
            }
            task.pics.addAll(defaultImgList);
        }
        List<Pics> samplePicList = task.getPics();
        //将非必须填写的图片放在最后一位
        ArrayList<Pics> tempMust = new ArrayList<>();
        ArrayList<Pics> tempNotMust = new ArrayList<>();
        for(int i = 0 ; i < samplePicList.size(); i++){
            String remarks = samplePicList.get(i).getRemarks();
            if(remarks.contains(defaultRemarks[0]) || remarks.contains(defaultRemarks[1]) || remarks.contains(defaultRemarks[2]) || remarks.contains(defaultRemarks[3])){
                tempMust.add(samplePicList.get(i));
            }else{
                tempNotMust.add(samplePicList.get(i));
            }
        }
        tempMust.addAll(tempNotMust);//将其他图片添加到末尾
        //将重新排序后的数据，通知给源数据进行更新
        task.getPics().clear();
        for(Pics pics : tempMust){
            task.getPics().add(pics);
        }
        holder.mRecyclerViewImage.setAdapter(new ImageSampleRecyclerViewAdapter(fragment,tempMust, canEdit, position));
        linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.mRecyclerViewVideo.setLayoutManager(linearLayoutManager);
    }

    public TaskSampleRecyclerViewAdapter(int layoutResId, @Nullable List<TaskSample> data, TaskSampleFragment fragment, boolean isLocalData, boolean canEdit,TaskDetailViewModel taskDetailViewModel) {
        super(layoutResId, data);
        this.fragment = fragment;
        this.isLocalData = isLocalData;
        this.canEdit = canEdit;
        this.taskDetailViewModel=taskDetailViewModel;
    }

    class ViewHolder extends BaseViewHolder {
        final TextView mTextViewTitle;
        final RecyclerView mRecyclerViewImage;
        final RecyclerView mRecyclerViewVideo;
        final ImageView mImageViewAdd;
        final ImageView mImageViewReduce;
        final Button mBtnEditCheck;//检查单编辑
        final Button mBtnUploadCheck;//检查单上传
        final Button mBtnEditWork;//工作单编辑
        final Button mBtnUploadWork;//工作单上传
        final Button mBtnEditRisk;//风险单编辑
        final Button mBtnUploadRisk;//风险单上传
        final Button mBtnQRC;//二维码打印
        final Button mBtnFastMail;//快递单号
        final Button mBtnHandle;//处置单
        final Button mBtnPhotoHandle;//处置单拍照


        final TextView mTextViewCheckSheet;
        final TextView mTextViewWorkSheet;
        final TextView mTextViewRiskSheet;
        final ImageView mIVAddVideo;//添加视频
        final ImageView mIVReduceVideo;//删除视频

        final TextView mTextViewDeleteSample;//删除样品


        ViewHolder(View view) {
            super(view);
            mTextViewTitle = view.findViewById(R.id.tv_title);
            mRecyclerViewImage = view.findViewById(R.id.item_image_list);
            mImageViewAdd = view.findViewById(R.id.iv_add);
            mImageViewReduce = view.findViewById(R.id.iv_reduce);
            mBtnUploadCheck = view.findViewById(R.id.btn_upload_check_sheet);
            mBtnEditCheck = view.findViewById(R.id.btn_edit_check_sheet);
            mTextViewCheckSheet = view.findViewById(R.id.tv_check_sheet);
            mTextViewWorkSheet = view.findViewById(R.id.tv_work_sheet);
            mTextViewRiskSheet = view.findViewById(R.id.tv_risk_sheet);

            mIVReduceVideo = view.findViewById(R.id.iv_reduce_video);
            mIVAddVideo = view.findViewById(R.id.iv_add_video);
            mRecyclerViewVideo = view.findViewById(R.id.item_video_list);
            mBtnEditWork = view.findViewById(R.id.btn_edit_work_sheet);
            mBtnUploadWork = view.findViewById(R.id.btn_upload_work_sheet);
            mBtnEditRisk = view.findViewById(R.id.btn_edit_risk_sheet);
            mBtnUploadRisk = view.findViewById(R.id.btn_upload_risk_sheet);
            mBtnQRC = view.findViewById(R.id.btn_qrc);
            mBtnFastMail = view.findViewById(R.id.btn_fastmail);
            mBtnHandle = view.findViewById(R.id.btn_edit_disposal);
            mTextViewDeleteSample = view.findViewById(R.id.tv_delete);
            mBtnPhotoHandle = view.findViewById(R.id.btn_upload_disposal);


        }
    }

    private void showDialog(Context context, int buttonid, int postion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("是否编辑表单?");
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (buttonid == 1) {
                    Intent intent = new Intent(context, H5WebViewActivity.class);
                     mData.get(postion);
                    String code=mData.get(postion).getId();//编号根据id得来
                    Bundle b = new Bundle();
                    b.putInt(Intent_Order, 1);
                    b.putInt("task", postion);
                    b.putSerializable("map", mData.get(postion).samplingInfoMap);
                    b.putBoolean(Intent_Edit, canEdit);
                    b.putString("code",code);
                    b.putString("resultData","");
                    TaskEntity taskEntity=taskDetailViewModel.taskEntity;
                    String taskId=taskEntity.id;
                    b.putString("taskId",taskId);
                    intent.putExtras(b);
                    fragment.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
                } else if (buttonid == 2) {
                    Intent intent = new Intent(context, H5WebViewActivity.class);
                    Bundle b = new Bundle();
                    b.putInt(Intent_Order, 2);
                    b.putInt("task", postion);
                    b.putSerializable("map", mData.get(postion).adviceInfoMap);
                    b.putBoolean(Intent_Edit, canEdit);
                    intent.putExtras(b);
                    fragment.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
                } else if (buttonid == 3) {
                    Intent intent = new Intent(context, H5WebViewActivity.class);
                    Bundle b = new Bundle();
                    b.putInt(Intent_Order, 5);
                    b.putInt("task", postion);
                    b.putSerializable("map", mData.get(postion).riskInfoMap);
                    b.putBoolean(Intent_Edit, canEdit);
                    intent.putExtras(b);
                    fragment.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
                } else if (buttonid == 4) {
                    Intent intent = new Intent(context, H5WebViewActivity.class);
                    Bundle b = new Bundle();
                    b.putInt(Intent_Order, 6);
                    b.putInt("task", postion);
                    b.putSerializable("map", mData.get(postion).workInfoMap);
                    b.putBoolean(Intent_Edit, canEdit);
                    intent.putExtras(b);
                    fragment.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    /**
     *  跳转到CheckForm监督抽查单
     */
    private void toCheckForm(Context context, int postion){
        Intent intent = new Intent(context, CheckOrRecheckFormActivity.class);
        mData.get(postion);
        String id = mData.get(postion).getId();//样品id
        if(TextUtils.isEmpty(id)){
            Toast.makeText(mContext,"样品ID获取出错！",Toast.LENGTH_LONG).show();
            return;
        }
        Bundle b = new Bundle();
//        b.putInt(Intent_Order, 1);
        b.putInt("task", postion);
        b.putSerializable("map", mData.get(postion).samplingInfoMap);
        b.putBoolean(Intent_Edit, canEdit);
        b.putString("id",id);
        TaskEntity taskEntity = taskDetailViewModel.taskEntity;
        String taskId = taskEntity.id;
        b.putString("taskId",taskId);
        b.putString("plantype",taskEntity.plantype);//任务类型
        b.putString("planid",taskEntity.planid);//计划id
        intent.putExtras(b);
        fragment.startActivityForResult(intent, TaskSampleRecyclerViewAdapter.RequestCodePdf);
    }

    /**
     * 跳转到处置单
     * @param context
     * @param postion
     */
    private void toHandleForm(Context context, int postion){
        Intent intent = new Intent(context, HandleLandFormActivity.class);
        Bundle b = new Bundle();
        //跳转到处置单
        mData.get(postion);
        String sampleId = mData.get(postion).getId();//编号根据id得来
        TaskEntity taskEntity = taskDetailViewModel.taskEntity;
        String taskId = taskEntity.id;//任务id
        String tasktypecount = taskEntity.tasktypecount;//产品名称
        String companyname = taskEntity.companyname;//企业名称
        b.putString("taskId",taskId);
        b.putString("id",sampleId);//样品id
        b.putString("tasktypecount",tasktypecount);
        b.putString("companyname",companyname);
        intent.putExtras(b);
        context.startActivity(intent);
    }
}
