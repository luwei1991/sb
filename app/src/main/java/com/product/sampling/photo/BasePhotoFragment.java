package com.product.sampling.photo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.product.sampling.R;
import com.product.sampling.maputil.ToastUtil;

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

/**
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
            ToastUtil.showShortToast(getContext(), "还未选中图片");
        }
    }

    public void showResultImages(ArrayList<TImage> images) {
    }

    public void showResultImages(ArrayList<TImage> images, int imageListPostionInTask) {
    }

    private AlertDialog alertDialog;

    public void showLoadingDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(true);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        alertDialog.show();
        alertDialog.setContentView(R.layout.loading_alert);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void showLoadingDialog(String msg) {
        Message message = new Message();
        message.what = 0;
        message.obj = msg;
        mHandler.sendMessageDelayed(message, 2*1000);
    }

    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void onRefreshTitle(boolean isImage, int index, String text) {
    }

    public void onRefreshSampleImageTitle(int samplePos, int index, String text) {
    }

    public void onRemoveSampleImage(int samplePos, int index) {
    }

    public void onRemove(boolean isImage, int index) {
    }


    public void shareBySystem(String path) {
        File doc = new File(path);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(doc));

        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.product.sampling.fileprovider", doc);
//        share.setDataAndType(contentUri, "application/vnd.android.package-archive");
        share.setDataAndType(contentUri, "application/pdf");

        startActivity(Intent.createChooser(share, "分享文件"));

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 0) {
                if (null != alertDialog) {
                    TextView tv = alertDialog.findViewById(R.id.tv_title);
                    tv.setText(msg.obj.toString());
                }
            } else {
                alertDialog.cancel();
            }
        }
    };

    @Override
    public void onDestroy() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
