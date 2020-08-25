package com.product.sampling.adapter;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.product.sampling.bean.TaskBean;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.httpmoudle.manager.RetrofitServiceManager;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.SPUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

//定义MyDataSource类，继承自DataSource三个子类之一
public class MyDataSource extends PageKeyedDataSource<Integer, TaskEntity> {

    private static final String TAG = MyDataSource.class.getSimpleName();

    private String status;
    private String ordertype;
    private String cityid;

    int pageNo = 1;
    Context context;

    public MyDataSource(Context context, int pageNo, String status, String ordertype, String cityid) {
        this.context = context;
        this.status = status;
        this.ordertype = ordertype;
        this.cityid = cityid;
        this.pageNo = pageNo;

    }

    //DataSource执行build()的时候调用
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, TaskEntity> callback) {
        //加载第一页
        if (TextUtils.isEmpty(status)) {
            return;
        }
        getData(context, status, ordertype, cityid, pageNo, callback, null, null);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, TaskEntity> callback) {

        //加载之前
        LogUtils.d(TAG, "loadBefore");

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, TaskEntity> callback) {
        if (TextUtils.isEmpty(status)) {
            return;
        }
        getData(context, status, ordertype, cityid, pageNo, null, params, callback);
    }

    private void getData(Context context, String status, String ordertype, String cityid, int pageNo, LoadInitialCallback loadInitialCallback, LoadParams<Integer> params, LoadCallback loadCallback) {

        RetrofitServiceManager.getInstance().createApiService(Request.class)
                .taskList(AccountManager.getInstance().getUserId(), status, ordertype, cityid, pageNo,"","","")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<TaskBean>() {

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                    }

                    @Override
                    public void onSuccess(TaskBean taskBean) {
                        List<TaskEntity> tasks = taskBean.getList();
                        List<TaskEntity> localData = findTaskInLocalFile(context);
                        if (null != localData && !localData.isEmpty()) {
                            for (TaskEntity taskEntity : localData) {
                                for (int i = 0; i < tasks.size(); i++) {
                                    if (tasks.get(i).id.equals(taskEntity.id)) {
                                        tasks.remove(i);
                                        break;
                                    }
                                }
                            }
                        }
                        taskBean.setList(tasks);
                        setPage(taskBean, loadInitialCallback, params, loadCallback);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });
    }

    private void setPage(TaskBean data, LoadInitialCallback loadInitialCallback, LoadParams<Integer> params, LoadCallback loadCallback) {
        if (null != loadInitialCallback) {
            List<TaskEntity> firstPage = data.getList();
            int pageNum = data.getPageNo();
            int pageSize = data.getPageSize();
            int pageTotal = data.getCount();
            pageNo = pageNum;
            Integer prevPageNum = pageNum;
            Integer nextPageNum = pageNum;
            if (pageNum <= 1) {
                prevPageNum = null;
            } else {
                prevPageNum = pageNum - 1;
            }
            if (pageTotal <= 1) {
                nextPageNum = null;
            } else {
                nextPageNum = pageNum + 1;
            }

            loadInitialCallback.onResult(firstPage, prevPageNum, nextPageNum);
        } else if (null != loadCallback) {
            Integer nextPage = params.key;
            if (params.key < pageNo) {
                nextPage = nextPage + 1;
            } else {
                nextPage = null;
            }
            loadCallback.onResult(data.getList(), nextPage);
        }


    }

    List<TaskEntity> findTaskInLocalFile(Context context) {

        try {
            String taskListStr = (String) SPUtil.get(context, "tasklist", "");
            if (!TextUtils.isEmpty(taskListStr)) {
                Type listType = new TypeToken<List<TaskEntity>>() {
                }.getType();
                Gson gson = new Gson();
                List<TaskEntity> listTask = gson.fromJson(taskListStr, listType);
                if (null == listTask || listTask.isEmpty()) return new ArrayList<>();
                for (TaskEntity taskEntity : listTask) {
                    taskEntity.isLoadLocalData = true;
                }
                return listTask;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}