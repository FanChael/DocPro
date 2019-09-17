# DocPro

#### 项目介绍
通用适配器封装 - 还包含了旧版ListView，仅供参考；可以根据具体项目优化甚至有自己的写法都行！

#### 使用说明
使用继承BaseMutilayoutAdapter即可；
如果还在使用ListView则继承BaseListAdapter；
同时还提供了addOnItemClickListener给控件添加点击事件

BaseMulViewHolder提供了很多控件设置相关方法，

使用参考
```Java
   package com.xxxx.app.modules.activitys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xxxx.app.R;
import com.xxxx.app.base.adapter.BaseMulDataModel;
import com.xxxx.app.base.adapter.BaseMulViewHolder;
import com.xxxx.app.base.adapter.BaseMutilayoutAdapter;
import com.xxxx.app.common.utils.DensityUtil;
import com.xxxx.app.common.utils.system.ScreenUtil;
import com.xxxx.app.modules.activitys.bean.ActivitysBean;

import java.util.List;

/**
 * @Author: hl
 * @Date: created at 2019/7/17 18:02
 * @Description: 快讯详情适配器
 */
public class ActivityFragmentAdapter<T extends BaseMulDataModel> extends BaseMutilayoutAdapter {
    public ActivityFragmentAdapter(Context context, List<T> datas) {
        super(context, datas);
    }

    @Override
    protected BaseMulViewHolder getHolder(Context context, ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: // 标题
                return new BaseMulViewHolder<ActivitysBean>(
                        LayoutInflater.from(context).inflate(R.layout.fragment_activity_list_title, parent,
                                false));
            case 1: // 活动
                return new BaseMulViewHolder<ActivitysBean>(
                        LayoutInflater.from(context).inflate(R.layout.fragment_activity_promotion_item, parent,
                                false));
            case 2: // 峰会
                return new BaseMulViewHolder<ActivitysBean>(
                        LayoutInflater.from(context).inflate(R.layout.fragment_activity_fenghui_item, parent,
                                false));
            default:
                return null;
        }
    }

    @Override
    protected void handleHolder(Context context, BaseMulViewHolder baseHolder, int viewType) {
        switch (viewType) {
            case 1: // 活动
                ScreenUtil.setConstraintLayoutWH(baseHolder.getView(R.id.fapi_posterIv), (int) (150 * 3), (int) (90 * 3));
                break;
            case 2: // 峰会
                ScreenUtil.setConstraintLayoutWHNoRatio(baseHolder.getView(R.id.fafli_posterIv), ScreenUtil.SCREEN_WIDTH - DensityUtil.dip2px(30), (40 * (ScreenUtil.SCREEN_WIDTH - DensityUtil.dip2px(30))) / 67);
                break;
        }
    }

    @Override
    protected void bindData(Context context, BaseMulViewHolder baseHolder, BaseMulDataModel baseMulDataModel, int postion, int itemViewType) {
        ActivitysBean activitysBean = (ActivitysBean) baseMulDataModel;

        switch (itemViewType) {
            case 0: // 标题
                baseHolder.setText(R.id.falt_titleName, activitysBean.getTitle());
                break;
            case 1: // 活动
                String resultTitle = activitysBean.getStatus_html();
                resultTitle += "  " + activitysBean.getTitle();
                baseHolder.setHighLightTextB(R.id.fapi_titleTv, resultTitle, new String[]{"进行中", "报名中", "已结束"});
                baseHolder.setText(R.id.fapi_timeTv, activitysBean.getStime());
                baseHolder.setText(R.id.fapi_postionTv, activitysBean.getAddress());
                baseHolder.setImageView(context, R.id.fapi_posterIv, R.drawable.activity_list_default_01, activitysBean.getPoster());
                break;
            case 2: // 峰会
                TextView fllita_joinTv = (TextView) baseHolder.getView(R.id.fafli_joinTv);
                if (activitysBean.getStatus().equals("completed")) {
                    fllita_joinTv.setEnabled(false);
                    fllita_joinTv.setBackground(context.getResources().getDrawable(R.drawable.corners_rectangle_grayb_selected));
                } else if (activitysBean.getStatus().equals("ing")) {
                    fllita_joinTv.setEnabled(false);
                    fllita_joinTv.setBackground(context.getResources().getDrawable(R.drawable.corners_rectangle_grayb_selected));
                } else if (activitysBean.getStatus().equals("will")) {
                    fllita_joinTv.setEnabled(true);
                    fllita_joinTv.setBackground(context.getResources().getDrawable(R.drawable.corners_rectangle_blue_selected));
                }
                fllita_joinTv.setText(activitysBean.getStatus_html());
                baseHolder.setText(R.id.fafli_titleTv, activitysBean.getTitle());
                baseHolder.setText(R.id.fafli_timeTv, activitysBean.getStime());
                baseHolder.setText(R.id.fafli_postionTv, activitysBean.getCity());
                baseHolder.setImageView(context, R.id.fafli_posterIv, R.drawable.activity_list_default_01, activitysBean.getPoster());
                addOnItemClickListener(fllita_joinTv, postion, baseMulDataModel, itemViewType, "buy_ticket");
                break;
        }
        addOnItemClickListener(baseHolder.getItemView(), postion, baseMulDataModel, itemViewType, null);
    }
}

```

结果回调处理参考
```Java
 activityFragmentAdapter.setOnItemClickListener(new BaseMutilayoutAdapter.OnItemClickListener<ActivitysBean>() {
            @Override
            public void onClick(View v, int position, ActivitysBean activitysBean, int itemViewType, Object externParams) {
                switch (itemViewType) {
                    case 0:  // 标题(更多)
                        Bundle bundleL = new Bundle();
                        bundleL.putString("from", activitysBean.getType());
                        bundleL.putString("title", activitysBean.getTitle());
                        toClass(mContext, ActivityListActivity.class, bundleL);
                        break;
                    case 1: // 活动
                        break;
                    case 2: // 峰会
                        // 如果是报名购票的话
                        if (null != externParams && externParams instanceof String &&
                                externParams.equals("buy_ticket")) {
                            toClassByLogined(mContext, new OnLoginedClickEvent() {
                                @Override
                                public void onSuccess() {
                                    buyActivitysBean = activitysBean;
                                    baseControlPresenter
                                            .bindView(TicketBean.class, v,
                                                    new Rect(30, 10, 30, 10),
                                                    new Rect(30, 10, 0, 10))
                                            .addParam("flag", activitysBean.getFlag())
                                            .postDataListData(TicketBean.class, new ResultCallBack<TicketBean>() {
                                            }, -1, true);
                                }
                            });

                        } else {
                            Bundle bundleF = new Bundle();
                            bundleF.putInt("page_id", Integer.parseInt(activitysBean.getId()));
                            bundleF.putString("from", activitysBean.getType());
                            toClass(mContext, FengHuiDetailActivity.class, bundleF);
                        }
                        break;
                }
            }
        });
```

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request