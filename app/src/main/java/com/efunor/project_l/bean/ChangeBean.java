package com.efunor.project_l.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-10-26
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class ChangeBean implements Parcelable {
    private int order_list;
    private String self_id;

    public int getOrder_list() {
        return order_list;
    }

    public void setOrder_list(int order_list) {
        this.order_list = order_list;
    }

    public String getSelf_id() {
        return self_id;
    }

    public void setSelf_id(String self_id) {
        this.self_id = self_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.order_list);
        dest.writeString(this.self_id);
    }

    public ChangeBean() {
    }

    protected ChangeBean(Parcel in) {
        this.order_list = in.readInt();
        this.self_id = in.readString();
    }

    public static final Creator<ChangeBean> CREATOR = new Creator<ChangeBean>() {
        @Override
        public ChangeBean createFromParcel(Parcel source) {
            return new ChangeBean(source);
        }

        @Override
        public ChangeBean[] newArray(int size) {
            return new ChangeBean[size];
        }
    };
}
