package com.product.sampling.ui;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.product.sampling.bean.SampleTask;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于管理本地的样品数据
 * Created by 陆伟 on 2020/6/30.
 * Copyright (c) 2020 . All rights reserved.
 */


public class LocalSampleManager {
    private static final String TAG = "LocalSampleManager";
    private static final String LOCAL_KEY = "local_sample_key";
    private static LocalSampleManager instance = new LocalSampleManager();

    private LocalSampleManager(){

    }

    public static LocalSampleManager getInstance(){
        return instance;
    }

    /**
     * 查询
     * @return
     */
    public List<SampleTask> query(){
        String listJson = MMKV.mmkvWithID(TAG).decodeString(LOCAL_KEY,"");
        if (TextUtils.isEmpty(listJson)) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<SampleTask>>() {
        }.getType();
        List<SampleTask> taskList = gson.fromJson(listJson, type);
        return taskList;
    }

    /**
     *
     * @return
     */
    public SampleTask query(String taskId){
        String listJson = MMKV.mmkvWithID(TAG).decodeString(LOCAL_KEY,"");
        if (TextUtils.isEmpty(listJson)) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<SampleTask>>() {
        }.getType();
        List<SampleTask> taskList = gson.fromJson(listJson, type);
        SampleTask localSampleTask = null;
        for(SampleTask sampleTask :taskList){
            if(taskId.equals(sampleTask.getTaskId())){
                localSampleTask = sampleTask;
                break;
            }
        }
        return localSampleTask;
    }
    /**
     * 增加
     * @param sampleTask
     */
    public void add(SampleTask sampleTask){
        List<SampleTask> tempList = query();
        if(tempList != null){
            tempList.add(sampleTask);
        }else{
            tempList = new ArrayList<>();
            tempList.add(sampleTask);
        }
        Gson gson = new Gson();
        String listJson = gson.toJson(tempList);
        MMKV.mmkvWithID(TAG).encode(LOCAL_KEY,listJson);
    }

    /**
     * 删除
     * @param taskId
     */
    public void remove(String taskId){
        List<SampleTask> tempList = query();
        if(tempList != null && tempList.size() > 0){
            for(int i= 0; i < tempList.size(); i++){
                if(taskId.equals(tempList.get(i).getTaskId())){
                    tempList.remove(i);
                    break;
                }
            }
            Gson gson = new Gson();
            String listJson = gson.toJson(tempList);
            MMKV.mmkvWithID(TAG).encode(LOCAL_KEY,listJson);
        }
    }

    /**
     * 删除其中的一个样品
     * @param sampleId
     */
    public void remove(String taskId,String sampleId){
        List<SampleTask> tempList = query();
        if(tempList != null && tempList.size() > 0){
            for(int i= 0; i < tempList.size(); i++){
                if(taskId.equals(tempList.get(i).getTaskId())){
                   List<String> sampleIdList = tempList.get(i).getSampleIdList();
                   if(sampleIdList != null && sampleIdList.size() > 0){
                       for(int j = 0; j < sampleIdList.size(); j++){
                           if(sampleId.equals(sampleIdList.get(j))){
                               sampleIdList.remove(j);
                               break;
                           }
                       }
                   }
                   break;
                }
            }
            Gson gson = new Gson();
            String listJson = gson.toJson(tempList);
            MMKV.mmkvWithID(TAG).encode(LOCAL_KEY,listJson);
        }
    }


    /**
     * 更新
     * @param sampleTask
     */
    public void update(SampleTask sampleTask){
        remove(sampleTask.getTaskId());
        add(sampleTask);
    }

}
