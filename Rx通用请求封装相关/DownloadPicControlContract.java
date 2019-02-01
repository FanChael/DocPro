package com.x.finance.controller;

/**
 * 图片下载控制接口【View与Model纽带】
 * Created by hl on 2018/5/13.
 */

public interface DownloadPicControlContract {
    /**
     * 负责设置界面
     */
    interface View extends BaseView{
        void downloadSuccess(String _file, Object externObj);
        void downloadFailer(String _message);
    }

    /**
     * 负责请求网络获取数据
     */
    interface Presenter extends BasePresenter{
        /**
         * 下载图片
         * @param url
         */
        void downloadPic(String url, Object externObj);
        /**
         * 下载图片
         * @param url
         */
        void downloadPic(String url, boolean bIsShowDialog);
    }
}
