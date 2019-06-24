package com.product.sampling.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.product.sampling.R;
import com.product.sampling.bean.TaskArea;
import com.product.sampling.bean.TaskCity;
import com.product.sampling.bean.TaskProvince;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SpinnerSimpleAdapter<T> extends BaseAdapter {
    private Context ctx;
    private List<T> dataList = Collections.emptyList();

    public SpinnerSimpleAdapter(Context ctx, ArrayList<T> dataList) {
        this.ctx = ctx;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.item_spinner_layout, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();// get convertView's holder
        Object obj = getItem(position);
        if (obj instanceof String) {
            String entity = (String) obj;
            holder.tv_title.setText(entity);
        } else if (obj instanceof TaskProvince) {
            TaskProvince entity = (TaskProvince) obj;
            holder.tv_title.setText(entity.name);
        }else if (obj instanceof TaskCity) {
            TaskCity entity = (TaskCity) obj;
            holder.tv_title.setText(entity.name);
        }else if (obj instanceof TaskArea) {
            TaskArea entity = (TaskArea) obj;
            holder.tv_title.setText(entity.name);
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv_title;

        public ViewHolder(View convertView) {
            tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(this);//set a viewholder
        }
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }
}