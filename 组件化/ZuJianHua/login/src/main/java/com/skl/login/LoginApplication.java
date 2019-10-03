package com.skl.login;

/**
 * 单独运行时需要的话可以配置到AndroidManifest，不需要就不要配置，不然运行报错
 */

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.skl.basemodule.AppConfig;
import com.skl.basemodule.CompomentsService;
import com.skl.basemodule.common_interface.IAppComponent;

public class LoginApplication extends Application implements IAppComponent {
    /**
     * 单独作为Application时会走该方法
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO 单独运行时没有需要可以不用调用
        initialize(this);
    }

    /**
     * App的Application注册组件时会调用initialize方法!
     *
     * @param app
     */
    @Override
    public void initialize(Application app) {
        // 注册自己到组件服务
        CompomentsService.setiAppComponentHashMap(AppConfig.PAGE_TYPE.LOGIN, this);
    }

    /**
     * 启动自己
     *
     * @param context
     * @param extra
     */
    @Override
    public void launch(Context context, String extra) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bundle = new Bundle();
            if (null != extra && !extra.equals(""))
                bundle.putString("extra_data", extra);
            context.startActivity(intent, bundle);
        } else {
            if (null != extra && !extra.equals(""))
                intent.putExtra("extra_data", extra);
            context.startActivity(intent);
        }
    }
}
