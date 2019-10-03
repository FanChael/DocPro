package com.skl.basemodule;

import java.util.HashMap;

public class AppConfig {
    public enum PAGE_TYPE {
        LOGIN, PERSONAL
    }

    /**
     * 组件集合
     */
    public static HashMap<PAGE_TYPE, String> COMPONENTS = new HashMap<PAGE_TYPE, String>() {
        {
            // 登录页面
            put(PAGE_TYPE.LOGIN, "com.skl.login.LoginApplication");
            // 登录页面
            put(PAGE_TYPE.PERSONAL, "com.skl.login.PersonalApplication");
        }
    };
}