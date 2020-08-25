package com.product.sampling.ui.form;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.product.sampling.Constants;
import com.product.sampling.R;
import com.product.sampling.db.DBManagerFactory;
import com.product.sampling.db.DbController;
import com.product.sampling.db.tables.HandleForm;
import com.product.sampling.db.tables.HandleFormDao;
import com.product.sampling.manager.GlideManager;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.form.bean.FormHandleBean;
import com.product.sampling.ui.form.bean.SignBean;
import com.product.sampling.ui.sign.SignActivity;
import com.product.sampling.ui.sign.SignActivityController;
import com.product.sampling.ui.widget.LWTextView;
import com.product.sampling.utils.DateUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.view.SingleClick;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.amap.api.maps.model.BitmapDescriptorFactory.getContext;

/**
 * Created by 陆伟 on 2020/2/13.
 * Copyright (c) 2020 . All rights reserved.
 */


public class HandleFormActivity extends BaseActivity<HandleFormController> implements View.OnClickListener {
    private static final String TAG = "HandleFormActivity";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int RESULT_CODE_PDF_SUCCESS = 0x02;
    public static final int IMAGE_REQUEST_CODE = 0x06;

    private static final int TIME_TYPE_LIM = 1;
    private static final int TIME_TYPE_DEFAULT= 0;

    private static final String PRODUCT = "1";
    private static final String SALE = "0";

    private static final String BY = "0";
    private static final String JB = "1";
    private TimePickerView timePicker;
    public QMUITipDialog pdfDialog;


    @BindView(R.id.lwtv_cydbh_in)
    LWTextView lwTvCydbhIn;//抽样单编号
    @BindView(R.id.lwtv_gs_in)
    LWTextView lwTvGsmcIn;//公司名称
    @BindView(R.id.lwtv_cydw_in)
    LWTextView lwTvCydwIn;//抽样单位名称
    @BindView(R.id.lwtv_year_in)
    LWTextView lwTvYearIn;//年
    @BindView(R.id.lwtv_month_in)
    LWTextView lwTvMonthIn;//月
    @BindView(R.id.lwtv_day_in)
    LWTextView lwTvDayIn;//日
    @BindView(R.id.lwtv_cpmc_in)
    LWTextView lwTvCpmcIn;//产品名称
    @BindView(R.id.lwtv_ggxh_in)
    LWTextView lwTvGgxhIn;//规格型号
    @BindView(R.id.ll_cyr_qz)
    LinearLayout llCyrqz;//抽样人签字
    @BindView(R.id.iv_cyr_qz_1)
    ImageView ivCyrqz_1;//抽样人签字
    @BindView(R.id.iv_cyr_qz_2)
    ImageView ivCyrqz_2;//抽样人签字
    @BindView(R.id.lwtv_cydw_rq)
    LWTextView lwTvCydwRq;//抽样单位日期
    @BindView(R.id.lwtv_sjdw_rq)
    LWTextView lwTvSjdwRq;//受检单位日期
    @BindView(R.id.line_cydbh)
    View line_cydbh;
    @BindView(R.id.ll_back)
    LinearLayout llBack;//返回
    @BindView(R.id.btn_more)
    QMUIRoundButton btnMore;//生成pdf
    @BindView(R.id.rb_produce)
    CheckBox cbProduct;
    @BindView(R.id.rb_xiao_shou)
    CheckBox cbSale;
    @BindView(R.id.rb_byyp)
    CheckBox cbByyp;
    @BindView(R.id.rb_jbyp)
    CheckBox cbJbyp;
    @BindView(R.id.iv_sjdw_qz)
    ImageView ivSjdwQz;

    private HandleFormController handleFormController;
    private static String SJDW_YEAR = "";
    private static String SJDW_MONTH = "";
    private static String SJDW_DAY = "";

    /**
     * 多选
     */
    private QMUIPopup mNormalPopup;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_form);
        ButterKnife.bind(this);
        initController();
        initView();
        initData();

    }



    private void initController(){
        handleFormController = new HandleFormController();
        setUIController(handleFormController);
        handleFormController.setUI(this);
    }

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    private void initView(){
        lwTvCydbhIn.setOnClickListener(this);
        lwTvGsmcIn.setOnClickListener(this);
        lwTvGsmcIn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        lwTvGsmcIn.getPaint().setAntiAlias(true);//抗锯
        lwTvCydwIn.setOnClickListener(this);
        lwTvYearIn.setOnClickListener(this);
        lwTvMonthIn.setOnClickListener(this);
        lwTvDayIn.setOnClickListener(this);
        lwTvCpmcIn.setOnClickListener(this);
        lwTvCpmcIn.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        lwTvGgxhIn.setOnClickListener(this);
        lwTvCydwRq.setOnClickListener(this);
        lwTvSjdwRq.setOnClickListener(this);
        llBack.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        llCyrqz.setOnClickListener(this);
        ivSjdwQz.setOnClickListener(this);


        cbProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    upDataBase(R.id.rb_produce,"");
                    cbSale.setChecked(false);
                }

            }
        });

        cbSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    upDataBase(R.id.rb_xiao_shou,"");
                    cbProduct.setChecked(false);
                }
            }
        });

        cbByyp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    upDataBase(R.id.rb_byyp,"");
                    cbJbyp.setChecked(false);
                }
            }
        });

        cbJbyp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    upDataBase(R.id.rb_jbyp,"");
                    cbByyp.setChecked(false);
                }
            }
        });

    }

    private void initData(){
        verifyStoragePermissions(HandleFormActivity.this);

        //加载数据   先从本地加载数据-------
        HandleForm handleForm = handleFormController.getHandleFormToDB(handleFormController.getTaskSampleId());
        if(handleForm != null){
            setData(handleForm);
        }

    }

    private void setData(HandleForm handleForm){
        lwTvGsmcIn.setText(checkNullString(handleForm.getSjdwmc()));
        lwTvCydwIn.setText(checkNullString(handleForm.getCydwmc()));
        lwTvCpmcIn.setText(checkNullString(handleForm.getCpmc()));
        lwTvCydbhIn.setText(checkNullString(handleForm.getCydCode()));
        lwTvYearIn.setText(checkNullString(handleForm.getYear()));
        lwTvMonthIn.setText(checkNullString(handleForm.getMonth()));
        lwTvDayIn.setText(checkNullString(handleForm.getDay()));
        lwTvGgxhIn.setText(checkNullString(handleForm.getGgxh()));
        //生产或者销售
        String typeS = handleForm.getSaleOrProductType();
        if(!TextUtils.isEmpty(typeS)){
            if(typeS.equals(PRODUCT)){
                cbProduct.setChecked(true);
            }else if(typeS.equals(SALE)){
                cbSale.setChecked(true);
            }
        }

        //备用样品
        String typeB = handleForm.getProState();
        if(!TextUtils.isEmpty(typeB)){
            if(typeB.equals(BY)){
                cbByyp.setChecked(true);
            }else if(typeB.equals(JB)){
                cbJbyp.setChecked(true);
            }
        }


//        lwTvCydwRq.setText(checkNullString(handleForm.getCydwDate()));
//        lwTvSjdwRq.setText(checkNullString(handleForm.getSjdwDate()));
        String imagePath_1 = handleForm.getCyrImg_1();
        if(!TextUtils.isEmpty(imagePath_1)){
            GlideManager.getInstance().ImageLoad(this,imagePath_1,ivCyrqz_1,true);
        }

        String imagePath_2 = handleForm.getCyrImg_2();
        if(!TextUtils.isEmpty(imagePath_2)){
            GlideManager.getInstance().ImageLoad(this,imagePath_2,ivCyrqz_2,true);
        }


        String imageSJ = handleForm.getSjdwImg();
        if(!TextUtils.isEmpty(imageSJ)){
            GlideManager.getInstance().ImageLoad(this,imageSJ,ivSjdwQz,true);
        }



    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setUIController(HandleFormController sc) {
        handleFormController = sc;
    }



    /**
     * 编辑
     */
    private void showEditTextDialog(TextView textView, String string){
        String deafultInputText = textView.getText().toString();
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("信息录入")
                .setPlaceholder(string)
                .setDefaultText(deafultInputText)
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            textView.setText(text);
                            upDataBase(textView.getId(),text.toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(HandleFormActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void upDataBase(int viewId,String text){
        HandleForm handleForm =  DbController.getInstance(HandleFormActivity.this).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(handleFormController.getTaskSampleId())).build().unique();
        if(handleForm == null){return;}
        switch (viewId){
            case R.id.lwtv_cydbh_in://抽样单编号
                handleForm.setCydCode(text);
                break;
            case R.id.lwtv_gs_in:
                handleForm.setSjdwmc(text);
                break;

            case R.id.lwtv_cydw_in:
                handleForm.setCydwmc(text);
                break;

            case R.id.lwtv_year_in:
                handleForm.setYear(text);
                break;

            case R.id.lwtv_month_in:
                handleForm.setMonth(text);
                break;
            case R.id.lwtv_day_in:
                handleForm.setDay(text);
                break;
            case R.id.lwtv_cpmc_in:
                handleForm.setCpmc(text);
                break;
            case R.id.lwtv_ggxh_in:
                handleForm.setGgxh(text);
                break;

            case R.id.rb_produce:
                handleForm.setSaleOrProductType(PRODUCT);
                break;

            case R.id.rb_xiao_shou:
                handleForm.setSaleOrProductType(SALE);
                break;

            case R.id.rb_byyp:
                handleForm.setProState(BY);
                break;

            case R.id.rb_jbyp:
                handleForm.setProState(JB);
                break;


        }
        DbController.getInstance(this).getDaoSession().getHandleFormDao().update(handleForm);
    }

    private void showTimePicker(int viewId){
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2015,0,1);
        endDate.set(2029,11,31);
        timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if(DateUtils.isCurTime(date)){
                    setViewDate(viewId,DateUtils.getSelTime(date));
                }else{
                    new QMUIDialog.MessageDialogBuilder(HandleFormActivity.this)
                            .setTitle("日期提醒")
                            .setMessage("您选择的日期不是今天！")
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                    setViewDate(viewId,DateUtils.getSelTime(date));
                                }
                            })
                            .create().show();

                }

            }
        }).setType(new boolean[]{true, true, true, true, true, true})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(20)//标题文字大小
                .setTitleText("日期选择")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年","月","日",null,null,null)//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();

        timePicker.show();
    }

    private void setViewDate(int viewId,String dateString){
        HandleForm handleForm =  DbController.getInstance(HandleFormActivity.this).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(handleFormController.getTaskSampleId())).build().unique();
        String dates[] = dateString.split("-");
        String timeStr = dates[0] + " 年" + dates[1] + " 月" + dates[2] + " 日";
        switch (viewId){
            case R.id.lwtv_sjdw_rq:
                handleForm.setSjdwDate(timeStr);
                SJDW_YEAR = dates[0];
                SJDW_MONTH = dates[1];
                SJDW_DAY = dates[2];
                lwTvSjdwRq.setText(timeStr);
                break;
            case R.id.lwtv_cydw_rq:
                lwTvCydwRq.setText(timeStr);
                lwTvYearIn.setText(dates[0]);
                lwTvMonthIn.setText(dates[1]);
                lwTvDayIn.setText(dates[2]);
                handleForm.setYear(dates[0]);
                handleForm.setMonth(dates[1]);
                handleForm.setDay(dates[2]);
                handleForm.setCydwDate(timeStr);
                break;
        }

        DbController.getInstance(HandleFormActivity.this).getDaoSession().getHandleFormDao().update(handleForm);
    }

    @SingleClick(500)
    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lwtv_cydbh_in://抽样单编号

              showEditTextDialog(lwTvCydbhIn,"抽样单编号");
                break;
            case R.id.lwtv_gs_in:
                showEditTextDialog(lwTvGsmcIn,"受检单位名称");
                break;

            case R.id.lwtv_cydw_in:

                initCheckProduceCYDY(lwTvCydwIn);
//                showEditTextDialog(lwTvCydwIn,"抽样单位名称");
                break;

            case R.id.lwtv_year_in:
                showEditTextDialog(lwTvYearIn,"年");
                break;

            case R.id.lwtv_month_in:
                showEditTextDialog(lwTvMonthIn,"月");
                break;
            case R.id.lwtv_day_in:
                showEditTextDialog(lwTvDayIn,"日");
                break;
            case R.id.lwtv_cpmc_in:
                showEditTextDialog(lwTvCpmcIn,"产品名称");
                break;
            case R.id.lwtv_ggxh_in:
                showEditTextDialog(lwTvGgxhIn,"规格型号");
                break;
            case R.id.lwtv_cydw_rq:
                showTimePicker(R.id.lwtv_cydw_rq);
                break;
            case R.id.lwtv_sjdw_rq:
                showTimePicker(R.id.lwtv_sjdw_rq);
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_more:
                pdfDialog = new QMUITipDialog.Builder(HandleFormActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord("PDF生成中...")
                        .create(false);
                pdfDialog.show();
//                formToPDF();
                formToService();
                break;
            case R.id.ll_cyr_qz:
                SignBean signBeanCY = new SignBean();
                signBeanCY.setImgType(SignActivityController.IMAGE_TYPE_HANDLE_CYR);
                signBeanCY.setId(handleFormController.getTaskSampleId());
                toSign(signBeanCY);
                break;

            case R.id.iv_sjdw_qz:
                SignBean signBeanSJ = new SignBean();
                signBeanSJ.setImgType(SignActivityController.IMAGE_TYPE_HANDLE_SJDW);
                signBeanSJ.setId(handleFormController.getTaskSampleId());
                toSign(signBeanSJ);
                break;
        }
    }

    /**
     * 去手写签名
     */
    private void toSign(SignBean signBean){
        Intent intent = new Intent(this, SignActivity.class);
        intent.putExtra(SignActivityController.SIGN_TAG,signBean);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    private void initCheckProduceCYDY(TextView textView){
        String [] listItems = new String[]{
                "湖南省市场监督管理局",
                "国家市场监督管理局",
                "其他"
        };

        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 2){//不是点击其他
                    textView.setText(listItems[i]);
                    upDataBase(textView.getId(),listItems[i]);
                }else{
                    showEditTextDialog(textView,"抽样单位");
                }
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };

        mNormalPopup =  QMUIPopups.listPopup(getContext(),
                QMUIDisplayHelper.dp2px(getContext(), 250),
                QMUIDisplayHelper.dp2px(getContext(), 500),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(getContext(), 5))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(textView);
//        QMUIListPopup mListPopup = new QMUIListPopup(this, QMUIPopup.DIRECTION_NONE, adapter);
//        mListPopup.create(QMUIDisplayHelper.dp2px(this, 250), QMUIDisplayHelper.dp2px(this, 500), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i != 2){//不是点击其他
//                    textView.setText(listItems[i]);
//                    upDataBase(textView.getId(),listItems[i]);
//                }else{
//                    showEditTextDialog(textView,"抽样单位");
//                }
//                mListPopup.dismiss();
//            }
//        });
//        mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//
//            }
//        });
//        mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
//        mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
//        mListPopup.show(textView);

    }

    /**
     * 生产pdf
     */
    private void formToPDF(){
        //创建pdf存储位置
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        String dateString = formatter.format(currentTime);
        String path = Constants.PDF_PATH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        Flowable.timer(600, TimeUnit.MILLISECONDS)
                .compose(RxSchedulersHelper.f_io_main())
                .subscribe(c -> saveToPDF(dateString), e -> {
                });
    }

    /**
     * 将页面截图并转换成PDF文件，并创建xxxx-xx-xx-tasksampleid文件为名
     */
    private void saveToPDF(String dateString){
        Observable.just(dateString)
                .observeOn(Schedulers.computation())
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {

                        //创建截图
                        int height = QMUIDisplayHelper.getScreenHeight(HandleFormActivity.this);
                        int statusBarHeight = QMUIDisplayHelper.getStatusBarHeight(HandleFormActivity.this);
                        int width = QMUIDisplayHelper.getScreenWidth(HandleFormActivity.this);
                        View view = getWindow().getDecorView();
                        view.setDrawingCacheEnabled(true);
                        view.buildDrawingCache();
                        Bitmap activityBitmap = view.getDrawingCache();
                        Rect rect = new Rect();
                        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                        Bitmap nowBitmap = Bitmap.createBitmap(activityBitmap,0,statusBarHeight,width,height-statusBarHeight*2-10);
//        try {
//            FileOutputStream fout = new FileOutputStream(Constants.PDF_PATH +"/" + dateString +"-" + checkOrRecheckFormController.getTaskSampleId()+"xxxx.png");
//            nowBitmap.compress(Bitmap.CompressFormat.PNG,100,fout);
//            fout.flush();
//            fout.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
                        view.setDrawingCacheEnabled(false);
                        PdfDocument doc = new PdfDocument();
                        int pageWidth = PrintAttributes.MediaSize.ISO_A4.getWidthMils() * 72 / 1000;
//        int pageWidth = PrintAttributes.MediaSize.ISO_A4.getWidthMils();
//        int pageHeight = PrintAttributes.MediaSize.ISO_A4.getHeightMils();
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        Matrix matrix = new Matrix();
                        float scale = (float) pageWidth / (float) nowBitmap.getWidth();
                        int pageHeight = (int) (nowBitmap.getHeight() * scale);

                        matrix.postScale(scale, scale);
                        PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,pageHeight,1).create();
                        PdfDocument.Page page = doc.startPage(newPage);
                        Canvas canvas = page.getCanvas();

                        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));                        canvas.drawBitmap(nowBitmap,matrix,paint);
                        doc.finishPage(page);
                        HandleForm handleForm =  DbController.getInstance(HandleFormActivity.this).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(handleFormController.getTaskSampleId())).build().unique();
                        String name = dateString +"-" + "CZD-"+ handleFormController.getTaskId()+".pdf";
                        //将地址插入数据库
                        handleForm.setPdfPath(Constants.PDF_PATH + "/" + name);
                        File pdfFile = new File(Constants.PDF_PATH,name);
                        FileOutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(pdfFile);
                            doc.writeTo(outputStream);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            doc.close();
                            try {
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        DbController.getInstance(HandleFormActivity.this).getDaoSession().getHandleFormDao().update(handleForm);
                        return pdfFile;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(File file) {

                    }


                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                            if(pdfDialog != null && pdfDialog.isShowing()){
                                pdfDialog.dismiss();
                            }
                            showPDFSuccess();
                            HandleForm handleForm =  DbController.getInstance(HandleFormActivity.this).getDaoSession().getHandleFormDao().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(handleFormController.getTaskSampleId())).build().unique();
                            shareBySystem(handleForm.getPdfPath());
                            setResult(CheckOrRecheckFormActivity.RESULT_CODE_PDF_SUCCESS);


                    }
                });

    }
    public void showPDFSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("PDF生成成功！")
                .create();
        sucDialog.show();


        btnMore.postDelayed(new Runnable() {
            @Override
            public void run() {
                sucDialog.dismiss();
            }
        },1500);

    }
    public void showPDFFail(){
        Dialog failDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("PDF生成失败，请稍候再试！")
                .create();
        failDialog.show();


        btnMore.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }

    private void formToService(){
        HandleForm handleForm = DBManagerFactory.getInstance().getHandleManager().queryBuilder().where(HandleFormDao.Properties.SampleId.eq(handleFormController.getTaskSampleId())).build().unique();
        FormHandleBean handleBean = new FormHandleBean();
        handleBean.setCheckFormID(handleForm.getCydCode());
        handleBean.setAcceptComName(handleForm.getSjdwmc());
        handleBean.setSupComName(handleForm.getCydwmc());
        handleBean.setYear(handleForm.getYear());
        handleBean.setMonth(handleForm.getMonth());
        handleBean.setDay(handleForm.getDay());
        handleBean.setSaleOrProductType(handleForm.getProState());
        handleBean.setCheckPersonYear(handleForm.getYear());
        handleBean.setCheckPersonMonth(handleForm.getMonth());
        handleBean.setCheckPersonDay(handleForm.getDay());
        List<String> imgList = new ArrayList<>();
        imgList.add(handleForm.getCyrImg_1());
        imgList.add(handleForm.getCyrImg_2());
        handleBean.setCheckPersonImage(imgList);
        handleBean.setProName(handleForm.getCpmc());
        handleBean.setProModel(handleForm.getGgxh());
        handleBean.setProState(handleForm.getProState());

        handleBean.setAcceptMonth(SJDW_MONTH);
        handleBean.setAcceptYear(SJDW_YEAR);
        handleBean.setAcceptDay(SJDW_DAY);
        handleBean.setAcceptImage(handleForm.getSjdwImg());
        handleFormController.requestSubmit(handleBean);
    }

    private String checkNullString(String s){
        if(TextUtils.isEmpty(s)) {
            return "";
        }else {
            return s;
        }
    }


    public void shareBySystem(String path) {
        File doc = new File(path);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(doc));
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(this, "com.product.sampling.fileprovider", doc);
        share.setDataAndType(contentUri, "application/pdf");
        startActivity(Intent.createChooser(share, "分享文件"));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == SignActivityController.IMAGE_SUCCESS_RESULT_CODE){
            Bundle bundle = data.getExtras(); //读取intent的数据给bundle对象
            String imagePath = bundle.getString(SignActivityController.RESULT_IMAGE_PATH);
            String imageType = bundle.getString(SignActivityController.RESULT_IMAGE_TYPE);
            switch (imageType){
                case SignActivityController.IMAGE_TYPE_HANDLE_CYR:
                    HandleForm handleForm = handleFormController.getHandleFormToDB(handleFormController.getTaskSampleId());
                    String imagePath_1 = handleForm.getCyrImg_1();
                    if(!TextUtils.isEmpty(imagePath_1)){
                        GlideManager.getInstance().ImageLoad(this,imagePath_1,ivCyrqz_1,true);
                    }

                    String imagePath_2 = handleForm.getCyrImg_2();
                    if(!TextUtils.isEmpty(imagePath_2)){
                        GlideManager.getInstance().ImageLoad(this,imagePath_2,ivCyrqz_2,true);
                    }
                    break;

                case  SignActivityController.IMAGE_TYPE_HANDLE_SJDW:
                    GlideManager.getInstance().ImageLoad(this,imagePath,ivSjdwQz,true);
                    break;
            }

        }
    }
}
