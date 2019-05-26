package com.product.sampling.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonArray;
import com.product.sampling.R;
import com.product.sampling.adapter.ImageAndTextRecyclerViewAdapter;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.dummy.DummyContent;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.LoadDataModel;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.ui.viewmodel.TaskDetailViewModel;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.ToastUtil;
import com.product.sampling.utils.ToastUtils;

import org.devio.takephoto.model.TImage;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class TaskSceneFragment extends BasePhotoFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    RecyclerView mRecyclerView;
    static TaskSceneFragment fragment;
    TaskDetailViewModel taskDetailViewModel;

    public TaskSceneFragment() {
    }

    public static TaskSceneFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putParcelable("task", task);
        if (fragment == null) {
            fragment = new TaskSceneFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scene_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView = rootView.findViewById(R.id.item_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        rootView.findViewById(R.id.iv_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto(10, false, true, false);
            }
        });
        rootView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postData();
            }
        });

        return rootView;
    }

    private void postData() {

        File file = new File("/storage/emulated/0/table.pdf");
        if (!file.exists()) {
            Log.e("file", file.getAbsolutePath());
            return;
        }
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);//把文件与类型放入请求体
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("id", taskDetailViewModel.taskEntity.id)
                .addFormDataPart("taskisok", "0")
                .addFormDataPart("samplecount", "1")
                .addFormDataPart("feed.companyname", "1");


        MultipartBody.Part part = MultipartBody.Part
                .createFormData("userid", AccountManager.getInstance().getUserId())
                .createFormData("id", taskDetailViewModel.taskEntity.id)
                .createFormData("taskisok", "0")
                .createFormData("samplecount", "1")
                .createFormData("feed.companyname", "1")
                .createFormData("feedfile", file.getName(), requestFile);

        Map<String, MultipartBody> array = new HashMap<>();
        Map<String, MultipartBody> arrayStr = new HashMap<>();

        for (int i = 0; i < taskDetailViewModel.imageList.size(); i++) {
            TaskImageEntity taskImageEntity = taskDetailViewModel.imageList.get(i);
            File f = new File(taskImageEntity.getOriginalPath());
            if (!f.exists()) {
                continue;
            }

            RequestBody imageBody = RequestBody.create(MultipartBody.FORM, f);//把文件与类型放入请求体
//            requestBody.addFormDataPart("uploadpics", f.getName(), imageBody);//把文件与类型放入请求体)
//            requestBody.addFormDataPart("picstrs", "鞍山");//把文件与类型放入请求体)
            MultipartBody.Builder imgRequestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uploadpics", f.getName(), imageBody);

            array.put("uploadpics", imgRequestBody.build());

            MultipartBody.Builder strRequestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("picstrs", f.getName(), RequestBody.create(MultipartBody.FORM, "sss"));
            arrayStr.put("picstrs", strRequestBody.build());

        }

        RequestBody userid = RequestBody.create(null, AccountManager.getInstance().getUserId());
        RetrofitService.createApiService(Request.class)
                .uploadtaskinfo(userid, part)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), "添加成功" + s);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        com.product.sampling.maputil.ToastUtil.showShortToast(getActivity(), message);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }
                });


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
        recyclerView.setAdapter(new ImageAndTextRecyclerViewAdapter(getActivity(), task, false));
    }

    @Override
    public void showResultImages(ArrayList<TImage> images) {

        for (TImage image :
                images) {
            TaskImageEntity taskImageEntity = new TaskImageEntity();
            taskImageEntity.setCompressPath(image.getCompressPath());
            taskImageEntity.setOriginalPath(image.getOriginalPath());
            taskImageEntity.setFromType(image.getFromType());
            taskDetailViewModel.imageList.add(taskImageEntity);
        }
        setupRecyclerView(mRecyclerView, taskDetailViewModel.imageList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel = ViewModelProviders.of(getActivity()).get(TaskDetailViewModel.class);
        setupRecyclerView(mRecyclerView, taskDetailViewModel.imageList);
    }
}
