package com.product.sampling.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class TaskSampleFragment extends BasePhotoFragment {
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView = rootView.findViewById(R.id.item_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        List list = new ArrayList();
        TaskSample sample = new TaskSample();
        sample.title = "北京";
        sample.list = new ArrayList<>();
        list.add(sample);
        setupRecyclerView(mRecyclerView, list);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List task) {
        recyclerView.setAdapter(new TaskSampleRecyclerViewAdapter((AppCompatActivity) getActivity(), this, task, false));
    }

    @Override
    public void showResultImages(ArrayList<TImage> images) {
        ArrayList<TaskImageEntity> imageList = new ArrayList<>();
        for (TImage image :
                images) {
            TaskImageEntity taskImageEntity = new TaskImageEntity();
            taskImageEntity.setCompressPath(image.getCompressPath());
            taskImageEntity.setOriginalPath(image.getOriginalPath());
            taskImageEntity.setFromType(image.getFromType());
            imageList.add(taskImageEntity);
        }
        List<TaskSample> list = new ArrayList<>();
        TaskSample taskSample = new TaskSample();
        taskSample.title = "北京";
        taskSample.list = imageList;
        list.add(taskSample);
        setupRecyclerView(mRecyclerView, list);
    }
}
