package com.product.sampling.bean;

import android.os.Parcelable;

import com.luck.picture.lib.entity.LocalMedia;

public class LocalMediaInfo extends LocalMedia implements Parcelable {
    public String title = " ";
    public boolean isRequstFromServer = true;

}
