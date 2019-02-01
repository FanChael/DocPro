package com.x.finance.net;

import com.x.finance.app.MyApplication;
import com.x.finance.controller.BaseView;
import com.x.finance.tools.NetworkUtil;

import rx.Subscriber;

/**
 * Created by hl on 2018/4/7.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private BaseView baseView;

    private BaseSubscriber(){}
    public BaseSubscriber(BaseView baseView){
        this.baseView = baseView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!NetworkUtil.isNetworkConnected(MyApplication.getInstance())) {
            ///< 取消订阅(后续订阅通知则不再重复发送)
            unsubscribe();
            ///< 发送错误事件(一定要加，因为网络请求可能需要错误处理，比如进度条消失等)
            onError(new Throwable("当前网络不可用!"));
            //baseView.showToast("当前网络不可用，请检查网络情况!");
            //onCompleted();
        }
    }
}
