package com.x.finance.controller;

import android.net.Uri;

/**
 * 资讯控制接口【View与Model纽带】
 * Created by hl on 2018/3/13.
 */

public interface UploadPicControlContract {
    /**
     * 负责设置界面
     */
    interface View extends BaseView{
        void uploadSuccess();
    }

    /**
     * 负责请求网络获取数据
     */
    interface Presenter extends BasePresenter{
        /**
         * 上传图片
         * @param uri
         */
        void uploadPic(Uri uri);
    }
}
