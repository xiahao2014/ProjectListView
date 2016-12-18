package com.efunor.project_l.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-09-07
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class ItemBean implements Parcelable {


    private  String tid;
    private String self_id;
    private String parten_id;
    private String msg;
    private boolean is_indent;
    private boolean isExpand;
    private boolean isShowMenu;
    private int order_list;


    public int getOrder_list() {
        return order_list;
    }

    public void setOrder_list(int order_list) {
        this.order_list = order_list;
    }


    public boolean isShowMenu() {
        return isShowMenu;
    }

    public void setShowMenu(boolean showMenu) {
        isShowMenu = showMenu;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }


    public ItemBean(String self_id, String parten_id, String msg, boolean is_indent,  int order_list,String tid) {
        this.tid = tid;
        this.self_id = self_id;
        this.parten_id = parten_id;
        this.msg = msg;
        this.is_indent = is_indent;
        this.order_list = order_list;

    }

    public ItemBean() {
    }

    public ItemBean(String self_id, String parten_id, String msg, boolean is_indent, int order_list) {
        this.self_id = self_id;
        this.parten_id = parten_id;
        this.msg = msg;
        this.is_indent = is_indent;
        this.order_list = order_list;
    }

    public String getSelf_id() {
        return self_id;
    }

    public void setSelf_id(String self_id) {
        this.self_id = self_id;
    }

    public String getParten_id() {
        return parten_id;
    }

    public void setParten_id(String parten_id) {
        this.parten_id = parten_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean is_indent() {
        return is_indent;
    }

    public void setIs_indent(boolean is_indent) {
        this.is_indent = is_indent;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setIsExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "self_id='" + self_id + '\'' +
                ", parten_id='" + parten_id + '\'' +
                ", msg='" + msg + '\'' +
                ", is_indent=" + is_indent +
                ", isExpand=" + isExpand +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tid);
        dest.writeString(this.self_id);
        dest.writeString(this.parten_id);
        dest.writeString(this.msg);
        dest.writeByte(this.is_indent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isExpand ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowMenu ? (byte) 1 : (byte) 0);
        dest.writeInt(this.order_list);
    }

    protected ItemBean(Parcel in) {
        this.tid = in.readString();
        this.self_id = in.readString();
        this.parten_id = in.readString();
        this.msg = in.readString();
        this.is_indent = in.readByte() != 0;
        this.isExpand = in.readByte() != 0;
        this.isShowMenu = in.readByte() != 0;
        this.order_list = in.readInt();
    }

    public static final Creator<ItemBean> CREATOR = new Creator<ItemBean>() {
        @Override
        public ItemBean createFromParcel(Parcel source) {
            return new ItemBean(source);
        }

        @Override
        public ItemBean[] newArray(int size) {
            return new ItemBean[size];
        }
    };
}
