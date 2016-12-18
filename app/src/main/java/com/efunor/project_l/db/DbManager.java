package com.efunor.project_l.db;

import android.content.Context;

import com.efunor.project_l.bean.ItemBean;
import com.efunor.project_l.bean.TaskBean;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.WhereCondition;

/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-09-09
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class DbManager {

    private Context mContext;
    private static DbManager instance = null;
    private static final String DB_NAME = "test.db";
    private TaskLayerDao mTaskLayerDao;
    private TaskDetailLayerDao mTaskDetailLayerDao;
    /**
     * 定义最上层ID
     */
    public static final String root_id = "0";


    private String test_P[] = {"新UI多次滑屏", "测试数据量重复问题", "今天完成某某任务", "测试分享功能是否可用", "重点关注数据加载重复问题"
            , "没什么其他的问题", "测试升级问题", "手机向导实现", "厉害了word哥", "我真的笑了...", "明天天气怎么样", "O(∩_∩)O哈哈哈~"
            , "太多了。。。。"};
    private String test_C[] = {"添加引导页", "其他的没什么了", "你妈喊你回家吃饭", "我笑了...", "明天泡妞！"
            , "厉害了word哥", "太多了。。。。", "今天完成某某任务"
            , "为什么密码需要进行哈希？", "如何破解哈希加密", "加盐", "无效的哈希方法", "恰当使用哈希加密", "Deprecation Notice",
            "Update Notice", "干货集中营", "码农周刊"};

    private DbManager(Context context) {
        this.mContext = context;
        init();
    }

    public static DbManager getInstance(Context context) {
        if (instance == null)
            instance = new DbManager(context);
        return instance;
    }


    private void init() {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        mTaskLayerDao = daoSession.getTaskLayerDao();
        mTaskDetailLayerDao = daoSession.getTaskDetailLayerDao();

    }

    /**
     * 添加数据库测试数据
     */
    public void addTestData2() {
        int j = 0;
        int k = 0;
        int s = 5;
        int l = 0;
        for (int i = 1; i <= 30; i++) {
            String p_msg;
            String c_Msg;
            if (i == 1) {
                p_msg = test_P[j];
                //产生为 “1” 父级
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, i);
//                insertTestData(itemBean);

                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, ++k, i + "");
                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(p_msg, i + "", root_id + "");
                insertTaskData(taskBean);
                j++;
            }
            if (i == 2 || i == 3) {
                c_Msg = test_C[k];
                //产生为 “1” 的两个子级


                TaskBean taskBean = new TaskBean(c_Msg, i + "", 1 + "");
                insertTaskData(taskBean);

                k++;
            }
            if (i == 4 || i == 5) {
                p_msg = test_P[j];
                //产生为 “4,5” 父级
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(p_msg, i + "", root_id + "");
                insertTaskData(taskBean);

                j++;
            }
            if (i == 6 || i == 7) {
                p_msg = test_P[j];
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, ++l);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(p_msg, i + "", root_id + "");
                insertTaskData(taskBean);


                j++;
            }
            if (i == 8 || i == 9) {
                c_Msg = test_C[k];
//                ItemBean itemBean = new ItemBean(i + "", 7 + "", c_Msg, true, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(c_Msg, i + "", 7 + "");
                insertTaskData(taskBean);

                k++;
            }

            if (i == 10 || i == 11) {
                c_Msg = test_C[s];
//                ItemBean itemBean = new ItemBean(i + "", 9 + "", c_Msg, true, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(c_Msg, i + "", 9 + "");
                insertTaskData(taskBean);

                s++;
            }
            if (i == 12 || i == 13) {
                p_msg = test_P[j];
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(p_msg, i + "", root_id + "");
                insertTaskData(taskBean);

                j++;
            }

            if (i == 14 || i == 15) {
                c_Msg = test_C[k];
//                ItemBean itemBean = new ItemBean(i + "", 13 + "", c_Msg, true, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(c_Msg, i + "", 13 + "");
                insertTaskData(taskBean);

                k++;
            }

            if (i == 16 || i == 17) {
                p_msg = test_P[j];
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(p_msg, i + "", root_id + "");
                insertTaskData(taskBean);

                j++;
            }

            if (i == 18 || i == 19) {
                c_Msg = test_C[k];
//                ItemBean itemBean = new ItemBean(i + "", 17 + "", c_Msg, true, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(c_Msg, i + "", 17 + "");
                insertTaskData(taskBean);

                k++;
            }
            if (i == 20) {
                p_msg = test_P[j];
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(p_msg, i + "", root_id + "");
                insertTaskData(taskBean);

                j++;
            }

            if (i == 21 || i == 22 || i == 23 || i == 24) {
                c_Msg = test_C[k];
//                ItemBean itemBean = new ItemBean(i + "", 20 + "", c_Msg, true, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(c_Msg, i + "", 20 + "");
                insertTaskData(taskBean);

                k++;
            }


            if (i == 25 || i == 26) {
                c_Msg = test_C[s];
//                ItemBean itemBean = new ItemBean(i + "", 24 + "", c_Msg, true, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(c_Msg, i + "", 24 + "");
                insertTaskData(taskBean);

                s++;
            }

            if (i == 27) {
                p_msg = test_P[j];
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", p_msg, false, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(p_msg, i + "", root_id + "");
                insertTaskData(taskBean);

            }

            if (i == 28 || i == 29 || i == 30) {
                c_Msg = test_C[k];
//                ItemBean itemBean = new ItemBean(i + "", 27 + "", c_Msg, true, i);
//                insertTestData(itemBean);

                TaskBean taskBean = new TaskBean(c_Msg, i + "", 27 + "");
                insertTaskData(taskBean);
                k++;
            }
        }
    }


    public void insertTaskData(TaskBean taskBean) {
        TaskDetailLayer mTaskBean = new TaskDetailLayer();
        if (taskBean != null) {
            mTaskBean.setTask_id(taskBean.getTask_id());
            mTaskBean.setParent_id(taskBean.getParent_id());
            mTaskBean.setTitle(taskBean.getTitle());
            mTaskDetailLayerDao.insert(mTaskBean);
        }

    }

    public void taskSize() {
        System.out.println(mTaskDetailLayerDao.count());
        System.out.println(mTaskLayerDao.count());
    }

    /**
     * 连接查询
     * 任务中有排序的
     *
     * @return
     */
    public List<ItemBean> quertTaskJoinOrder() {
        List<ItemBean> itemBeans = new ArrayList<>();
        List<TaskLayer> list = mTaskLayerDao.queryBuilder()
                .orderAsc(TaskLayerDao.Properties.Order_list)
                .where(new WhereCondition.StringCondition("tid in" + "(select task_id from Task_Detail_Layer)"))
                .build()
                .list();
        for (TaskLayer taskLayer : list) {
            ItemBean itemBean = new ItemBean();
            itemBean.setSelf_id(taskLayer.getSelf_id());
            itemBean.setParten_id(taskLayer.getParent_id());
            itemBean.setMsg(taskLayer.getTid());
            itemBean.setIs_indent(taskLayer.getIs_indent());
            itemBeans.add(itemBean);
        }
        return itemBeans;
    }


    public List<TaskDetailLayer> queryTaskDetilList() {
        List<TaskDetailLayer> taskDetailLayers = mTaskDetailLayerDao.queryBuilder().build().list();
        return taskDetailLayers;
    }


    public void insertTestData(ItemBean itemBean) {
        TaskLayer mTaskLayer = new TaskLayer();
        if (itemBean != null) {
            mTaskLayer.setSelf_id(itemBean.getSelf_id());
            mTaskLayer.setParent_id(itemBean.getParten_id());
            mTaskLayer.setTid(itemBean.getMsg());
            mTaskLayer.setIs_indent(itemBean.is_indent());
            mTaskLayer.setUpdate_time(System.currentTimeMillis());
            mTaskLayer.setOrder_list(itemBean.getOrder_list());
            mTaskLayer.setTid(itemBean.getTid());
            mTaskLayerDao.insert(mTaskLayer);
        }
    }

    /**
     * 查询顶层的数据
     */
    public List<ItemBean> queryRootData() {
        List<TaskLayer> taskLayerList = mTaskLayerDao.queryBuilder()
                .where(TaskLayerDao.Properties.Parent_id.eq(root_id))
                .orderAsc(TaskLayerDao.Properties.Order_list)
                .build().list();
        List<ItemBean> itemBeans = new ArrayList<>();
        for (TaskLayer alarm : taskLayerList) {
            ItemBean itemBean = new ItemBean();
            itemBean.setSelf_id(alarm.getSelf_id());
            itemBean.setParten_id(alarm.getParent_id());
            itemBean.setMsg(alarm.getTid());
            itemBean.setIs_indent(alarm.getIs_indent());
            itemBeans.add(itemBean);
        }
        return itemBeans;
    }


    /**
     * 查询子项
     * <br/>Version 1.0
     * <br/>CreateTime 2016/6/14,11:03
     * <br/>UpdateTime 2016/6/14,11:03
     * <br/>CreateAuthor XiaHao
     * <br/>UpdateAuthor
     * <br/>UpdateInfo
     *
     * @param self_id
     */
    public List<ItemBean> querySubOrgList(String self_id) {
        List<TaskLayer> subTaskLayerList = mTaskLayerDao.queryBuilder()
                .where(TaskLayerDao.Properties.Parent_id.eq(self_id))
                .orderAsc(TaskLayerDao.Properties.Order_list)
                .build().list();

        List<ItemBean> itemBeans = new ArrayList<>();
        for (TaskLayer alarm : subTaskLayerList) {
            ItemBean itemBean = new ItemBean();
            itemBean.setSelf_id(alarm.getSelf_id());
            itemBean.setParten_id(alarm.getParent_id());
            itemBean.setMsg(alarm.getTid());
            itemBean.setIs_indent(alarm.getIs_indent());
            itemBeans.add(itemBean);
        }
        return itemBeans;
    }

    /**
     * 通过self_id,查数据
     *
     * @param self_id
     * @return
     */
    public TaskLayer queryByIdInfo(String self_id) {
        List<TaskLayer> taskLayerList = mTaskLayerDao.queryBuilder()
                .where(TaskLayerDao.Properties.Self_id.eq(self_id))
                .build().list();
        if (taskLayerList != null && taskLayerList.size() > 0) {
            return taskLayerList.get(0);
        } else {
            return null;
        }
    }

    public long dataCount() {
        return mTaskLayerDao.count();
    }

    /**
     * 更新数据的层级关系
     */
    public void updateItemLayer(TaskLayer taskLayer) {
        mTaskLayerDao.insertOrReplace(taskLayer);
    }
}

