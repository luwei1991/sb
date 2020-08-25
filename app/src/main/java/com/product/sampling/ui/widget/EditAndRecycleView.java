package com.product.sampling.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.product.sampling.R;
import com.product.sampling.bean.TaskCompany;
import com.product.sampling.ui.base.BaseRecyclerAdapter;
import com.product.sampling.ui.base.RecyclerViewHolder;
import com.product.sampling.ui.eventmessage.SelectedTaskCompanyMessage;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陆伟 on 2020/4/7.
 * Copyright (c) 2020 . All rights reserved.
 */


public class EditAndRecycleView extends QMUIDialog.AutoResizeDialogBuilder{
    private Context mContext;
    private EditText mEditText;
    private RecyclerView mRecycleView;
    private EditAndRecycleViewAdapter editAndRecycleViewAdapter;
    private List<TaskCompany> wholeList = new ArrayList<>();
    private String type;


    public EditAndRecycleView(Context context,String type) {
        super(context);
        mContext = context;
        this.type = type;

    }
    @Override
    public View onBuildContent(@NonNull QMUIDialog dialog, @NonNull Context context) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int padding = QMUIDisplayHelper.dp2px(mContext, 20);
        layout.setPadding(padding, padding, padding, padding);
        mEditText = new AppCompatEditText(mContext);
        QMUIViewHelper.setBackgroundKeepingPadding(mEditText, QMUIResHelper.getAttrDrawable(mContext, R.drawable.qmui_divider_bottom_bitmap));
        mEditText.setHint("输入框");
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshRecycleUI(searchByText(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        LinearLayout.LayoutParams editTextLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, QMUIDisplayHelper.dpToPx(50));
        editTextLP.bottomMargin = QMUIDisplayHelper.dp2px(mContext, 15);
        mEditText.setLayoutParams(editTextLP);
        layout.addView(mEditText);
        mRecycleView = new RecyclerView(mContext);
        setRecycleViewStyle(mRecycleView);
        layout.addView(mRecycleView);
        return layout;
    }


//    @Override
//    public View onBuildContent(QMUIDialog dialog, ScrollView parent) {
//
//    }

    /**
     * 设置recycleview的样式
     * @param recyclerView
     */
    private void setRecycleViewStyle(RecyclerView recyclerView){
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });

        recyclerView.addItemDecoration(getRecyclerViewDivider(R.drawable.item_recyle_view_diver));//设置分割线
    }
    /**
     * 获取分割线
     *
     * @param drawableId 分割线id
     * @return
     */
    public RecyclerView.ItemDecoration getRecyclerViewDivider(@DrawableRes int drawableId) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(mContext.getDrawable(drawableId));
        return itemDecoration;
    }


    public EditText getEditText() {
        return mEditText;
    }

    public RecyclerView getRecycleView() {
        return mRecycleView;
    }

    /**
     * 刷新adapter
     */
    public void refreshRecycleUI(List<TaskCompany> taskCompanyList) {
        if (editAndRecycleViewAdapter == null) {
            editAndRecycleViewAdapter = new EditAndRecycleViewAdapter(mContext, taskCompanyList);
            mRecycleView.setAdapter(editAndRecycleViewAdapter);
        } else {
            editAndRecycleViewAdapter.setData(taskCompanyList);
            editAndRecycleViewAdapter.notifyDataSetChanged();
        }
        editAndRecycleViewAdapter.setOnItermCLickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                EventBus.getDefault().post(new SelectedTaskCompanyMessage(taskCompanyList.get(pos),type));
                mDialog.dismiss();
            }
        });
    }

    public void setWholeList(List<TaskCompany> taskCompanyList){
        wholeList = taskCompanyList;
    }

    private List<TaskCompany> searchByText(String text){
        List<TaskCompany> containList = new ArrayList<>();
        if(wholeList != null && wholeList.size() > 0){
            for(TaskCompany taskCompany:wholeList){
                if(taskCompany.getItem().contains(text)){
                    containList.add(taskCompany);
                }
            }
        }
        return containList;
    }


    class EditAndRecycleViewAdapter extends BaseRecyclerAdapter<TaskCompany> {

        public EditAndRecycleViewAdapter(Context context, List<TaskCompany> data){
            super(context,data);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_edit_and_recycleview_list;
        }

        @Override
        public void bindData(RecyclerViewHolder holder, int position, TaskCompany taskCompany) {
            holder.getTextVIew(R.id.tv_do_stand_name).setText(taskCompany.getItem()+"/"+taskCompany.getValue());
        }
    }
}