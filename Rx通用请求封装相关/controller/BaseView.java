package com.xxxx.app.net.controller;

/**
 * View负责显示数据，被Presenter调用来设置界面，可以是Activity, Fragment，或者View, Dialog
 * Created by hl on 2018/3/13.
 */

public interface BaseView {
    /**
     * 共有的一些UI操作，比如吐司，进度条显示，消失等
     */
    void showDialog();
    void disDialog();
    void retryDialog();
    void emptyDialog();
    void showToast(String msg);
    void showRunToast(String msg);

    /**
     * 账号过期时回调
     * @param _dataModelclass
     * @param object
     */
    void onLoinOut(Class _dataModelclass, Object object);
    /**
     * 第三方登录的时候，如果未绑定账号，会有这个回调
     * @param _dataModelclass
     * @param code
     */
    void onThirdBind(Class _dataModelclass, int code);
}
