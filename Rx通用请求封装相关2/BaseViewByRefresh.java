package com.x.finance.controller;

/**
 * View负责显示数据，被Presenter调用来设置界面，可以是Activity, Fragment，或者View, Dialog
 * Created by hl on 2018/3/13.
 */

public interface BaseViewByRefresh extends BaseView{
    /**
     * 结束下拉刷新
     */
    void finishRefresh();
    /**
     * 结束上拉加载刷新
     */
    void finishLoadMore();
    /**
     * 重置列表
     */
    void resetItemList();

    /**
     * 请求失败
     */
    void onRequestFailer();

    /**
     * 没有数据
     */
    void onNoMoreData();
}
