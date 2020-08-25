package com.product.sampling.ui.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 陆伟 on 2020/3/11.
 * Copyright (c) 2020 . All rights reserved.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReType {
    String value();
}
