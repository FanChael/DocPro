package com.skl.basemodule;

import com.skl.basemodule.common_interface.IAppComponent;

import java.util.HashMap;

/**
 * 组件初始化时都注册到这个服务里面
 */
public class CompomentsService {
    private static HashMap<AppConfig.PAGE_TYPE, IAppComponent> iAppComponentHashMap = new HashMap<>();

    public static void setiAppComponentHashMap(AppConfig.PAGE_TYPE componentName, IAppComponent iAppComponent){
        iAppComponentHashMap.put(componentName, iAppComponent);
    }

    public static IAppComponent getiAppComponentHashMap(AppConfig.PAGE_TYPE componentName){
        if (iAppComponentHashMap.containsKey(componentName)){
            return iAppComponentHashMap.get(componentName);
        }
        return null;
    }
}
