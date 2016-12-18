package com.efunor.project_l.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.efunor.project_l.R;
import com.efunor.project_l.adapter.MyAdapter;
import com.efunor.project_l.bean.ChangeBean;
import com.efunor.project_l.bean.ItemBean;
import com.efunor.project_l.bean.SelectBean;
import com.efunor.project_l.db.DbManager;
import com.efunor.project_l.db.TaskDetailLayer;
import com.efunor.project_l.db.TaskLayer;
import com.efunor.project_l.util.DensityUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.efunor.project_l.db.DbManager.root_id;


public class TouchDragDownActivity extends Activity implements TouchInterceptorListView.RemoveListener {

    private TouchInterceptorListView mTrackList, mTrackList2;

    private List<ItemBean> mItemBeans = new ArrayList<>();
    private List<ItemBean> mSubItemBeans = new ArrayList<>();
    private List<ChangeBean> mChangeItemBeans = new ArrayList<>();
    private MyAdapter adapter;
    private MyAdapter adapter2;
    private DbManager mDbManager;
    private int indexPosition = 0;

    /**
     * 屏幕宽度值。
     */
    private int screenWidth;
    /**
     * menu完全显示时，留给content的宽度值。
     */
    private int menuPadding = 200;

    /**
     * 主内容的布局。
     */
    private View content;

    /**
     * menu的布局。
     */
    private View menu;

    /**
     * menu布局的参数，通过此参数来更改leftMargin的值。
     */
    private RelativeLayout.LayoutParams menuParams, contentParams;
    /**
     * 显示右侧菜单的 速度
     */
    private static final int speed = 50;
    public static boolean isRightMenu = false;
    /**
     * 存放每一个Item的 parent_id
     */
    private BreadcrumbLayout mBreadcrumbLayout;

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView id_pull_to_refresh_loadmore_text;

    private RelativeLayout id_rl_loading;
    private ProgressBar id_pull_to_refresh_load_progress;

    final String scroll_Load_More = "加载中...";
    final String loading_Load_More = "加载中...";
    final String comp_Load_More = "加载完成";
    String nowNormalText = "";//存放当前footview显示的文字


    boolean isLoading = false;//是否正在加载中
    boolean isComp = false;//标记一次是否加载完成


    int temp_data_count_page = 0;//临时存放当前加载页对应的pos
    int data_count_page_all = 2;//模拟3页数据

    public static TouchDragDownActivity instance;


    private TouchInterceptorListView.DropListener mDropListener = new TouchInterceptorListView.DropListener() {
        public void drop(int from, int to, int currenType) {
            System.out.println("from" + from);
            System.out.println("to" + to);
            // -indexPosition 表示当前的list从下面一开始，但列表的顺序是从0开始的
            if (isRightMenu) {
                if (mBreadcrumbLayout.getCrumbCount() >= 1) { //如果大于1说明 菜单还在显示中
                    moveItem(from - indexPosition, to - indexPosition, currenType, mSubItemBeans, isRightMenu);
                    AdapterNotifyData_C();
                    mTrackList2.invalidateViews();
                }
//                else {
//                    moveItem(from - indexPosition, to - indexPosition, currenType, mSubItemBeans, isRightMenu);
//                    AdapterNotifyData_C();
//                    mTrackList2.invalidateViews();
//                }
            } else {
                moveItem(from - indexPosition, to - indexPosition, currenType, mItemBeans, isRightMenu);
                AdapterNotifyData_P();
                mTrackList.invalidateViews();
            }
        }
    };

    /**
     * 长按时 清除下面的子项
     */
    private TouchInterceptorListView.ClearFromSubListener mClearListener = new TouchInterceptorListView.ClearFromSubListener() {
        @Override
        public void clearFromSub(int from) {
            if (from >= 0 && mItemBeans.get(from).isExpand()
                    && !mItemBeans.get(from).is_indent()) {
                List<ItemBean> itemBeen = mDbManager.querySubOrgList(mItemBeans.get(from).getSelf_id());
                for (int i = 0; i < itemBeen.size(); i++) {
                    for (int j = 0; j < mItemBeans.size(); j++) {
                        if (itemBeen.get(i).getSelf_id().equals(mItemBeans.get(j).getSelf_id())) {
                            mItemBeans.remove(mItemBeans.get(j));
                        }
                    }
                }
                mItemBeans.get(from).setExpand(false);
//                adapter.notifyDataSetChanged();
                AdapterNotifyData_P();
            }
        }
    };

    /**
     * 长按时 清除下面的子项
     */
    private TouchInterceptorListView.ClearFromSubListener mClearListener2 = new TouchInterceptorListView.ClearFromSubListener() {
        @Override
        public void clearFromSub(int from) {
            if (from >= 0 && mSubItemBeans.get(from).isExpand()
                    && mItemBeans.get(from).is_indent()) {
                List<ItemBean> itemBeen = mDbManager.querySubOrgList(mSubItemBeans.get(from).getSelf_id());
                for (int i = 0; i < itemBeen.size(); i++) {
                    for (int j = 0; j < mSubItemBeans.size(); j++) {
                        if (itemBeen.get(i).getSelf_id().equals(mSubItemBeans.get(j).getSelf_id())) {
                            mSubItemBeans.remove(mSubItemBeans.get(j));
                        }
                    }
                }
                mSubItemBeans.get(from).setExpand(false);
//                adapter.notifyDataSetChanged();
                AdapterNotifyData_P();
            }
        }
    };


    MyAdapter.ExpandListerner mExpandListerner = new MyAdapter.ExpandListerner() {
        @Override
        public void Expand(MyAdapter adapter, int position, List<ItemBean> itemBeen, List<ItemBean> subItemBeam) {
            if (!isRightMenu) {
                insertBean(position, itemBeen, subItemBeam);
                adapter.notifyDataSetChanged();
            } else {
                mSubItemBeans.clear();
                mSubItemBeans = mDbManager.querySubOrgList(mItemBeans.get(position).getSelf_id());
                adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
                for (ItemBean itemBean : mSubItemBeans) {
                    itemBean.setIs_indent(false);
                }
                mTrackList2.setAdapter(adapter2);


                BreadcrumbLayout.Breadcrumb crumbAt = mBreadcrumbLayout.getCrumbAt(mBreadcrumbLayout.getCrumbCount() - 1);
                SelectBean tag = (SelectBean) crumbAt.getTag();
                tag.setParten_id(mItemBeans.get(position).getParten_id());
                tag.setSelf_id(mItemBeans.get(position).getSelf_id());
                tag.setSelf_id(mItemBeans.get(position).getSelf_id());
                crumbAt.setText(mItemBeans.get(position).getSelf_id());
            }
        }
    };


    MyAdapter.ExpandListerner mExpandListerner2 = new MyAdapter.ExpandListerner() {
        @Override
        public void Expand(MyAdapter adapter, int position, List<ItemBean> itemBeen, List<ItemBean> subItemBeam) {
            insertBean(position, itemBeen, subItemBeam);
            adapter.notifyDataSetChanged();
            System.out.println(itemBeen.get(position).toString());
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        instance = this;

        mTrackList = (TouchInterceptorListView) findViewById(R.id.mtrlist);
        mTrackList2 = (TouchInterceptorListView) findViewById(R.id.mtrlist2);
        mBreadcrumbLayout = (BreadcrumbLayout) findViewById(R.id.layer_bread);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_refresh);

        adapter = new MyAdapter();
        adapter2 = new MyAdapter();
        mDbManager = DbManager.getInstance(this);

//        mDbManager.addTestData2();
        mDbManager.taskSize();

        List<ItemBean> itemBeanList = mDbManager.quertTaskJoinOrder();
        List<TaskDetailLayer> taskDetailLayers = mDbManager.queryTaskDetilList();
        Iterator it = taskDetailLayers.iterator();
        while (it.hasNext()) {
            TaskDetailLayer taskDetailLayer = (TaskDetailLayer) it.next();
            for (int j = 0; j < itemBeanList.size(); j++) {
                if (itemBeanList.get(j).getSelf_id().equals(taskDetailLayer.getTask_id()))
                    it.remove();
            }
        }


        int orderCount = 0;
        int orderSize = orderCount = itemBeanList.size();

        if (orderSize > 0) {
            for (int i = 0; i < orderSize; i++) {
                Iterator iterator = taskDetailLayers.iterator();
                while (iterator.hasNext()) {
                    TaskDetailLayer taskDetailLayer = (TaskDetailLayer) iterator.next();
                    if (itemBeanList.get(i).getSelf_id().equals(taskDetailLayer.getParent_id())) {
                        ItemBean itemBean = new ItemBean(taskDetailLayer.getTask_id() + "", taskDetailLayer.getParent_id() + "", taskDetailLayer.getTitle(), true, ++orderCount, taskDetailLayer.getTask_id());
                        iterator.remove();
                        mDbManager.insertTestData(itemBean);
                    }
                }
            }

            TaskOrder(taskDetailLayers, (int) mDbManager.dataCount());

        } else {
            List<TaskDetailLayer> taskDetailLayersNoOrder = mDbManager.queryTaskDetilList();
            TaskOrder(taskDetailLayersNoOrder,1);

        }
        mDbManager.taskSize();



        View listview_footer_view = LayoutInflater.from(this).inflate(R.layout.listview_footer, null);
        id_rl_loading = (RelativeLayout) listview_footer_view.findViewById(R.id.id_rl_loading);
        id_pull_to_refresh_load_progress = (ProgressBar) listview_footer_view.findViewById(R.id.id_pull_to_refresh_load_progress);
        id_pull_to_refresh_load_progress.setVisibility(View.GONE);
        id_pull_to_refresh_loadmore_text = (TextView) listview_footer_view.findViewById(R.id.id_pull_to_refresh_loadmore_text);
        id_pull_to_refresh_loadmore_text.setClickable(false);


        adapter.setList(mItemBeans, this);
        mTrackList.setAdapter(adapter);
        mTrackList.setCacheColorHint(0);
        mTrackList.setDropListener(mDropListener);
        mTrackList.setClearFromSubListener(mClearListener);
        mTrackList.setRemoveListener(this);
        mTrackList.setDivider(null);
        mTrackList.setHorizontalFadingEdgeEnabled(true);
        mTrackList.addFooterView(listview_footer_view);

        mTrackList.setOnScrollListener(OnScrollListenerFour);
        id_pull_to_refresh_loadmore_text.setText(scroll_Load_More);

        mTrackList2.setCacheColorHint(0);
        mTrackList2.setDropListener(mDropListener);
        mTrackList2.setClearFromSubListener(mClearListener2);
        mTrackList2.setRemoveListener(this);
        mTrackList2.setDivider(null);
        mTrackList2.setAdapter(adapter);
        mTrackList2.setHorizontalFadingEdgeEnabled(true);


        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        //swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        int size = DensityUtil.dip2pxComm(this, 25);
        // 第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(false, 0, size);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        refreshData();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3 * 1000);
            }
        });


        adapter.setExpandListerner(mExpandListerner);
        adapter2.setExpandListerner(mExpandListerner2);


        mTrackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSubItemBeans.clear();
                mSubItemBeans = mDbManager.querySubOrgList(mItemBeans.get(position).getSelf_id());
                adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
                for (ItemBean itemBean : mSubItemBeans) {
                    itemBean.setIs_indent(false);
                }
                mTrackList2.setAdapter(adapter2);

                if (mBreadcrumbLayout.getCrumbCount() != 0) {
                    BreadcrumbLayout.Breadcrumb crumbAt = mBreadcrumbLayout.getCrumbAt(mBreadcrumbLayout.getCrumbCount() - 1);
                    SelectBean tag = (SelectBean) crumbAt.getTag();
                    tag.setParten_id(mItemBeans.get(position).getParten_id());
                    tag.setSelf_id(mItemBeans.get(position).getSelf_id());
                    tag.setSelf_id(mItemBeans.get(position).getSelf_id());
                    crumbAt.setText(mItemBeans.get(position).getSelf_id());
                }
            }
        });


        mBreadcrumbLayout.setOnBreadcrumbSelectedListener(new BreadcrumbLayout.OnBreadcrumbSelectedListener() {
            @Override
            public void onBreadcrumbSelected(BreadcrumbLayout.Breadcrumb crumb) {
                if (crumb.getTag() != null && crumb.getTag() instanceof SelectBean) {
                    mSubItemBeans.clear();
                    mSubItemBeans = mDbManager.querySubOrgList(((SelectBean) crumb.getTag()).getSelf_id());

                    adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
                    for (ItemBean itemBean : mSubItemBeans) {
                        itemBean.setIs_indent(false);
                    }
                    mTrackList2.setAdapter(adapter2);
                    isRightMenu = true;

                    mItemBeans.clear();
                    mItemBeans.addAll(mDbManager.querySubOrgList(((SelectBean) crumb.getTag()).getParten_id()));
                    adapter.setList(mItemBeans, TouchDragDownActivity.this);
                    mTrackList.setAdapter(adapter);
                }

            }

            @Override
            public void onBreadcrumbUnselected(BreadcrumbLayout.Breadcrumb crumb) {
            }

            @Override
            public void onBreadcrumbReselected(BreadcrumbLayout.Breadcrumb crumb) {
                if (crumb.getTag() != null && crumb.getTag() instanceof SelectBean) {
                    mSubItemBeans.clear();
                    mSubItemBeans = mDbManager.querySubOrgList(((SelectBean) crumb.getTag()).getSelf_id());

                    adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
                    for (ItemBean itemBean : mSubItemBeans) {
                        itemBean.setIs_indent(false);
                    }
                    mTrackList2.setAdapter(adapter2);
                    isRightMenu = true;

                    mItemBeans.clear();
                    mItemBeans.addAll(mDbManager.querySubOrgList(((SelectBean) crumb.getTag()).getParten_id()));
                    adapter.setList(mItemBeans, TouchDragDownActivity.this);
                    mTrackList.setAdapter(adapter);
                }
            }
        });


        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = window.getDefaultDisplay().getWidth();
        content = findViewById(R.id.content);
        menu = findViewById(R.id.menu);
        menuParams = (RelativeLayout.LayoutParams) menu.getLayoutParams();
        contentParams = (RelativeLayout.LayoutParams) content.getLayoutParams();
        // 将menu的宽度设置为屏幕宽度减去menuPadding
        menuParams.width = screenWidth - menuPadding;
        contentParams.width = screenWidth;

    }

    /**
     * 对没有在排序表中的任务进行自动排序
     */
    private void TaskOrder(List<TaskDetailLayer> taskDetailLayers,int orderCount) {

        for (TaskDetailLayer taskDetailLayer : taskDetailLayers) {
            ItemBean itemBean;
            if (taskDetailLayer.getParent_id().equals(root_id)){
                itemBean = new ItemBean(taskDetailLayer.getTask_id() + "", taskDetailLayer.getParent_id() + "", taskDetailLayer.getTitle(), false, ++orderCount, taskDetailLayer.getTask_id());
            }else{
                itemBean = new ItemBean(taskDetailLayer.getTask_id() + "", taskDetailLayer.getParent_id() + "", taskDetailLayer.getTitle(), true, ++orderCount, taskDetailLayer.getTask_id());
            }
            mDbManager.insertTestData(itemBean);
        }

        mItemBeans.addAll(mDbManager.queryRootData());
    }

    private void showMenu(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = getResources().getDimensionPixelOffset(R.dimen.expanded_menu_height);
        view.setLayoutParams(params);
        view.setVisibility(View.VISIBLE);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.menu);
        rl.setVisibility(View.VISIBLE);
    }

    /**
     * 滑动底部自动加载
     */
    AbsListView.OnScrollListener OnScrollListenerFour = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (view.getLastVisiblePosition() == view.getCount() - 1 && !isLoading && !isComp) {
                loadMore();
            }
        }
    };

    private void refreshData() {
        mItemBeans.clear();
        temp_data_count_page = 0;//回到第一页
        mItemBeans.addAll(mDbManager.queryRootData());
        adapter.notifyDataSetChanged();
    }

    //加载逻辑
    private void loadMore() {
        id_rl_loading.setVisibility(View.VISIBLE);
        id_pull_to_refresh_loadmore_text.setText(loading_Load_More);
        id_pull_to_refresh_loadmore_text.setClickable(false);
        id_pull_to_refresh_load_progress.setVisibility(View.VISIBLE);
        isLoading = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 3 * 1000);
    }

    //模拟加载数据
    private void loadData() {
        isLoading = false;
        if (temp_data_count_page < data_count_page_all) {//模拟加载完成了

            for (int i = 0; i < 15; i++) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("name", "name" + temp_data_count_page + "More" + i);
//                mapList.add(map);
//                ItemBean itemBean = new ItemBean(i + "", root_id + "", "新添加测试数据" + i, false, i);
//                mItemBeans.add(itemBean);
            }

            temp_data_count_page++;
            id_pull_to_refresh_loadmore_text.setText(nowNormalText);
            isComp = false;
            id_pull_to_refresh_loadmore_text.setClickable(true);
        } else {
            isComp = true;
            id_pull_to_refresh_loadmore_text.setClickable(false);
            id_pull_to_refresh_loadmore_text.setText(comp_Load_More);
        }
        id_pull_to_refresh_load_progress.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();
    }

    /**
     * 处理交换的数据变化
     * <br/>Version 1.0
     * <br/>CreateTime 2016/9/13,10:59
     * <br/>UpdateTime 2016/9/13,10:59
     * <br/>CreateAuthor XiaHao
     * <br/>UpdateAuthor
     * <br/>UpdateInfo
     *
     * @param fromPosition
     * @param toPosition
     */
    protected void moveItem(int fromPosition, int toPosition, int currenType, List<ItemBean> typeItemBeans,
                            boolean isRightMenu) {
        int lastPosition = toPosition - 1;
        if (lastPosition >= 0) {
            int dataPosition = -1;
            if (fromPosition > toPosition) {
                //表示从下往上拖动
                dataPosition = lastPosition;
            } else if (fromPosition < toPosition) {
                dataPosition = toPosition;
                //表示从上往下拖动
            } else {
                dataPosition = lastPosition;
            }
            //获取当前信息
            TaskLayer taskLayer = mDbManager.queryByIdInfo(typeItemBeans.get(fromPosition).getSelf_id());
            if (taskLayer != null)
                if (TouchInterceptorListView.LAYER_CHILD == currenType) {//表示向右拖动变子项
                    //当前拖动的是父级
                    List<ItemBean> fromSub = mDbManager.querySubOrgList(typeItemBeans.get(fromPosition).getSelf_id());
                    ItemBean temp = typeItemBeans.get(fromPosition);
                    if (!typeItemBeans.get(fromPosition).is_indent()) {
                        /** 保留被拖动的 item信息*/
                        temp.setIs_indent(true);
                        temp.setExpand(false);
                        temp.setParten_id(typeItemBeans.get(dataPosition).getSelf_id());

                        if (!typeItemBeans.get(dataPosition).is_indent()) {
                            //交换的层级的是父(更新信息)
                            taskLayer.setIs_indent(true);
                            taskLayer.setParent_id(typeItemBeans.get(dataPosition).getSelf_id());
                            mDbManager.updateItemLayer(taskLayer);

                            if (fromPosition != toPosition) {
                                /** 移除当前的在被拖动的项---动态插入新的项*/
                                typeItemBeans.remove(fromPosition);
                                typeItemBeans.add(toPosition, temp);
                            } else {
                                typeItemBeans.remove(fromPosition);
                            }

                            updateOrderList(typeItemBeans);

                            System.out.println(typeItemBeans.get(dataPosition).toString());
                            System.out.println("-----右拖动----父----父");
                        } else {
                            //交换的层级的是子(更新为子的父的信息)
                            taskLayer.setIs_indent(true);
                            taskLayer.setParent_id(typeItemBeans.get(dataPosition).getParten_id());
                            temp.setParten_id(taskLayer.getParent_id());
                            mDbManager.updateItemLayer(taskLayer);


                            /** 移除当前的在被拖动的项---动态插入新的项*/
                            typeItemBeans.remove(fromPosition);
                            typeItemBeans.add(toPosition, temp);

                            System.out.println(typeItemBeans.get(dataPosition).toString());
                            System.out.println("-----右拖动----父----子");
                        }

                        /** 移除当前父级下面的子项*/
                        for (int j = 0; j < fromSub.size(); j++) {
                            removeSub(fromSub, typeItemBeans);
                        }

                        updateOrderList(typeItemBeans);

                    } else {
                        //当前拖动的是子级
                        temp.setParten_id(typeItemBeans.get(dataPosition).getSelf_id());
                        if (!typeItemBeans.get(dataPosition).is_indent()) {
                            //交换的层级的是父
                            taskLayer.setIs_indent(true);
                            taskLayer.setParent_id(typeItemBeans.get(dataPosition).getSelf_id());
                            mDbManager.updateItemLayer(taskLayer);

                            /** 移除当前的在被拖动的项---动态插入新的项*/
                            typeItemBeans.remove(fromPosition);
                            typeItemBeans.add(toPosition, typeItemBeans.get(fromPosition));

                            System.out.println(typeItemBeans.get(dataPosition).toString());
                            System.out.println("右拖动----子----父");

                        } else {
                            //交换的层级的是子
                            taskLayer.setIs_indent(true);
                            taskLayer.setParent_id(typeItemBeans.get(dataPosition).getSelf_id());
                            mDbManager.updateItemLayer(taskLayer);
                            typeItemBeans.remove(fromPosition);

                            System.out.println(typeItemBeans.get(dataPosition).toString());
                            System.out.println("右拖动----子----子");
                        }
                        updateOrderList(typeItemBeans);
                    }

                } else if (TouchInterceptorListView.LAYER_PARENT == currenType) { //表示向左拖动变父项
                    if (typeItemBeans.get(fromPosition).is_indent()) {
                        ItemBean temp = typeItemBeans.get(fromPosition);

                        if (isRightMenu) {
                            temp.setIs_indent(false);
                            temp.setParten_id(mDbManager.queryByIdInfo(typeItemBeans.get(dataPosition).getParten_id()).getParent_id());
                            taskLayer.setIs_indent(true);

                            if (fromPosition != toPosition)
                                typeItemBeans.get(fromPosition).setExpand(false);
                            else
                                typeItemBeans.get(fromPosition).setExpand(true);
                            taskLayer.setParent_id(mDbManager.queryByIdInfo(typeItemBeans.get(dataPosition).getParten_id()).getParent_id());
                            mDbManager.updateItemLayer(taskLayer);
                        } else {
                            temp.setIs_indent(false);
                            temp.setParten_id(root_id);
                            taskLayer.setIs_indent(false);
                            if (fromPosition != toPosition)
                                typeItemBeans.get(fromPosition).setExpand(false);
                            else
                                typeItemBeans.get(fromPosition).setExpand(true);
                            taskLayer.setParent_id(root_id);
                            mDbManager.updateItemLayer(taskLayer);
                        }


                        boolean isFlag = true;
                        int nexiSub;
                        if (fromPosition != toPosition) {
                            nexiSub = dataPosition + 1;
                        } else {
                            nexiSub = toPosition + 1;
                        }
                        if (nexiSub < typeItemBeans.size()) {
                            while (isFlag) {
                                if (nexiSub < typeItemBeans.size() && typeItemBeans.get(nexiSub).is_indent()) {
                                    typeItemBeans.get(nexiSub).setParten_id(temp.getSelf_id());
                                    TaskLayer next = mDbManager.queryByIdInfo(typeItemBeans.get(nexiSub).getSelf_id());
                                    next.setParent_id(temp.getSelf_id());
                                    next.setUpdate_time(System.currentTimeMillis());
                                    mDbManager.updateItemLayer(next);
                                    nexiSub++;
                                } else {
                                    isFlag = false;
                                }
                            }
                            /** 移除当前的在被拖动的项---动态插入新的项*/
                            if (fromPosition != toPosition) {
                                typeItemBeans.remove(fromPosition);
                                typeItemBeans.add(toPosition, temp);
                            }
                        }

                        updateOrderList(typeItemBeans);
                    }

                } else if (TouchInterceptorListView.LAYER_NONE == currenType && fromPosition != toPosition &&
                        dataPosition < typeItemBeans.size()) {
                    if (!typeItemBeans.get(dataPosition).is_indent()) {
                        //发生交换的是父级 当前是父级
                        ItemBean temp_p_p = typeItemBeans.get(fromPosition);//当前被拖动的信息
                        if (!typeItemBeans.get(fromPosition).is_indent()) {
                            ItemBean lastItemBean = typeItemBeans.get(dataPosition);//拿到交换层级的信息
                            List<ItemBean> lastItemBeans = mDbManager.querySubOrgList(lastItemBean.getSelf_id());
                            List<ItemBean> currentItemBeans = mDbManager.querySubOrgList(typeItemBeans.get(fromPosition).getSelf_id());
                            for (ItemBean itemBean : lastItemBeans) {
                                TaskLayer next = mDbManager.queryByIdInfo(itemBean.getSelf_id());
                                next.setParent_id(temp_p_p.getSelf_id());
                                next.setUpdate_time(System.currentTimeMillis());
                                mDbManager.updateItemLayer(next);
                            }

                            typeItemBeans.get(fromPosition).setExpand(false);
                            /** 移除当前的在被拖动的项---动态插入新的项*/
                            typeItemBeans.remove(fromPosition);
                            typeItemBeans.add(toPosition, temp_p_p);

                            /** 移除当前父级下面的子项*/
                            for (int j = 0; j < currentItemBeans.size(); j++) {
                                removeSub(currentItemBeans, typeItemBeans);
                            }

                            updateOrderList(typeItemBeans);
                        } else {
                            //当前是子级 子----父
                            temp_p_p.setParten_id(typeItemBeans.get(dataPosition).getSelf_id());
                            if (!typeItemBeans.get(dataPosition).is_indent()) {
                                if (taskLayer != null) {
                                    taskLayer.setParent_id(typeItemBeans.get(dataPosition).getSelf_id());
                                    mDbManager.updateItemLayer(taskLayer);
                                }
                                typeItemBeans.get(dataPosition).setExpand(true);
                                //如果当前是子级
                                ItemBean temp = typeItemBeans.remove(fromPosition);
                                typeItemBeans.add(toPosition, temp);

                                updateOrderList(typeItemBeans);
                            }
                        }
                    } else {
                        //发生交换的是子级
                        if (typeItemBeans.get(fromPosition).is_indent()) {
                            //当前的是子级 子--->子
                            TaskLayer sunTask = mDbManager.queryByIdInfo(typeItemBeans.get(fromPosition).getSelf_id());
                            if (sunTask != null) {
                                sunTask.setIs_indent(true);
                                if (typeItemBeans.get(dataPosition).is_indent()) {
                                    //上一层级是子级
                                    typeItemBeans.get(fromPosition).setParten_id(typeItemBeans.get(dataPosition).getParten_id());
                                    sunTask.setParent_id(typeItemBeans.get(dataPosition).getParten_id());
                                } else {
                                    //上一层级是父级
                                    sunTask.setParent_id(typeItemBeans.get(dataPosition).getSelf_id());
                                }
                                mDbManager.updateItemLayer(sunTask);
                            }

                            ItemBean temp = typeItemBeans.remove(fromPosition);
                            typeItemBeans.add(toPosition, temp);

                            updateOrderList(typeItemBeans);
                        } else {
                            //当前的是父级 父--->子
                            ItemBean currentTask = typeItemBeans.get(fromPosition);
                            ItemBean changeItemBean = typeItemBeans.get(toPosition);
                            List<ItemBean> fromSubitemBeans = mDbManager.querySubOrgList(currentTask.getSelf_id());
                            if (fromPosition > toPosition)
                                currentTask.setExpand(false);
                            else
                                currentTask.setExpand(false);

                            if (!currentTask.getSelf_id().equals(changeItemBean.getParten_id())) {
                                boolean isFlag = true;
                                int nexiSub = dataPosition + 1;
                                if (nexiSub < typeItemBeans.size()) {
                                    while (isFlag) {
                                        if (nexiSub < typeItemBeans.size() && typeItemBeans.get(nexiSub).is_indent()) {
                                            typeItemBeans.get(nexiSub).setParten_id(currentTask.getSelf_id());
                                            TaskLayer next = mDbManager.queryByIdInfo(typeItemBeans.get(nexiSub).getSelf_id());
                                            next.setParent_id(currentTask.getSelf_id());
                                            next.setUpdate_time(System.currentTimeMillis());
                                            mDbManager.updateItemLayer(next);
                                            nexiSub++;
                                        } else {
                                            isFlag = false;
                                        }
                                    }
                                    ItemBean temp = typeItemBeans.remove(fromPosition);
                                    typeItemBeans.add(toPosition, temp);

                                    for (int k = 0; k < fromSubitemBeans.size(); k++) {
                                        removeSub(fromSubitemBeans, typeItemBeans);
                                    }
                                }
                            }

                            updateOrderList(typeItemBeans);
                        }
                    }
                    System.out.println(typeItemBeans.get(toPosition).toString());
                    mTrackList.setSelection(toPosition);
                }
        } else {
            //跟第一个进行交换
            TaskLayer taskLayer = mDbManager.queryByIdInfo(typeItemBeans.get(fromPosition).getSelf_id());
            if (isRightMenu) {
                if (typeItemBeans.get(fromPosition).is_indent()) {
                    typeItemBeans.get(fromPosition).setParten_id(mDbManager.queryByIdInfo(typeItemBeans.get(fromPosition).getParten_id()).getSelf_id());
                    taskLayer.setParent_id(mDbManager.queryByIdInfo(typeItemBeans.get(fromPosition).getParten_id()).getParent_id());
                    mDbManager.updateItemLayer(taskLayer);
                } else {
//                    typeItemBeans.get(fromPosition).setParten_id(DbManager.root_id);
//                    typeItemBeans.get(fromPosition).setParten_id(typeItemBeans.get(fromPosition).getParten_id());
                }
                typeItemBeans.get(fromPosition).setIs_indent(false);
            } else {
                typeItemBeans.get(fromPosition).setIs_indent(false);
                typeItemBeans.get(fromPosition).setParten_id(root_id);
                taskLayer.setIs_indent(false);
                taskLayer.setParent_id(root_id);
                mDbManager.updateItemLayer(taskLayer);
            }

            ItemBean temp = typeItemBeans.remove(fromPosition);
            typeItemBeans.add(toPosition, temp);
        }
        updateOrderList(typeItemBeans);
    }

    /**
     * 更新数据库的排序顺序
     *
     * @param typeItemBeans
     */
    private void updateOrderList(final List<ItemBean> typeItemBeans) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0, j = 1; i < typeItemBeans.size(); i++, j++) {
                    ChangeBean changeBean = new ChangeBean();
                    changeBean.setOrder_list(j);
                    changeBean.setSelf_id(typeItemBeans.get(i).getSelf_id());
                    mChangeItemBeans.add(changeBean);
                }
                for (int i = 0; i < mChangeItemBeans.size(); i++) {
                    TaskLayer taskLayer = mDbManager.queryByIdInfo(mChangeItemBeans.get(i).getSelf_id());
                    if (taskLayer != null)
                        for (int j = 0; j < mDbManager.dataCount(); j++) {
                            if (mChangeItemBeans.get(i).getSelf_id().equals(taskLayer.getSelf_id()) &&
                                    mChangeItemBeans.get(i).getOrder_list() != taskLayer.getOrder_list()) {
                                taskLayer.setOrder_list(mChangeItemBeans.get(i).getOrder_list());
                                mDbManager.updateItemLayer(taskLayer);
                                break;
                            }
                        }
                }
            }
        }).start();
    }


    private void removeSub(List<ItemBean> subItemBean, List<ItemBean> typeItemBeans) {
        for (int i = 0; i < subItemBean.size(); i++) {
            for (int j = 0; j < typeItemBeans.size(); j++) {
                ItemBean next = typeItemBeans.get(j);
                if (next.getSelf_id().equals(subItemBean.get(i).getSelf_id())) {
                    typeItemBeans.remove(next);
                }
            }
        }
    }

//    @Override
//    public void onUpdate() {
//        MyUpdateTask2 task = new MyUpdateTask2(mTrackList);
//        task.execute((Void) null);
//    }
//
//    @Override
//    public void onLoad() {
//        MyLoadTask2 task = new MyLoadTask2(mTrackList);
//        task.execute((Void) null);
//    }

    @Override
    public void showSlideMenu(TouchInterceptorListView.RemoveDirection direction, int position, boolean isShowMenu) {
        swipeRefreshLayout.setEnabled(true);
        if (isShowMenu && position >= 0) {
            if (isRightMenu) {
                //右滑菜单已经 存在
                String self_id = mSubItemBeans.get(position).getSelf_id();


                if (mDbManager.querySubOrgList(self_id).size() > 0) {

                    mBreadcrumbLayout.setVisibility(View.VISIBLE);
                    SelectBean orgInfo = new SelectBean(mSubItemBeans.get(position).getParten_id(), mSubItemBeans.get(position).getSelf_id(), mSubItemBeans.get(position).getSelf_id());


                    mBreadcrumbLayout.addCrumb(mBreadcrumbLayout.newCrumb().setText(mSubItemBeans.get(position).getSelf_id()).setTag(orgInfo));
                    mItemBeans.clear();
                    mItemBeans.addAll(mDbManager.querySubOrgList(mSubItemBeans.get(position).getParten_id()));
                    adapter.setList(mItemBeans, this);
                    mTrackList.setAdapter(adapter);


                    mSubItemBeans.clear();
                    mSubItemBeans.addAll(mDbManager.querySubOrgList(self_id));
                    adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
                    for (ItemBean itemBean : mSubItemBeans) {
                        itemBean.setIs_indent(false);
                    }
                    mTrackList2.setAdapter(adapter2);
                    isRightMenu = true;

                    mTrackList2.setAnimation(AnimationUtils.makeOutAnimation(this, false));
                } else {
//                    Toast.makeText(TouchDragDownActivity.this,"没有更多子项",Toast.LENGTH_SHORT);

                    Snackbar.make(mTrackList2, "当前没有更多子项...", Snackbar.LENGTH_SHORT).show();
                }

            } else {
                //显示右滑菜单

                ItemBean itemBean = mItemBeans.get(position);
                if (mDbManager.querySubOrgList(itemBean.getSelf_id()).size() > 0) {
                    isRightMenu = true;
                    itemBean.setExpand(false);
                    SelectBean orgInfo = new SelectBean(itemBean.getParten_id(), itemBean.getSelf_id(), itemBean.getSelf_id());
                    mBreadcrumbLayout.addCrumb(mBreadcrumbLayout.newCrumb().setText(itemBean.getSelf_id()).setTag(orgInfo));
                    new showRightMenuAsyncTask(itemBean).execute(-speed, position);
                    mBreadcrumbLayout.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(mTrackList, "当前没有更多子项...", Snackbar.LENGTH_SHORT).show();
                }
            }

        } else {
            if (isRightMenu) {
                if (mBreadcrumbLayout.getCrumbCount() >= 1) {
                    if (mBreadcrumbLayout.getCrumbCount() == 1) {
                        mBreadcrumbLayout.removeAllCrumbs();
                        isRightMenu = false;
                        mBreadcrumbLayout.setVisibility(View.GONE);
                        new showRightMenuAsyncTask(null).execute(speed, position);
                        AdapterNotifyData_P();
                    } else {
                        refresh();
                        mTrackList.setAnimation(AnimationUtils.makeOutAnimation(this, true));
                    }
                }
            }
        }
    }

    private void refresh() {
        int currenCount = mBreadcrumbLayout.getCrumbCount() - 1;
        BreadcrumbLayout.Breadcrumb currenCrumbAt = mBreadcrumbLayout.getCrumbAt(currenCount);
        BreadcrumbLayout.Breadcrumb lastCrumbAt = mBreadcrumbLayout.getCrumbAt(currenCount - 1);

        System.out.println(((SelectBean) currenCrumbAt.getTag()).getParten_id());
        System.out.println(((SelectBean) lastCrumbAt.getTag()).getSelf_id());

        String parten_id;
        if (currenCrumbAt.getTag() != null && currenCrumbAt.getTag() instanceof SelectBean &&
                lastCrumbAt.getTag() != null && lastCrumbAt.getTag() instanceof SelectBean) {

            if (((SelectBean) currenCrumbAt.getTag()).getParten_id().equals(((SelectBean) lastCrumbAt.getTag()).getSelf_id())) {
                parten_id = ((SelectBean) currenCrumbAt.getTag()).getParten_id();
            } else {
                parten_id = ((SelectBean) lastCrumbAt.getTag()).getSelf_id();
            }
            mSubItemBeans.clear();
            mSubItemBeans = mDbManager.querySubOrgList(parten_id);

            mBreadcrumbLayout.removeCrumbAt(currenCount);
            adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
            for (ItemBean itemBean : mSubItemBeans) {
                itemBean.setIs_indent(false);
            }
            mTrackList2.setAdapter(adapter2);
            isRightMenu = true;

            mItemBeans.clear();
            mItemBeans.addAll(mDbManager.querySubOrgList(mDbManager.queryByIdInfo(parten_id).getParent_id()));
            adapter.setList(mItemBeans, this);
            mTrackList.setAdapter(adapter);
        }
    }

    /**
     * 控制右侧View
     */
    class showRightMenuAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        ItemBean whitch;

        public showRightMenuAsyncTask(ItemBean whitch) {
            this.whitch = whitch;
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int leftMargin = contentParams.leftMargin;
            if (params[0] > 0) {
                leftMargin = leftMargin + TouchInterceptorListView.SUBMARGIN;
            } else {
                leftMargin = leftMargin - TouchInterceptorListView.SUBMARGIN;
            }

            while (true) {
                leftMargin += params[0];
                if (params[0] > 0 && leftMargin >= 0) {
                    break;
                } else if (params[0] < 0 && leftMargin <= -contentParams.width + menuPadding) {
                    break;
                }
                publishProgress(leftMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            contentParams.leftMargin = values[0];
            content.setLayoutParams(contentParams);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            contentParams.leftMargin = result;
            content.setLayoutParams(contentParams);
            if (isRightMenu) {
                if (whitch != null) {
                    if (whitch.is_indent()) {
                        //如果是直接滑动的子项
                        String self_id = whitch.getSelf_id();
                        mItemBeans.clear();
                        mItemBeans.addAll(mDbManager.querySubOrgList(whitch.getParten_id()));
                        adapter.setList(mItemBeans, TouchDragDownActivity.this);
                        mTrackList.setAdapter(adapter);

                        mSubItemBeans.clear();
                        mSubItemBeans.addAll(mDbManager.querySubOrgList(self_id));
                        adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
                        for (ItemBean itemBean : mSubItemBeans) {
                            itemBean.setIs_indent(false);
                        }
                        mTrackList2.setAdapter(adapter2);
                        isRightMenu = true;
                    } else {
                        mSubItemBeans.clear();
                        mSubItemBeans = mDbManager.querySubOrgList(whitch.getSelf_id());
                        adapter2.setList(mSubItemBeans, TouchDragDownActivity.this);
                        for (ItemBean itemBean : mSubItemBeans) {
                            itemBean.setIs_indent(false);
                        }
                        mTrackList2.setAdapter(adapter2);
                    }
                }
            } else {
                mSubItemBeans.clear();
                mItemBeans.clear();
                mItemBeans.addAll(mDbManager.querySubOrgList(root_id));
                adapter.setList(mItemBeans, TouchDragDownActivity.this);
                mTrackList.setAdapter(adapter);
            }
        }

    }

    @Override
    protected void onDestroy() {
        isRightMenu = false;
        super.onDestroy();
    }

    public void insertBean(int position, List<ItemBean> itemBeans, List<ItemBean> subItemBeans) {
        if (subItemBeans.size() > 0) {
            if (!itemBeans.get(position).isExpand()) {
                Iterator<ItemBean> iter = itemBeans.iterator();
                while (iter.hasNext()) {
                    ItemBean itemBean = iter.next();
                    for (int i = 0; i < subItemBeans.size(); i++) {
                        if (itemBean.getSelf_id().equals(subItemBeans.get(i).getSelf_id())) {
                            iter.remove();
                        }
                    }
                }
                for (int i = 0; i < subItemBeans.size(); i++) {
                    itemBeans.add(position + i + 1, subItemBeans.get(i));
                }
                itemBeans.get(position).setIsExpand(true);

            } else {
                Iterator<ItemBean> iter = itemBeans.iterator();
                while (iter.hasNext()) {
                    ItemBean itemBean = iter.next();
                    for (int i = 0; i < subItemBeans.size(); i++) {
                        if (itemBean.getSelf_id().equals(subItemBeans.get(i).getSelf_id())) {
                            iter.remove();
                        }
                    }
                }
                itemBeans.get(position).setIsExpand(false);
            }
        }
    }


    private void AdapterNotifyData_P() {
        adapter.notifyDataSetChanged();
    }

    private void AdapterNotifyData_C() {
        adapter2.notifyDataSetChanged();
    }
}