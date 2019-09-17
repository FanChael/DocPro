package com.xxxx.app.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
*@Author: hl
*@Date: created at 2019/7/17 17:44
*@Description: 基础适配器 - 支持多布局
*/
public abstract class BaseListAdapter<T extends BaseMulDataModel> extends BaseAdapter {
    private WeakReference<Context> contextWeakReference = null;
    private List<T> datas;

    public BaseListAdapter(Context context, List<T> datas) {
        this.contextWeakReference = new WeakReference<>(context);
        this.datas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseListViewHolder baseListViewHolder;
        if (convertView == null) {
            // 创建ViewHolder
            baseListViewHolder = getHolder(contextWeakReference.get(), getItemViewType(position));
            // 获取视图
            convertView = baseListViewHolder.getItemView();
            // 调整大小
            resizeHolder(contextWeakReference.get(), baseListViewHolder, getItemViewType(position));
            // 保存视图
            convertView.setTag(baseListViewHolder);
        } else {
            baseListViewHolder = (BaseListViewHolder) convertView.getTag();
        }

        T tData = null;
        if (position < datas.size()) {
            tData = datas.get(position);
        }
        // 绑定数据
        bindData(contextWeakReference.get(), baseListViewHolder, tData, position, getItemViewType(position));

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        if (null == datas) {
            return null;
        }
        if (position >= datas.size()) {
            return null;
        }
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (null == datas) {
            return 0;
        }
        if (position >= datas.size()) {
            return 0;
        }
        return datas.get(position).getDrawType();
    }

    // 子类实现布局加载返回ViewHolder
    protected abstract BaseListViewHolder getHolder(Context context, int viewType);

    // 中间处理下布局问题 - 比如设置下图片一定比例的大小
    protected abstract void resizeHolder(Context context, BaseListViewHolder baseListViewHolder, int viewType);

    // 子类实现对不同的item进行操作
    protected abstract void bindData(Context context, BaseListViewHolder baseListViewHolder, T t, int postion, int itemViewType);

    /**
     * 创建一个监听事件的接口
     */
    public interface OnItemClickListener<T> {
        void onClick(View v, int position, T t, int itemViewType);
    }
}
