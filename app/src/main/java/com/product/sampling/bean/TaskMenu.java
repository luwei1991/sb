package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TaskMenu implements Parcelable {

    String id;
    String value;
    String type;
    String label;



    protected TaskMenu(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskMenu> CREATOR = new Creator<TaskMenu>() {
        @Override
        public TaskMenu createFromParcel(Parcel in) {
            return new TaskMenu(in);
        }

        @Override
        public TaskMenu[] newArray(int size) {
            return new TaskMenu[size];
        }
    };
}

