package com.product.sampling.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.product.sampling.R;
import com.product.sampling.bean.New;
import com.product.sampling.utils.ScreenUtils;
import com.product.sampling.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PdfDocumentActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private final int QualityMiddle = 1;
    private final int QualitySmall = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        verifyStoragePermissions(this);
    }

    /**
     * 步骤1：建立一个空的PdfDocument。
     * <p>
     * 步骤2：建立一个新的page,  注意view的高宽必须大于1。这里内部其实建立一个Canvas.
     * <p>
     * 步骤3：在这个canvas把view画出来。
     * <p>
     * 步骤4：告诉page已经结束。
     * <p>
     * 要重新开始新的一页的话，重复步骤2到4。
     * <p>
     * 最后步骤5：再close.这时候就不能再加page了。
     */
    private void initView() {

//        View pdfview = getLayoutInflater().inflate(R.layout.activity_pdf, null); //1
//
//        TextView textView = pdfview.findViewById(R.id.tv_test);
//        textView.setText("打印pdf");
//        pdfview.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), //2, 测量大小
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        pdfview.layout(0, 0, pdfview.getMeasuredWidth(), pdfview.getMeasuredHeight()); //3, 测量位置
//
//
//        PdfDocument document = new PdfDocument();//1, 建立PdfDocument
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
//                ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this), 1).create();//2
//        PdfDocument.Page page = document.startPage(pageInfo);
//        pdfview.draw(page.getCanvas());//3
//        document.finishPage(page);//4
        try {
            String path = Environment.getExternalStorageDirectory() + File.separator + "table.pdf";

            File e = new File(path);
            if (e.exists()) {
                e.delete();
            }
            boolean makeNewFile = e.createNewFile();
            if (!makeNewFile) {
                ToastUtil.showShortToast(this, "无法创建pdf文件");
                return;
            }
            generate(path, true, 1);

//            FileOutputStream fos = new FileOutputStream(e, true);
////            fos.write(sb.toString().getBytes());
//            document.writeTo(fos);

        } catch (IOException e) {
            e.printStackTrace();
        }
//        document.close();
    }


    public void verifyStoragePermissions(Activity activity) {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            } else {
                initView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。
                    initView();
                } else {
                    Toast.makeText(this, "拒绝了读写存储权限", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * PDF转换
     */
    private void generate(String path, boolean isRotating, int quality) {
        Observable.just(path)
                .observeOn(Schedulers.computation())
                .map(new Function<String, PdfDocument>() {
                    @Override
                    public PdfDocument apply(String s) throws Exception {
                        View pdfview = getLayoutInflater().inflate(R.layout.activity_pdf, null); //1

                        TextView textView = pdfview.findViewById(R.id.tv_test);
                        textView.setText("生成pdf");
                        pdfview.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), //2, 测量大小
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        pdfview.layout(0, 0, pdfview.getMeasuredWidth(), pdfview.getMeasuredHeight()); //3, 测量位置
                        PdfDocument document = new PdfDocument();//1, 建立PdfDocument
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        float scale = ScreenUtils.getScreenWidth(PdfDocumentActivity.this) / pdfview.getMeasuredWidth();

                        int screenW = (int) (ScreenUtils.getScreenWidth(PdfDocumentActivity.this));
                        int screenH = (int) (ScreenUtils.getScreenHeight(PdfDocumentActivity.this));

                        if (isRotating) {
                            //当前是否翻转
//                            int temp = screenW;
//                            screenW = screenH;
//                            screenH = temp;

//                            pdfview.layout(0, 0, pdfview.getMeasuredWidth(), (int) (screenH * scale)); //3, 测量位置
                        }

                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                                pdfview.getMeasuredWidth(),
                                screenH,
                                1).create();

                        // start a page
                        PdfDocument.Page page = document.startPage(pageInfo);

                        // draw something on the page
//                        WeakReference<Bitmap> wfb = new WeakReference<>(BitmapFactory.decodeFile(bean.getRawPath(), options));
//                        Bitmap bitmap = wfb.get();
//                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//                        int bitmapW = (int) (bitmap.getWidth() * scale);
//                        int bitmapH = (int) (bitmap.getHeight() * scale);
//
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(bean.getRotating());
//
//                        WeakReference<Bitmap> nwfb = new WeakReference<>(Bitmap.createScaledBitmap(bitmap, bitmapW, bitmapH, false));
//
//                        if (bean.getRotating() % 180 != 0) {
//                            int temp = bitmapW;
//                            bitmapW = bitmapH;
//                            bitmapH = temp;
//                        }
//                        Bitmap newBM = nwfb.get();
//                        Rect dst = new Rect(0, 0, bitmapW, bitmapH);
//                        page.getCanvas().drawBitmap(newBM, null, dst, paint);
//                        bitmap.recycle();
//                        wfb.clear();
//                        newBM.recycle();
//                        nwfb.clear();

//                        document.finishPage(page);
//                    }


                        pdfview.draw(page.getCanvas());//3
                        document.finishPage(page);//4

                        // write the document content
                        OutputStream out = new FileOutputStream(path);
                        document.writeTo(out);
                        out.close();
                        //close the document
                        document.close();
                        return document;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PdfDocument>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        showLoading();
                    }

                    @Override
                    public void onNext(PdfDocument pdfDocument) {

                    }


                    @Override
                    public void onError(Throwable e) {
//                        hideLoading();
//                        LogUtils.e(e);
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(PdfDocumentActivity.this, "生成table.pdf成功,请到手机存储文件目录下查看", Toast.LENGTH_LONG).show();

                        Log.e("pdfpath",new File(path).getAbsolutePath());
//                        hideLoading();
//                        finish();
                    }
                });

    }
}
