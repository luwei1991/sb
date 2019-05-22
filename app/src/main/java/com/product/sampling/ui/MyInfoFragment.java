package com.product.sampling.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.product.sampling.R;
import com.product.sampling.dummy.DummyContent;
import com.product.sampling.httpmoudle.RetrofitService;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.net.NetWorkManager;
import com.product.sampling.net.ZBaseObserver;
import com.product.sampling.net.request.Request;
import com.product.sampling.photo.BasePhotoFragment;
import com.product.sampling.utils.LogUtils;
import com.product.sampling.utils.RxSchedulersHelper;
import com.product.sampling.utils.ToastUtil;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.model.TImage;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.product.sampling.httpmoudle.Constants.IMAGE_BASE_URL;

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

        if (!TextUtils.isEmpty(image)) {

            Glide.with(this).load(IMAGE_BASE_URL + image).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivPhoto);
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

        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);//把文件与类型放入请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userid", AccountManager.getInstance().getUserId())
                .addFormDataPart("photo", file.getName(), requestFile).build();

        RetrofitService.createApiService(Request.class)
                .setPhotoRequestBody(requestBody)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxSchedulersHelper.ObsHandHttpResult())
                .subscribe(new ZBaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            Glide.with(getActivity()).load(IMAGE_BASE_URL + s).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivPhoto);
                            AccountManager.getInstance().setUserPhoto(s);
                            com.product.sampling.maputil.ToastUtil.show(getActivity(), "修改头像成功");
                        }
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        com.product.sampling.maputil.ToastUtil.show(getActivity(), message + "");
                    }
                });

    }


    public void showDialog(String title, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity()).setTitle(title)
                .setPositiveButton("确定", listener).setNegativeButton("取消", null).show();
    }
}
