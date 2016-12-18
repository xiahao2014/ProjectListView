package com.efunor.project_l.bean;

/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-12-16
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class TaskBean {

    private String title;
    private String task_id;
    private String parent_id;

    public TaskBean(String title, String task_id, String parent_id) {
        this.title = title;
        this.task_id = task_id;
        this.parent_id = parent_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
