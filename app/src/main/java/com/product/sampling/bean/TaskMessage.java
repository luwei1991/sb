package com.product.sampling.bean;

public class TaskMessage {
    public final String message;
    public boolean isRemove;//用于是否执行删除list

    public static TaskMessage getInstance(String message,boolean isRemove) {
        return new TaskMessage(message,isRemove);
    }

    private TaskMessage(String message,boolean isRemove) {
        this.message = message;
        this.isRemove = isRemove;
    }
}
