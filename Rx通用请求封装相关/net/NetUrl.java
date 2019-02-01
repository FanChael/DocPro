package com.lieyunwang.liemine.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hl on 2018/3/8.
 */

public class NetUrl {

    /**
     * 全局cookie，每次获取先清空列表 - 暂时用的第三方cookiejar
     */
    public static List<String> cookies  = new ArrayList<String>();

    /**
     * 服务地址
     */
    public static final String base_ip = "http://xxx.xxx.xxx.xxx:8088/";
    public static final String base_url = base_ip + "LIFE/v1/";
}
