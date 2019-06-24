package com.product.sampling.bean;

public class TaskMessage {
    public final String message;

    public static TaskMessage getInstance(String message) {
        return new TaskMessage(message);
    }

    private TaskMessage(String message) {
        this.message = message;
    }
}
