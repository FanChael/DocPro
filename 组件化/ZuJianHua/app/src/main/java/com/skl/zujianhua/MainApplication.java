package com.skl.zujianhua;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.skl.basemodule.AppConfig;
import com.skl.basemodule.common_interface.IAppComponent;

import java.util.Map;

public class MainApplication extends Application implements IAppComponent {
    @Override
    public void onCreate() {
        super.onCreate();
        initialize(this);
    }

    @Override
    public void initialize(Application app) {
        // 遍历所有的组件的Application类，依次用反射的方式实现组件初始化和注册
        for(Map.Entry<AppConfig.PAGE_TYPE, String> entry: AppConfig.COMPONENTS.entrySet())
        {
            try {
                Class classz = Class.forName(entry.getValue());
                Object object = classz.newInstance();
                // 实例化后，调用各个组件的initialize方法（init会实现组件的注册）
                if (object instanceof IAppComponent) {
                    ((IAppComponent) object).initialize(app);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // ARouter路由初始化
        if (BuildConfig.DEBUG) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this); // As early as possible, it is recommended to initialize in the Application
    }

    @Override
    public void launch(Context context, String extra) {
        Intent intent = new Intent(context, MainActivity.class);
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
