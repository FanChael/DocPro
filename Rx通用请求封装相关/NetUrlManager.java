package com.x.finance.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.x.finance.bean.CandyBean;
import com.x.finance.bean.CandyListBean;

import java.util.HashMap;
import java.util.List;

/*
*@Description: 方法url管理+类型转换管理
*@Author: hl
*@Time: 2018/11/22 16:18
*/
public class NetUrlManager {
    private static HashMap<Class, String> classStringHashMap = new HashMap<Class, String>() {
        {
            put(CandyBean.ethAddr.class, "eth-addr");
            put(CandyBean.ethSuccess.class, "receive-candy-detail");
            put(CandyListBean.class, "candy-list");
        }
    };

    private static HashMap<Class, Class> classClassHashMap = new HashMap<Class, Class>() {
        {
            put(CandyBean.ethAddr.class, CandyBean.ethAddr.class);
            put(CandyBean.ethSuccess.class,  CandyBean.ethSuccess.class);
        }
    };

    public static String getFunUrl(Class _class) {
        return classStringHashMap.get(_class);
    }

    public static Object getBean(String strData, Class _class) {
        if (_class.getName().equals(CandyBean.ethAddr.class.getName())){
            return JSON.parseObject(strData, new TypeReference<CandyBean.ethAddr>() {});
        }else if (_class.getName().equals(CandyBean.ethSuccess.class.getName())){
            return JSON.parseObject(strData, new TypeReference<CandyBean.ethSuccess>() {});
        }
        return null;
    }

    public static Object getBeanList(String strData, Class _class) {
        if (_class.getName().equals(CandyListBean.class.getName())){
            return JSON.parseObject(strData, new TypeReference<List<CandyListBean>>() {});
        }
        return null;
    }
}
