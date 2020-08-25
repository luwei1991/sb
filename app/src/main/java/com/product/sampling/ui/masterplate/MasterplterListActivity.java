package com.product.sampling.ui.masterplate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.product.sampling.R;
import com.product.sampling.ui.base.BaseActivity;
import com.product.sampling.ui.masterplate.adapter.MASimpleAdapter;
import com.product.sampling.ui.masterplate.bean.MasterpleListBean;
import com.product.sampling.ui.widget.ListTipItem;
import com.product.sampling.ui.widget.ListTipView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIAnimationListView;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.product.sampling.ui.masterplate.MasterplterMainActivity.REPORT_TYPE;

/**
 * Created by 陆伟 on 2019/11/21.
 * Copyright (c) 2019 . All rights reserved.
 */


public class MasterplterListActivity extends BaseActivity<MasterplterListController> {
    private static final String TAG = "MasterplterListActivity";
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.listview)
    QMUIAnimationListView mListView;
    @BindView(R.id.empty_view)
    QMUIEmptyView emptyView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    MasterplterListController masterplterListController;
    List<MasterpleListBean> mListBeans = new ArrayList<>();
    MyAdapter maSimpleAdapter;
    Toolbar toolbar;
    LinearLayout llBack;
    public static final String MOUDLE_ID = "moudle_id";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_masterplate_list, null);
        setContentView(root);
//        setContentView(R.layout.activity_masterplate_list);
        ButterKnife.bind(this);
        findToolBar();
        initController();
    }
    private void initController(){
        masterplterListController = new MasterplterListController();
        setUIController(masterplterListController);
        masterplterListController.setUI(this);
    }

    @Override
    public void setUIController(MasterplterListController sc) {
        masterplterListController = sc;

    }

    /**
     * 初始化listView
     * @param listBeans
     */
    public void initListView(List<MasterpleListBean> listBeans) {
        mListBeans = listBeans;
        maSimpleAdapter = new MyAdapter(this,mListBeans);
        mListView.setAdapter(maSimpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (masterplterListController.getType()){
                    case MasterplterMainActivity.REPORT_TYPE_SAMPLIG:
                        toMasterpleterEdit(MasterplterMainActivity.REPORT_TYPE_SAMPLIG,mListBeans.get(position).getId());
                        break;
                    case MasterplterMainActivity.REPORT_TYPE_UNFIND:
//                        masterplterListActivity.tvTitle.setText("未抽到样模版");
                        break;
                    case MasterplterMainActivity.REPORT_TYPE_REFUSE:
//                        masterplterListActivity.tvTitle.setText("拒检单模版");
                        break;
                }

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                float OldListY = (float) location[1];
                float OldListX = (float) location[0];
                new ListTipView.Builder(
                        MasterplterListActivity.this,rootView,(int) OldListX + view.getWidth() / 2, (int) OldListY+ QMUIDisplayHelper.getStatusBarHeight(MasterplterListActivity.this))
                        .addItem(new ListTipItem("删除"))
                        .addItem(new ListTipItem("修改"))
                        .setOnItemClickListener(new ListTipView.OnItemClickListener() {
                            @Override
                            public void onItemClick(String name, int pos) {
                                if(pos == 0){
                                    new QMUIDialog.MessageDialogBuilder(MasterplterListActivity.this)
                                            .setTitle("删除模板")
                                            .setMessage("确定要删除模板吗？！")
                                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                                @Override
                                                public void onClick(QMUIDialog dialog, int index) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                                @Override
                                                public void onClick(QMUIDialog dialog, int index) {
                                                    masterplterListController.deleteMasterplate(mListBeans.get(position).getId(),position);
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();

                                }else if(pos == 1){
                                    QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(MasterplterListActivity.this);
                                             builder.setTitle("信息录入").
                                                     setPlaceholder("请输入模版名称")
                                                     .setDefaultText(mListBeans.get(position).getMouldtitle())
                                                     .setInputType(InputType.TYPE_CLASS_TEXT).
                                                     addAction("取消", new QMUIDialogAction.ActionListener() {
                                         @Override
                                         public void onClick(QMUIDialog dialog, int index) {
                                                dialog.dismiss();
                                        }
                             })
                         .addAction("确定", new QMUIDialogAction.ActionListener() {
                             @Override
                             public void onClick(QMUIDialog dialog, int index) {
                             CharSequence text = builder.getEditText().getText();
                              if (text != null && text.length() > 0) {
                              masterplterListController.updateMasterplate(masterplterListController.getType(),text.toString(),listBeans.get(position).getId());

                              dialog.dismiss();
                              } else {
                            Toast.makeText(MasterplterListActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                     })
                        .show();
                                }
                            }

                            @Override
                            public void dismiss() {

                            }
                        })
                        .create();


                return true;
            }
        });
    }
    public void findToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            llBack = findViewById(R.id.ll_back);
            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     * 添加
     */
    @OnClick(R.id.tv_add)
    public void addItem(){
        QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("信息录入")
                .setPlaceholder("请输入模版名称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            masterplterListController.addMasterplate(masterplterListController.getType(),text.toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MasterplterListActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();

    }

    public void updateItemToListView(MasterpleListBean masterpleListBean){
        for(MasterpleListBean bean:mListBeans){
            if(bean.getId().equals(masterpleListBean.getId())){
                bean.setMouldtitle(masterpleListBean.getMouldtitle());
            }
        }
        maSimpleAdapter.notifyDataSetChanged();
        showUpdateSuccess();

    }

    public void addItemToListView(MasterpleListBean masterpleListBean){
        mListView.manipulate(new QMUIAnimationListView.Manipulator<MyAdapter>() {
            @Override
            public void manipulate(MyAdapter adapter) {
                int position = mListView.getFirstVisiblePosition();
                mListBeans.add(position,masterpleListBean);
                showAddSuccess();
            }
        });
    }

    /**
     * 删除
     */
    public void removeItemFromListView(int position){
        mListView.manipulate(new QMUIAnimationListView.Manipulator<MyAdapter>() {
            @Override
            public void manipulate(MyAdapter adapter) {
                mListBeans.remove(position);
                showDeleteSuccess();
            }
        });

    }

    private void toMasterpleterEdit(String type,String id){
        Intent intent = new Intent(this, MasterplterEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(REPORT_TYPE,type);
        bundle.putString(MOUDLE_ID,id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showAddSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("添加成功")
                .create();
        sucDialog.show();


        tvAdd.postDelayed(new Runnable() {
            @Override
            public void run() {
                sucDialog.dismiss();
            }
        },1500);

    }
    public void showAddFail(){
        Dialog failDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("提交失败")
                .create();
        failDialog.show();


        tvAdd.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }


    public void showDeleteSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("删除成功")
                .create();
        sucDialog.show();


        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                sucDialog.dismiss();
            }
        },1500);

    }
    public void showDeleteFail(){
        Dialog failDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("删除失败")
                .create();
        failDialog.show();


        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }


    public void showUpdateSuccess(){
        Dialog sucDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord("修改成功")
                .create();
        sucDialog.show();


        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                sucDialog.dismiss();
            }
        },1500);

    }
    public void showUpdateFail(){
        Dialog failDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("更新失败")
                .create();
        failDialog.show();


        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                failDialog.dismiss();
            }
        },1500);

    }





    private static class MyAdapter extends MASimpleAdapter {
        public MyAdapter(Context context, List<MasterpleListBean> data) {
            super(context, data);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

}
