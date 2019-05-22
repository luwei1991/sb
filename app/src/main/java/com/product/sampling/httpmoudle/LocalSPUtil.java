package com.product.sampling.httpmoudle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class LocalSPUtil {

    private final String SP_NAME = "language_setting";
    private final String TAG_LANGUAGE = "language_select";
    private static volatile LocalSPUtil instance;

    private final SharedPreferences mSharedPreferences;

    private Locale systemCurrentLocal = Locale.CHINESE;


    public LocalSPUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }


    public void saveLanguage(int select) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(TAG_LANGUAGE, select);
        edit.apply();
    }

    public int getSelectLanguage() {
        return mSharedPreferences.getInt(TAG_LANGUAGE, 0);
    }


    public Locale getSystemCurrentLocal(Context context) {
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }

        String language = locale.getLanguage();
        if(language.contains("zh")){
            systemCurrentLocal = Locale.SIMPLIFIED_CHINESE;
        }else if(language.contains("en")){
            systemCurrentLocal = Locale.US;
        }else if(language.contains("ko")){
            systemCurrentLocal = Locale.KOREA;
        }
        return systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }

    public static LocalSPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalSPUtil.class) {
                if (instance == null) {
                    instance = new LocalSPUtil(context);
                }
            }
        }
        return instance;
    }


}
