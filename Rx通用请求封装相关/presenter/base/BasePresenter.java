package com.lieyunwang.liemine.mvp.presenter.base;

/**
 * Presenter沟通View和Model，从Model检索数据后，返给View层.
 * Created by hl on 2018/3/13.
 */

public interface BasePresenter {
    /**
     * 共有一些操作，比如释放资源
     */
    public void releaseResource();
}
