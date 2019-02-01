package com.lieyunwang.liemine.net;

import com.lieyunwang.liemine.MainApplication;
import com.lieyunwang.liemine.mvp.presenter.base.BaseView;

import com.lieyunwang.liemine.MainApplication;
import com.lieyunwang.liemine.mvp.presenter.base.BaseView;
import com.lieyunwang.liemine.utils.system.NetworkUtil;
import rx.Subscriber;

/**
 * Created by hl on 2018/7/4.
 * 1. 增加具体错误处理回调 - 给需要使用的地方(比如没有重试处理界面的地方)
 */

public abstract class BaseSubscribers<T> extends Subscriber<T> {

    private BaseView baseView;

    private BaseSubscribers() {
    }

    public BaseSubscribers(BaseView baseView) {
        this.baseView = baseView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!NetworkUtil.isNetworkConnected(MainApplication.getInstance())) {
            ///< 取消订阅(后续订阅通知则不再重复发送)
            unsubscribe();
            ///< 发送错误事件(一定要加，因为网络请求可能需要错误处理，比如进度条消失等)
            //onError(new Throwable("当前网络不可用!"));
            onError(new ApiException(ApiException.NO_NET));
            //baseView.showToast("当前网络不可用，请检查网络情况!");
            //onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof Exception) {
            ///< 访问获得对应的Exception
            onErrors(ExceptionHandle.handleException(e));
        } else if (e.getMessage().contains("当前网络不可用")) {
            ///< 将Throwable 和 网络错误的status code返回
            ExceptionHandle.ResponeThrowable responeThrowable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.NO_NETWORK);
            responeThrowable.message = e.getMessage();
            onErrors(responeThrowable);
        } else {
            ///< 将Throwable 和 未知错误的status code返回
            ExceptionHandle.ResponeThrowable responeThrowable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
            responeThrowable.message = e.getMessage();
            onErrors(responeThrowable);
        }
    }

    public abstract void onErrors(ExceptionHandle.ResponeThrowable responeThrowable);
}
