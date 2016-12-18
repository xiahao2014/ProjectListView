package com.efunor.project_l.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.efunor.project_l.R;
import com.efunor.project_l.bean.ItemBean;
import com.efunor.project_l.db.DbManager;
import com.efunor.project_l.ui.TouchInterceptorListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 自定义适配器，常见将Cursor或者其他的数据存储在一个集合中，效率提高！
 */
public class MyAdapter extends BaseAdapter {
    private List<ItemBean> list;
    private LayoutInflater listContainer;           //视图容器
    private Context mContext;
    private DbManager mDbManager;
    private ExpandListerner mExpandListerner;

    //为该适配器设置展现的数据，当我们为该适配器指定了新的数据后，应该调用notifyDataSetChanged()方法后才能完成数据的动态加载
    public void setList(List<ItemBean> list, Context context) {
        this.list = list;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置
        mContext = context;
        mDbManager = DbManager.getInstance(context);
    }


    public interface ExpandListerner {
        public void Expand(MyAdapter adapter, int position, List<ItemBean> itemBeen, List<ItemBean> subItemBean);
    }

    public void setExpandListerner(ExpandListerner expandListerner) {
        mExpandListerner = expandListerner;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //重要方法，用于展现该listview的item条目
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //获取list_item布局文件的视图
        convertView = listContainer.inflate(R.layout.edit_track_list_item, null);
        ImageView iv_expand = (ImageView) convertView.findViewById(R.id.play_indicator);
        TextView text = (TextView) convertView.findViewById(R.id.line2);
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_partent);
//        RelativeLayout roof_layout = (RelativeLayout) convertView.findViewById(R.id.roof_layout);
//        final RelativeLayout item_menu = (RelativeLayout) convertView.findViewById(R.id.item_menu);
        if (list.get(position).is_indent()) {
            RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.rl_partent);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl.getLayoutParams();
            lp.setMargins(TouchInterceptorListView.SUBMARGIN, 0, 0, 0);
            relativeLayout.setLayoutParams(lp);
//            rl.setBackgroundColor(Color.parseColor("#553db369"));
        } else {
            RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.rl_partent);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            relativeLayout.setLayoutParams(lp);

            iv_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<ItemBean> subItemBeam = new ArrayList<>();
                    subItemBeam.clear();
                    subItemBeam = mDbManager.querySubOrgList(list.get(position).getSelf_id());
                    mExpandListerner.Expand(MyAdapter.this, position, list, subItemBeam);
                }
            });

        }

//        if (expandPosition == position) {
////            ViewGroup.LayoutParams params =  roof_layout.getLayoutParams();
////            params.height = mRes.getDimensionPixelSize(R.dimen.expand_menu);
////            convertView.setLayoutParams(params);
////            item_menu.setVisibility(View.VISIBLE);
//        } else {
////            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) roof_layout.getLayoutParams();
////            params.height = mRes.getDimensionPixelSize(R.dimen.normal_height);
////            convertView.setLayoutParams(params);
////            item_menu.setVisibility(View.GONE);
//        }
//
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //如果当前项为展开，则将其置为-1，目的是为了让其隐藏，如果当前项为隐藏，则将当前位置设置给全局变量，让其展开，这也就是借助于中间变量实现布局的展开与隐藏
//                if (expandPosition == position) {
//                    expandPosition = -1;
//                } else {
//                    expandPosition = position;
//                }
////                notifyDataSetChanged();
//
//            }
//        });
        text.setText(list.get(position).getMsg() + "");
        relativeLayout.setTag(position);
        return convertView;
    }

    public void insertBean(int position, List<ItemBean> itemBeans) {
        if (itemBeans.size() > 0) {
            if (!list.get(position).isExpand()) {
                Iterator<ItemBean> iter = list.iterator();
                while (iter.hasNext()) {
                    ItemBean itemBean = iter.next();
                    for (int i = 0; i < itemBeans.size(); i++) {
                        if (itemBean.getSelf_id().equals(itemBeans.get(i).getSelf_id())) {
                            iter.remove();
                        }
                    }
                }
                for (int i = 0; i < itemBeans.size(); i++) {
                    list.add(position + i + 1, itemBeans.get(i));
                }
                list.get(position).setIsExpand(true);

            } else {
//                for (int i = 0; i < itemBeans.size(); i++) {
//                    list.remove(position + 1);
//                }
                Iterator<ItemBean> iter = list.iterator();
                while (iter.hasNext()) {
                    ItemBean itemBean = iter.next();
                    for (int i = 0; i < itemBeans.size(); i++) {
                        if (itemBean.getSelf_id().equals(itemBeans.get(i).getSelf_id())) {
                            iter.remove();
                        }
                    }
                }
                list.get(position).setIsExpand(false);

            }
            notifyDataSetChanged();
        }
    }

    public void addUpdatedData(String[] data) {
//        for (int i=data.length-1; i>=0; --i) {
//            mData.add(0, data[i]);
//        }

        notifyDataSetChanged();
    }

    public void addLoadedData(String[] data) {
//        for (int i=0; i<data.length; ++i) {
//            mData.add(data[i]);
//        }

        notifyDataSetChanged();
    }
}