package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskSample implements Parcelable {
    public List<TaskImageEntity> list;
    public String title = "";
    public String handleSheet = "";
    public String checkSheet = "";

    public TaskSample() {

    }

    protected TaskSample(Parcel in) {
        title = in.readString();
        handleSheet = in.readString();
        checkSheet = in.readString();
    }

    public static final Creator<TaskSample> CREATOR = new Creator<TaskSample>() {
        @Override
        public TaskSample createFromParcel(Parcel in) {
            return new TaskSample(in);
        }

        @Override
        public TaskSample[] newArray(int size) {
            return new TaskSample[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(handleSheet);
        dest.writeString(checkSheet);
    }
}

