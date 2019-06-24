package com.product.sampling.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TaskProvince {

    public String id;
    public String name;
    public ArrayList<TaskCity> shicitys;

    public TaskProvince() {
    }
}

