package com.efunor.project_l.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-09-12
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class DensUtils {


    public int getWidth(Context context){

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
       return wm.getDefaultDisplay().getWidth();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
