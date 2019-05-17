package com.product.sampling.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.product.sampling.R;
import com.product.sampling.adapter.ImageAndTextRecyclerViewAdapter;
import com.product.sampling.bean.Task;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.bean.TaskImageEntity;
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
        return rootView;
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
