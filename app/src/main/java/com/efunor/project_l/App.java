package com.efunor.project_l;

import android.app.Application;

import com.antfortune.freeline.FreelineCore;

/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-11-11
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FreelineCore.init(this);
    }
}
