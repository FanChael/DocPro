package com.xxxx.app.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xxxx.app.R;
import com.xxxx.app.common.utils.GlideUtil;
import com.xxxx.app.common.utils.HighLightKeyWordUtil;

/**
 * Created by hl on 2018/3/14.
 */

public class BaseListViewHolder<T extends BaseMulDataModel> {
    private View itemView;
    private SparseArray<View> views = new SparseArray<>();

    public BaseListViewHolder(View itemView) {
        this.itemView = itemView;
    }

    /**
     * 获取视图
     *
     * @return
     */
    public View getItemView() {
        return itemView;
    }

    /**
     * 获取子控件
     *
     * @param id
     * @param <V>
     * @return
     */
    public <V extends View> V getView(int id) {
        View view = views.get(id);
        if (null == view) {
            view = itemView.findViewById(id);
            views.put(id, view);
        }
        return (V) view;
    }

    /**
     * 设置文本 - 字符串
     *
     * @param viewId
     * @param content
     * @return
     */
    public BaseListViewHolder setText(int viewId, String content) {
        ((TextView) getView(viewId)).setText(content);
        return this;
    }

    /**
     * 设置文本 - 资源
     *
     * @param viewId
     * @param resId
     * @return
     */
    public BaseListViewHolder setText(int viewId, int resId) {
        ((TextView) getView(viewId)).setText(resId);
        return this;
    }

    /**
     * 设置高亮文本 - 资源
     *
     * @param viewId
     * @param content
     * @return
     */
    public BaseListViewHolder setHighLightText(int viewId, String content) {
        ((TextView) getView(viewId)).setText(HighLightKeyWordUtil.getBackgroudKeyWord(
                new int[]{Color.parseColor("#ffffff"), Color.parseColor("#ffffff")},
                new int[]{Color.parseColor("#febc48"), Color.parseColor("#f13b2f")},
                content, new String[]{"独家", "首发"}));
        return this;
    }

    /**
     * 设置圆角图片ImageView
     *
     * @param context
     * @param viewId
     * @param imgUrl
     * @return
     */
    public BaseListViewHolder setRoundImageView(Context context, int viewId, String imgUrl) {
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.home_list_img_default)
                        .fitCenter()  // 解决了有时候加载显示被裁剪等问题
                )
                .load(imgUrl)
                .apply(GlideUtil.getRoundRe(context, 4))
                //.placeholder(R.drawable.home_list_img_default)
                //.error(R.mipmap.pic_default)
                .into((ImageView) getView(viewId));
        return this;
    }

    /**
     * 设置图片ImageView
     *
     * @param context
     * @param viewId
     * @param imgUrl
     * @return
     */
    public BaseListViewHolder setImageView(Context context, int viewId, String imgUrl) {
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.home_list_img_default)
                        .fitCenter()  // 解决了有时候加载显示被裁剪等问题
                )
                .load(imgUrl)
                //.placeholder(R.drawable.home_list_img_default)
                //.error(R.mipmap.pic_default)
                .into((ImageView) getView(viewId));
        return this;
    }

    /**
     * 设置图片ImageView
     *
     * @param context
     * @param viewId
     * @param imageRourceId
     * @return
     */
    public BaseListViewHolder setImageView(Context context, int viewId, int imageRourceId) {
        Glide.with(context)
                .load(imageRourceId)
                .into((ImageView) getView(viewId));
        return this;
    }
}
