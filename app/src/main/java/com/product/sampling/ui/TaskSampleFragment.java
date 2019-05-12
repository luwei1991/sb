package com.product.sampling.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.product.sampling.R;
import com.product.sampling.adapter.TaskSampleRecyclerViewAdapter;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
import com.product.sampling.bean.TaskSample;
import com.product.sampling.dummy.DummyContent;
import com.product.sampling.photo.BasePhotoFragment;

import org.devio.takephoto.model.TImage;

import java.util.ArrayList;
import java.util.List;

/**
 * 样品信息
 */
public class TaskSampleFragment extends BasePhotoFragment implements View.OnClickListener {
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
    List<TaskSample> taskList = new ArrayList<>();

    public TaskSampleFragment() {
    }

    public static TaskSampleFragment newInstance(TaskEntity task) {

        Bundle args = new Bundle();
        args.putParcelable("task", task);
        TaskSampleFragment fragment = new TaskSampleFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_sample_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        }
        rootView.findViewById(R.id.tv_create).setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView = rootView.findViewById(R.id.item_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        TaskSample sample = new TaskSample();
        sample.title = "北京";
        sample.list = new ArrayList<>();
        taskList.add(sample);
        setupRecyclerView(mRecyclerView, taskList);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
        recyclerView.setAdapter(new TaskSampleRecyclerViewAdapter((AppCompatActivity) getActivity(), this, task, false));
    }

    @Override
    public void showResultImages(ArrayList<TImage> images, int pos) {
        ArrayList<TaskImageEntity> imageList = new ArrayList<>();
        for (TImage image :
                images) {
            TaskImageEntity taskImageEntity = new TaskImageEntity();
            taskImageEntity.setCompressPath(image.getCompressPath());
            taskImageEntity.setOriginalPath(image.getOriginalPath());
            taskImageEntity.setFromType(image.getFromType());
            imageList.add(taskImageEntity);
        }
        if (pos > -1 && taskList.size() > pos) {
            taskList.get(pos).list = imageList;
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_create) {
            EditText et = new EditText(getActivity());
            new AlertDialog.Builder(getActivity()).setTitle("请输入样品名称")
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //按下确定键后的事件
                            String text = et.getText().toString();
                            createNewSample(text);
                        }
                    }).setNegativeButton("取消", null).show();


        }
    }

    void createNewSample(String text) {
        if (null != text && !text.isEmpty()) {
            TaskSample sample = new TaskSample();
            sample.title = text;
            sample.list = new ArrayList<>();
            taskList.add(sample);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
