package com.xxxx.app.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xxxx.app.common.utils.GlideUtil;
import com.xxxx.app.common.utils.HighLightKeyWordUtil;

/**
 * Created by hl on 2018/3/14.
 */

public class BaseMulViewHolder<T extends BaseMulDataModel> extends RecyclerView.ViewHolder {
    private View itemView;
    private SparseArray<View> views = new SparseArray<>();

    public BaseMulViewHolder(View itemView) {
        super(itemView);
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
    public BaseMulViewHolder setText(int viewId, String content) {
        ((TextView) getView(viewId)).setText(content);
        return this;
    }


    /**
     * 设置文本 - 字符串
     *
     * @param viewId
     * @param visibility
     * @return
     */
    public BaseMulViewHolder setVisible(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    /**
     * 设置文本 - 资源
     *
     * @param viewId
     * @param resId
     * @return
     */
    public BaseMulViewHolder setText(int viewId, int resId) {
        ((TextView) getView(viewId)).setText(resId);
        return this;
    }

    /**
     * 设置文本颜色
     *
     * @param viewId
     * @param color
     * @return
     */
    public BaseMulViewHolder setTextColor(int viewId, int color) {
        ((TextView) getView(viewId)).setTextColor(color);
        return this;
    }

    /**
     * 设置文本颜色
     *
     * @param viewId
     * @param colorId
     * @return
     */
    public BaseMulViewHolder setTextColor(Context context, int viewId, int colorId) {
        ((TextView) getView(viewId)).setTextColor(context.getResources().getColor(colorId));
        return this;
    }

    /**
     * 设置可用性
     *
     * @param viewId
     * @param bEnable
     */
    public void setEnable(int viewId, boolean bEnable) {
        getView(viewId).setEnabled(bEnable);
    }

    /**
     * 设置控件背景
     *
     * @param viewId
     * @param drawable
     * @return
     */
    public BaseMulViewHolder setBackground(int viewId, Drawable drawable) {
        getView(viewId).setBackground(drawable);
        return this;
    }

    /**
     * 设置控件背景
     *
     * @param viewId
     * @param viewId
     * @param drawableId
     * @return
     */
    public BaseMulViewHolder setBackground(Context context, int viewId, int drawableId) {
        getView(viewId).setBackground(context.getResources().getDrawable(drawableId));
        return this;
    }

    /**
     * 设置高亮文本 - 独家首发
     *
     * @param viewId
     * @param content
     * @return
     */
    public BaseMulViewHolder setHighLightText(int viewId, String content, String[] highText) {
        ((TextView) getView(viewId)).setText(HighLightKeyWordUtil.getBackgroudKeyWord(
                new int[]{Color.parseColor("#ffffff"), Color.parseColor("#ffffff")},
                new int[]{Color.parseColor("#febc48"), Color.parseColor("#f13b2f")},
                content, highText));
        return this;
    }

    /**
     * 设置高亮文本 - "进行中", "报名中", "已结束"
     *
     * @param viewId
     * @param content
     * @return
     */
    public BaseMulViewHolder setHighLightTextB(int viewId, String content, String[] highText) {
        ((TextView) getView(viewId)).setText(HighLightKeyWordUtil.getBackgroudKeyWord(
                new int[]{Color.parseColor("#ffffff"), Color.parseColor("#ffffff"), Color.parseColor("#ffffff")},
                new int[]{Color.parseColor("#26d191"), Color.parseColor("#febc48"), Color.parseColor("#9f9f9f")},
                content, highText));
        return this;
    }

    /**
     * 设置高亮文本 - 专题
     *
     * @param viewId
     * @param content
     * @return
     */
    public BaseMulViewHolder setHighLightTextTopic(int viewId, String content) {
        ((TextView) getView(viewId)).setText(HighLightKeyWordUtil.getBackgroudKeyWord(
                new int[]{Color.parseColor("#ffffff")},
                new int[]{Color.parseColor("#477ae4")},
                content, new String[]{"猎云专题"}));
        return this;
    }

    /**
     * 设置圆角图片ImageView
     *  - asDrawable()比asBitmap要省
     *  - .skipMemoryCache(true)跳过内存缓存
     *  - .diskCacheStrategy(DiskCacheStrategy.ALL)全部使用磁盘缓存
     *  - .not to do : 裁剪图片大小.override(w,h);
     * @param context
     * @param viewId
     * @param imgUrl
     * @return
     */
    public BaseMulViewHolder setRoundImageView(Context context, int viewId, int placeResource, String imgUrl) {
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .centerCrop()
                        .placeholder(placeResource)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()  // 解决了有时候加载显示被裁剪等问题
                )
                .asDrawable()
                .load(imgUrl)
                .apply(GlideUtil.getRoundRe(context, 4))
                //.placeholder(R.drawable.home_list_img_default)
                //.error(R.mipmap.pic_default)
                .into((ImageView) getView(viewId));
        return this;
    }

    /**
     * 设置图片ImageView
     *  - asDrawable()比asBitmap要省
     *  - .skipMemoryCache(true)跳过内存缓存
     *  - .diskCacheStrategy(DiskCacheStrategy.ALL)全部使用磁盘缓存
     *  - .not to do : 裁剪图片大小.override(w,h);
     * @param context
     * @param viewId
     * @param imgUrl
     * @return
     */
    public BaseMulViewHolder setImageView(Context context, int viewId, int placeResource, String imgUrl) {
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .centerCrop()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(placeResource)
                        .fitCenter()  // 解决了有时候加载显示被裁剪等问题
                )
                .asDrawable()
                .load(imgUrl)
                //.placeholder(R.drawable.home_list_img_default)
                //.error(R.mipmap.pic_default)
                .into((ImageView) getView(viewId));
        return this;
    }

    /**
     * 设置图片ImageView
     *  - asDrawable()比asBitmap要省
     *  - .skipMemoryCache(true)跳过内存缓存
     *  - .diskCacheStrategy(DiskCacheStrategy.ALL)全部使用磁盘缓存
     *  - .not to do : 裁剪图片大小.override(w,h);
     * @param context
     * @param viewId
     * @param imageRourceId
     * @return
     */
    public BaseMulViewHolder setImageView(Context context, int viewId, int placeResource, int imageRourceId) {
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(placeResource)
                )
                .asDrawable()
                .load(imageRourceId)
                //.placeholder(placeResource)
                .into((ImageView) getView(viewId));
        return this;
    }

    //    没有设置缓存策略的情况
    //    /**
    //     * 设置圆角图片ImageView
    //     *
    //     * @param context
    //     * @param viewId
    //     * @param imgUrl
    //     * @return
    //     */
    //    public BaseMulViewHolder setRoundImageView(Context context, int viewId, int placeResource, String imgUrl) {
    //        Glide.with(context)
    //                .setDefaultRequestOptions(new RequestOptions()
    //                        .centerCrop()
    //                        .placeholder(placeResource)
    //                        .fitCenter()  // 解决了有时候加载显示被裁剪等问题
    //                )
    //                .load(imgUrl)
    //                .apply(GlideUtil.getRoundRe(context, 4))
    //                //.placeholder(R.drawable.home_list_img_default)
    //                //.error(R.mipmap.pic_default)
    //                .into((ImageView) getView(viewId));
    //        return this;
    //    }
    //
    //    /**
    //     * 设置图片ImageView
    //     *
    //     * @param context
    //     * @param viewId
    //     * @param imgUrl
    //     * @return
    //     */
    //    public BaseMulViewHolder setImageView(Context context, int viewId, int placeResource, String imgUrl) {
    //        Glide.with(context)
    //                .setDefaultRequestOptions(new RequestOptions()
    //                        .centerCrop()
    //                        .placeholder(placeResource)
    //                        .fitCenter()  // 解决了有时候加载显示被裁剪等问题
    //                )
    //                .load(imgUrl)
    //                //.placeholder(R.drawable.home_list_img_default)
    //                //.error(R.mipmap.pic_default)
    //                .into((ImageView) getView(viewId));
    //        return this;
    //    }
    //
    //    /**
    //     * 设置图片ImageView
    //     *
    //     * @param context
    //     * @param viewId
    //     * @param imageRourceId
    //     * @return
    //     */
    //    public BaseMulViewHolder setImageView(Context context, int viewId, int placeResource, int imageRourceId) {
    //        Glide.with(context)
    //                .load(imageRourceId)
    //                .placeholder(placeResource)
    //                .into((ImageView) getView(viewId));
    //        return this;
    //    }
}
