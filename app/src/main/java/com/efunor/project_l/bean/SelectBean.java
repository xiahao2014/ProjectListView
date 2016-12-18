package com.efunor.project_l.bean;

/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-11-01
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class SelectBean {


    private String parten_id;
    private String self_id;
    private String text;


    public SelectBean(String parten_id, String self_id, String text) {
        this.parten_id = parten_id;
        this.self_id = self_id;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
