package com.product.sampling.httpmoudle;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 *Created by gdy on 2018年10月22日15:58:23.
 * 国际化语言设置选项
 */

public class LocalManageUtil {
    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return LocalSPUtil.getInstance(context).getSystemCurrentLocal(context);
    }

    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {
        switch (LocalSPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                Locale locale = getSystemLocale(context);
                if (!locale.getLanguage().startsWith("zh") && !locale.getLanguage().startsWith("en")) {
                    locale = Locale.US;
                }
                return locale;
            case 1:
                return Locale.SIMPLIFIED_CHINESE;
            case 2:
                return Locale.US;
            case 3:
                return Locale.KOREA;
            default:
                return Locale.US;
        }

    }


    public static void saveSelectLanguage(Context context, int select) {
        LocalSPUtil.getInstance(context).saveLanguage(select);
        //setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 设置语言类型
     *
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }

        LocalSPUtil.getInstance(context).setSystemCurrentLocal(locale);
    }

    public static void onConfigurationChanged(Context context){
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }

    public static void applyLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(newLanguage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale);
        } else {
            // updateConfiguration
            configuration.locale = locale;
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }
    }


    public static Context attachBaseContext(Context context, String language) {
        Locale locale = getSetLanguageLocale(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context, locale.getLanguage());
        } else {
            applyLanguage(context, locale.getLanguage());
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = null;
        if(language.startsWith("zh")){
            locale = Locale.SIMPLIFIED_CHINESE;
        }else if(language.startsWith("en")||language.toLowerCase().startsWith("english")){
            locale = Locale.ENGLISH;
        }
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

}
