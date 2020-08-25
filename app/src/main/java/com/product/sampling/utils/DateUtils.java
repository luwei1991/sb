package com.product.sampling.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 陆伟 on 2020/3/23.
 * Copyright (c) 2020 . All rights reserved.
 */


public class DateUtils {
    /**
     * 获取当前日期
     * @return
     */
    public static String getCurTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String nowDate = format.format(new Date());//当前日期
        return nowDate;
    }

    /**
     * 判断是否是今天
     * @param date
     * @return
     */
    public static boolean isCurTime(Date date){
        return getCurTime().equals(getSelTime(date)) ? true : false;
    }


    /**
     * 获取选择的日期
     * @param date
     * @return
     */
    public static String getSelTime(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String selDate = format.format(date);//选择的日期
        return selDate;
    }

    public static void showTimePicker(){

    }

}
