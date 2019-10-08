package com.skl.login;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.skl.basemodule.common_interface.IDataCallBack;

@Route(path = "/login/provider")
public class LoginProvider implements IDataCallBack {
    private Object content;

    @Override
    public void setSomething(Object obj) {
        this.content = obj;
    }

    @Override
    public Object getSomething() {
        return content;
    }

    @Override
    public void init(Context context) {
        // TODO 可以在init方法中进行一些接口首次被调用时的初始化操作
        Log.e("LoginProvider", "init!");
    }
}
