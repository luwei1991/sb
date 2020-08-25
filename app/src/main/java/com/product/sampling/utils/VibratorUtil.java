package com.product.sampling.utils;

import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

public class VibratorUtil extends AppCompatActivity {

    // 指向自己实例的私有静态引用

    private static   Vibrator vibrator = null;




    // 私有的构造方法

    public VibratorUtil(){}



    // 以自己实例为返回值的静态的公有方法，静态工厂方法

    public    Vibrator getVibratorUtil(){

        // 被动创建，在真正需要使用时才去创建

        if (vibrator == null) {

            vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        }

        return  vibrator;

    }

}
