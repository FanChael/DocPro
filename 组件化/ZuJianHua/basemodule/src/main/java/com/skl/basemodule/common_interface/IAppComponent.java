package com.skl.basemodule.common_interface;

import android.app.Application;
import android.content.Context;

/**
 * 组件Application初始化时需要实现的接口
 */
public interface IAppComponent {
    void initialize(Application app);
    void launch(Context context, String extra);
}
