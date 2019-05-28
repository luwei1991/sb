package com.product.sampling.photo;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
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
import com.product.sampling.ui.ItemDetailActivity;
import com.product.sampling.ui.ItemListActivity;
import com.product.sampling.utils.ToastUtil;
import com.product.sampling.utils.ToastUtils;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public abstract class BasePhotoFragment extends TakePhotoFragment {
    TakePhoto takePhoto;
    int imageListPostionInTask = -1;

    public void selectPhoto(int limit, boolean isCrop, boolean isFromFile, boolean isCompress, int pos) {
        imageListPostionInTask = pos;
        selectPhoto(limit, isCrop, isFromFile, isCompress);
    }

    public void selectPhoto(int limit, boolean isCrop, boolean isFromFile, boolean isCompress) {

        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto, isCompress, 1024 * 5, 800, 800, true, true, true);
        configTakePhotoOption(takePhoto, true, true);
        if (limit > 1) {
            if (isCrop) {
                takePhoto.onPickMultipleWithCrop(limit, getCropOptions(isCrop, true, true, 800, 800));
            } else {
                takePhoto.onPickMultiple(limit);
            }
            return;
        }
        if (isFromFile) {
            if (isCrop) {
                takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions(isCrop, true, true, 800, 800));
            } else {
                takePhoto.onPickFromDocuments();
            }
            return;
        } else {
            if (isCrop) {
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions(isCrop, true, true, 800, 800));
            } else {
                takePhoto.onPickFromGallery();
            }
        }
    }

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    /**
     * @param takePhoto
     * @param isTakePhotoSelfGallery 是否使用TakePhoto自带相册
     * @param isCorrectYes           是否纠正照片旋转角度
     */
    private void configTakePhotoOption(TakePhoto takePhoto, boolean isTakePhotoSelfGallery, boolean isCorrectYes) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        if (isTakePhotoSelfGallery) {
            builder.setWithOwnGallery(true);
        }
        if (isCorrectYes) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());

    }

    /**
     * @param isCrop         是否裁切
     * @param isToolsCropOwn 裁切工具TakePhoto自带
     * @return
     */
    private CropOptions getCropOptions(boolean isCrop, boolean isToolsCropOwn, boolean isAspect, int height, int width) {
        if (isCrop) {
            return null;
        }
        CropOptions.Builder builder = new CropOptions.Builder();

        if (isAspect) {
            builder.setAspectX(width).setAspectY(height);
        } else {
            builder.setOutputX(width).setOutputY(height);
        }
        builder.setWithOwnCrop(isToolsCropOwn);
        return builder.create();
    }

    /**
     * @param takePhoto
     * @param isCompress
     * @param maxSize                        最大尺寸 B
     * @param width
     * @param height
     * @param isShowCompressLoading
     * @param isCompressByOwedTools
     * @param isSaveOrginPhotoAfterTakePhoto
     */
    private void configCompress(TakePhoto takePhoto, boolean isCompress, int maxSize, int width, int height, boolean isShowCompressLoading, boolean isCompressByOwedTools, boolean isSaveOrginPhotoAfterTakePhoto) {
        if (!isCompress) {
            takePhoto.onEnableCompress(null, false);
            return;
        }
        CompressConfig config;
        if (isCompressByOwedTools) {
            config = new CompressConfig.Builder().setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(isSaveOrginPhotoAfterTakePhoto)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(isSaveOrginPhotoAfterTakePhoto);
        }
        takePhoto.onEnableCompress(config, isShowCompressLoading);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        ArrayList<TImage> resultImages = result.getImages();
        if (null != resultImages && !resultImages.isEmpty()) {
            if (imageListPostionInTask == -1) {
                showResultImages(resultImages);
            } else {
                showResultImages(resultImages, imageListPostionInTask);
            }
        } else {
            ToastUtil.showToast(getContext(), "还未选中图片");
        }
    }

    public void showResultImages(ArrayList<TImage> images) {
    }

    public void showResultImages(ArrayList<TImage> images, int imageListPostionInTask) {
    }

    private AlertDialog alertDialog;

    public void showLoadingDialog() {
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });
        alertDialog.show();
        alertDialog.setContentView(R.layout.loading_alert);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }


}
