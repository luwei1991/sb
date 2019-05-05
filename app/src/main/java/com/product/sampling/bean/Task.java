package com.product.sampling.bean;

import com.google.gson.annotations.SerializedName;

public class Task {
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
}

