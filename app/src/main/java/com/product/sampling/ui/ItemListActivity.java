package com.product.sampling.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.product.sampling.R;
import com.product.sampling.bean.ImageItem;
import com.product.sampling.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        List list = new ArrayList();
        for (int i = 0; i < 4; i++) {
            list.add(createItem(i));
        }
        setupRecyclerView((RecyclerView) recyclerView, list);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List list) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, list, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<ImageItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageItem item = (ImageItem) view.getTag();
                if (mTwoPane) {

                    Bundle arguments = new Bundle();
                    arguments.putString(TaskListFragment.ARG_ITEM_ID, item.getTitle());
                    Fragment fragment;
                    if (item.getTitle().equalsIgnoreCase("待办任务")) {
                        fragment = TaskListFragment.newInstance();
                    } else if (item.getTitle().equalsIgnoreCase("我的信息")) {
                        fragment = MyInfoFragment.newInstance();
                    } else {
                        fragment = new ItemDetailFragment();
                    }
                    fragment.setArguments(arguments);

                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getTitle());

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<ImageItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ImageItem imageItem = mValues.get(position);
            holder.mImageView.setImageResource(imageItem.getRes());
            holder.mContentView.setText(imageItem.getTitle());

            holder.itemView.setTag(imageItem);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView mImageView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.iv_task);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }

    private static ImageItem createItem(int position) {
        String text = "";
        int res = 0;
        switch (position) {
            case 0:
                text = "待办任务";
                res = R.mipmap.menu_icon1;
                break;
            case 1:
                text = "未上传";
                res = R.mipmap.menu_icon2;
                break;
            case 2:
                text = "已上传";
                res = R.mipmap.menu_icon3;
                break;
            case 3:
                text = "我的信息";
                res = R.mipmap.menu_icon4;
                break;
        }
        return new ImageItem(text, res);
    }
}
