package com.product.sampling.ui;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.product.sampling.bean.TaskEntity;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陆伟 on 2020/6/30.
 * Copyright (c) 2020 . All rights reserved.
 */


public class LocalTaskListManager {
    private static final String TAG = "LocalTaskListManager";
    private static final String LOCAL_KEY = "local_key";
    private static LocalTaskListManager instance = new LocalTaskListManager();

    private LocalTaskListManager(){

    }

    public static LocalTaskListManager getInstance(){
        return instance;
    }

    /**
     * 查询
     * @return
     */
    public List<TaskEntity> query(){
        String listJson = MMKV.mmkvWithID(TAG).decodeString(LOCAL_KEY,"");
        if (TextUtils.isEmpty(listJson)) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<TaskEntity>>() {
        }.getType();
        List<TaskEntity> taskList = gson.fromJson(listJson, type);
        return taskList;
    }

    /**
     *
     * @return
     */
    public TaskEntity query(TaskEntity taskEntity){
        String listJson = MMKV.mmkvWithID(TAG).decodeString(LOCAL_KEY,"");
        if (TextUtils.isEmpty(listJson)) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<TaskEntity>>() {
        }.getType();
        List<TaskEntity> taskList = gson.fromJson(listJson, type);
        TaskEntity localEntity = null;
        for(TaskEntity listEntity :taskList){
            if(listEntity.id.equals(taskEntity.id)){
                localEntity = listEntity;
                break;
            }
        }
        return localEntity;
    }
    /**
     * 增加
     * @param taskEntity
     */
    public void add(TaskEntity taskEntity){
        List<TaskEntity> tempList = query();
        if(tempList != null){
            tempList.add(taskEntity);
        }else{
            tempList = new ArrayList<>();
            tempList.add(taskEntity);
        }
        Gson gson = new Gson();
        String listJson = gson.toJson(tempList);
        MMKV.mmkvWithID(TAG).encode(LOCAL_KEY,listJson);
    }

    /**
     * 删除
     * @param taskEntity
     */
    public void remove(TaskEntity taskEntity){
        List<TaskEntity> tempList = query();
        if(tempList != null && tempList.size() > 0){
            for(int i= 0; i < tempList.size(); i++){
                if(tempList.get(i).id.equals(taskEntity.id)){
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
     * 更新
     * @param taskEntity
     */
    public void update(TaskEntity taskEntity){
        remove(taskEntity);
        add(taskEntity);
    }

    /**
     * 清除所有缓存
     */
    public void clear(){
        MMKV.mmkvWithID(TAG).removeValueForKey(LOCAL_KEY);
    }

}
