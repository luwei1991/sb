package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskEntity implements Parcelable, Serializable {
    public String id;//任务唯一id
    public String planid;//所属计划id
    public String companyid;//抽样企业id
    public String remark;//任务备注
    public String starttime;//任务开始时间
    public String endtime;//任务结束时间

    public String taskstatus;//任务状态 0待办1本地保存2上传3退回修改4已确认5停用
    public String doman;//抽样人

    public String tasktypecount;//抽样产品名
    public String taskCode;//任务编号
    public String planname;//所属计划名
    public String companyname;//企业名

    public String goodscount;//计划抽样批次总数
    public String companyaddress;//企业地址
    public String taskisok;//任务异常状态 0抽样正常1企业拒检2未检测到样品
    public String companyperson;//企业联系人
    public String companytel;//企业联系号码

    public String businesslicence;//企业营业执照
    public String planno;//计划备案号
    public String planfrom;//计划来源
    public String groupPersonCount;//任务每组人数

    public String areasheng;//企业所在省
    public String areashi;//企业所在市
    public String areaqu;//企业所在区/县
    public String taskAddress;//任务提交时的地址
    public String taskTime;//任务提交时间

    public String longitude;//任务提交时的经度
    public String latitude;//任务提交时的维度
    public int leftday;//任务剩余天数

    public List<Pics> pics = new ArrayList<Pics>();
    public List<Videos> voides = new ArrayList<Videos>();
    public boolean isNewRecord;

    public boolean isLoadLocalData;

    public TaskEntity() {

    }

    protected TaskEntity(Parcel in) {
        id = in.readString();
        planid = in.readString();
        companyid = in.readString();
        remark = in.readString();
        starttime = in.readString();
        endtime = in.readString();
        taskstatus = in.readString();
        doman = in.readString();
        tasktypecount = in.readString();
        taskCode = in.readString();
        planname = in.readString();
        companyname = in.readString();
        goodscount = in.readString();
        companyaddress = in.readString();
        taskisok = in.readString();
        companyperson = in.readString();
        companytel = in.readString();
        businesslicence = in.readString();
        planno = in.readString();
        planfrom = in.readString();
        groupPersonCount = in.readString();
        areasheng = in.readString();
        areashi = in.readString();
        areaqu = in.readString();
        taskAddress = in.readString();
        taskTime = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        leftday = in.readInt();

        isNewRecord = in.readByte() != 0;
        isLoadLocalData = in.readByte() != 0;

        in.readTypedList(pics, Pics.CREATOR);

        //        in.readTypedList(pics, Pics.CREATOR);
        in.readTypedList(voides, Videos.CREATOR);

    }

    public static final Creator<TaskEntity> CREATOR = new Creator<TaskEntity>() {
        @Override
        public TaskEntity createFromParcel(Parcel in) {
            return new TaskEntity(in);
        }

        @Override
        public TaskEntity[] newArray(int size) {
            return new TaskEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(planid);
        dest.writeString(companyid);
        dest.writeString(remark);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeString(taskstatus);
        dest.writeString(doman);
        dest.writeString(tasktypecount);
        dest.writeString(taskCode);
        dest.writeString(planname);
        dest.writeString(companyname);
        dest.writeString(goodscount);
        dest.writeString(companyaddress);
        dest.writeString(taskisok);
        dest.writeString(companyperson);
        dest.writeString(companytel);
        dest.writeString(businesslicence);
        dest.writeString(planno);
        dest.writeString(planfrom);
        dest.writeString(groupPersonCount);
        dest.writeString(areasheng);
        dest.writeString(areashi);
        dest.writeString(areaqu);
        dest.writeString(taskAddress);
        dest.writeString(taskTime);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeInt(leftday);
        dest.writeByte((byte) (isNewRecord ? 1 : 0));
        dest.writeByte((byte) (isLoadLocalData ? 1 : 0));
        dest.writeTypedList(pics);
        dest.writeTypedList(voides);

//        if (pics != null)
//            dest.writeParcelableArray(
//                    pics.toArray(new Pics[pics.size()]), flags);
    }

}

