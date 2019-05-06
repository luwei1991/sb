package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Task implements Parcelable {
    @SerializedName("task_id")
    public String task_id;
    @SerializedName("task_type")
    public String task_type;
    @SerializedName("comp_name")
    public String comp_name;
    @SerializedName("comp_addr")
    public String comp_addr;

    @SerializedName("pro_type")
    public String pro_type;

    @SerializedName("start_date")
    public String start_date;

    @SerializedName("end_date")
    public String end_date;

    @SerializedName("free_date")
    public String free_date;

    protected Task(Parcel in) {
        task_id = in.readString();
        task_type = in.readString();
        comp_name = in.readString();
        comp_addr = in.readString();
        pro_type = in.readString();
        start_date = in.readString();
        end_date = in.readString();
        free_date = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getTask_id() {
        return task_id;
    }

    public String getTask_type() {
        return task_type;
    }

    public String getComp_name() {
        return comp_name;
    }

    public String getComp_addr() {
        return comp_addr;
    }

    public String getPro_type() {
        return pro_type;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getFree_date() {
        return free_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(task_id);
        dest.writeString(task_type);
        dest.writeString(comp_name);
        dest.writeString(comp_addr);
        dest.writeString(pro_type);
        dest.writeString(start_date);
        dest.writeString(end_date);
        dest.writeString(free_date);
    }
}

