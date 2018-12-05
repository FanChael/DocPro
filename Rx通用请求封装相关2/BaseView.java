package com.x.finance.controller;

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
}
