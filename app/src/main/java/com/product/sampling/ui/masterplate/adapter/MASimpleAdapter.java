package com.product.sampling.ui.masterplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.product.sampling.R;
import com.product.sampling.ui.masterplate.bean.MasterpleListBean;

import java.util.List;

/**
 * @author luwei
 * @date 2019-11-21
 */

public class MASimpleAdapter extends BaseAdapter {
    private Context mContext;
    private List<MasterpleListBean> mData;

    public MASimpleAdapter(Context context, List<MasterpleListBean> data) {
        mContext = context;
        mData = data;
    }

    public void setData(List<MasterpleListBean> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public MasterpleListBean getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview,null);
            viewHolder.mainTitle = convertView.findViewById(R.id.tv_main);
            viewHolder.subTitle = convertView.findViewById(R.id.tv_sub);
            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mainTitle.setText(getItem(position).getMouldtitle());
        viewHolder.subTitle.setText("创建日期: " + getItem(position).getCreateDate());
        return convertView;
    }

    public void remove(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    class ViewHolder{
        TextView mainTitle;
        TextView subTitle;

    }

}
