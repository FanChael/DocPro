package com.xxxx.app.base.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

/**
*@Author: hl
*@Date: created at 2019/7/17 17:44
*@Description: 基础适配器 - 支持多布局
*/
public abstract class BaseMutilayoutAdapter<T extends BaseMulDataModel> extends RecyclerView.Adapter<BaseMulViewHolder> {
    private WeakReference<Context> contextWeakReference = null;
    private List<T> datas;
    private OnItemClickListener<T> onItemClickListener;

    public BaseMutilayoutAdapter(Context context, List<T> datas) {
        this.contextWeakReference = new WeakReference<>(context);
        this.datas = datas;
    }

    @NonNull
    @Override
    public BaseMulViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        BaseMulViewHolder baseMulViewHolder = getHolder(contextWeakReference.get(), viewGroup, viewType);
        handleHolder(contextWeakReference.get(), baseMulViewHolder, viewType);
        return baseMulViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseMulViewHolder baseMulViewHolder, int postion) {
        bindData(contextWeakReference.get(), baseMulViewHolder, datas.get(postion), postion, getItemViewType(postion));
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getDrawType();
    }

    /**
     * 设置监听接口
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 给控件添加点击事件
     * @param v
     * @param position
     * @param t
     * @param itemViewType
     */
    public void addOnItemClickListener(View v, int position, T t, int itemViewType, Object externParams){
        if (null != v){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemClickListener){
                        onItemClickListener.onClick(v, position, t, itemViewType, externParams);
                    }
                }
            });
        }
    }

    /**
     * 直接回调点击事件
     * @param v
     * @param position
     * @param t
     * @param itemViewType
     */
    public void dispatchOnItemClickListener(View v, int position, T t, int itemViewType, Object externParams){
        if (null != onItemClickListener){
            onItemClickListener.onClick(v, position, t, itemViewType, externParams);
        }
    }

    // 子类实现布局加载返回ViewHolder
    protected abstract BaseMulViewHolder getHolder(Context context, ViewGroup parent, int viewType);

    // 中间处理下布局问题 - 比如设置下图片一定比例的大小
    protected abstract void handleHolder(Context context, BaseMulViewHolder baseHolder, int viewType);

    // 子类实现对不同的item进行操作
    protected abstract void bindData(Context context, BaseMulViewHolder baseHolder, T t, int postion, int itemViewType);

    /**
     * 创建一个监听事件的接口
     */
    public interface OnItemClickListener<T> {
        void onClick(View v, int position, T t, int itemViewType, Object externParams);
    }
}
