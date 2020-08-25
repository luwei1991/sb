package com.product.sampling.ui.form.viewmodel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

/**
 * Created by 陆伟 on 2020/5/18.
 * Copyright (c) 2020 . All rights reserved.
 */


public class HandleLandFormViewModel extends ViewModel {
    public final ObservableField<String> sampleCode = new ObservableField<>("");//样品编号
    public final ObservableField<String> companyName = new ObservableField<>("");//单位名称
    public final ObservableField<String> wtCompany = new ObservableField<>("湖南省市场监督管理局");//委托单位
    public final ObservableField<String> handleTime = new ObservableField<>("");//时间年
    public final ObservableField<String> handleTimeYear = new ObservableField<>("");//时间年
    public final ObservableField<String> handleTimeMonth = new ObservableField<>("");//时间月
    public final ObservableField<String> handleTimeDay = new ObservableField<>("");//时间日
    public final ObservableField<Boolean> productOrSale = new ObservableField<>(false);//生产、销售
    public final ObservableField<String> productName = new ObservableField<>();//产品名称
    public final ObservableField<String> ggxh = new ObservableField<>("");//规格型号
    public final ObservableField<Boolean> byypOrJbyp = new ObservableField<>(true);//备用样品//检毕样品
    public final ObservableField<String> sjdwPath = new ObservableField<>();//受检单位签名处
    public final ObservableField<String> cyrPath_1 = new ObservableField<>();//抽样人签名
    public final ObservableField<String> cyrPath_2 = new ObservableField<>();//抽样人签名
    public final ObservableField<String> sjdwTime = new ObservableField<>();//受检单位时间
    public final ObservableField<String>  cryTime = new ObservableField<>();//抽样人时间

}
