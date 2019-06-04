package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.devio.takephoto.model.TImage;

import java.io.Serializable;

public class TaskImageEntity extends TImage implements Serializable {
    public String title = "212";
    public static final Creator<TaskImageEntity> CREATOR = new Creator<TaskImageEntity>() {
        @Override
        public TaskImageEntity createFromParcel(Parcel in) {
            return new TaskImageEntity(in);
        }

        @Override
        public TaskImageEntity[] newArray(int size) {
            return new TaskImageEntity[size];
        }
    };

    public TaskImageEntity() {

    }

    public TaskImageEntity(Parcel in) {
        title = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }
}

