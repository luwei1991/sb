package com.product.sampling.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.product.sampling.R;
import com.product.sampling.adapter.SpinnerSimpleAdapter;
import com.product.sampling.bean.Task;
import com.product.sampling.dummy.DummyContent;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.Exception.ApiException;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.response.ResponseTransformer;
import com.product.sampling.net.schedulers.SchedulerProvider;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.utils.ToastUtil;
import com.product.sampling.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoFragment;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.model.TakePhotoOptions;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 我的信息
 */
public class MyInfoFragment extends BasePhotoFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    Disposable disposable;
    TakePhoto takePhoto;
    ImageView ivPhoto;

    public static MyInfoFragment newInstance() {

        Bundle args = new Bundle();

        MyInfoFragment fragment = new MyInfoFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_my_info, container, false);
        ivPhoto = rootView.findViewById(R.id.image);
        rootView.findViewById(R.id.rl_photo).setOnClickListener(this);
        rootView.findViewById(R.id.rl_logout).setOnClickListener(this);
        rootView.findViewById(R.id.rl_changepwd).setOnClickListener(this);

        String image = AccountManager.getInstance().getUserInfoBean().getPhoto();

        if (null != image && !TextUtils.isEmpty(image)) {
            Glide.with(this).load(image).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivPhoto);
        }
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        }
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_photo:
                selectPhoto(1, false, true, false);
                break;
            case R.id.rl_logout:
                showDialog("确定退出登录吗", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                        ActivityManager am = ActivityManager.getInstance();
                        am.popAllActivity();
                    }
                });
                break;
            case R.id.rl_changepwd:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;

        }
    }


    @Override
    public void showResultImages(ArrayList<TImage> images) {

        String string = images.get(0).getOriginalPath();
        File file = new File(string);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", file.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file));
        RequestBody userid = RequestBody.create(null, "d444d03c23ca4a75aae89c81dbcbcdf6");

        NetWorkManager.getRequest().setPhoto(userid, filePart)
//                .compose(ResponseTransformer.handleResult())
                .compose(SchedulerProvider.getInstance().applySchedulers())
                .subscribe(userbean -> {
                    Log.e("photo:", userbean + "");
                }, throwable -> {
                    String displayMessage = ((ApiException) throwable).getDisplayMessage();
                    ToastUtils.showToast(displayMessage);
                });
    }


    public void showDialog(String title, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity()).setTitle(title)
                .setPositiveButton("确定", listener).setNegativeButton("取消", null).show();
    }
}
