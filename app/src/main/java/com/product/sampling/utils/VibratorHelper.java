package com.product.sampling.utils;

import android.content.Context;
import android.os.Vibrator;

public class VibratorHelper {
    private static VibratorHelper instance;
    private static Context mContext;
    private Vibrator mVibrator;
    private VibratorHelper(Context context){
        mContext=context;
        if (mVibrator==null){
            mVibrator= (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }
    public static VibratorHelper getInstance(Context context){
        if (instance==null){
            synchronized (VibratorHelper.class){
                if (instance==null){
                    instance=new VibratorHelper(context);
                }
            }
        }
        return instance;
    }
    public Vibrator getVib(){

              return mVibrator;
    }


}
