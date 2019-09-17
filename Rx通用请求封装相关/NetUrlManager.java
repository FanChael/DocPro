package com.xxxx.app.net;

import com.google.gson.Gson;
import com.xxxx.app.base.page.bean.AdvBean;
import com.xxxx.app.base.page.bean.Api1Bean;
import com.xxxx.app.base.page.bean.Api2Bean;

import java.util.HashMap;

/*
 *@Description: 方法url管理+类型转换管理
 *@Author: hl
 *@Time: 2018/11/22 16:18
 */
public class NetUrlManager {
    private static HashMap<Class, String> classStringHashMap = new HashMap<Class, String>() {
        {
            // 极验验证码
            put(Api1Bean.class, "captcha-servlet");
            put(Api2Bean.class, "captcha-validate");
            put(MobileCodeBean.class, "get-mobile-code");
        }
    };

    public static String getFunUrl(Class _class) {
        return classStringHashMap.get(_class);
    }

    /**
     *  更通用的泛型转换
     * @param strData
     * @param _resultCallBack
     * @return
     */
    public static Object getBean(String strData, ResultCallBack _resultCallBack) {
        String typeName = _resultCallBack.mType.toString().replace(" ", "").replaceFirst("class", "");
        if (null == strData || strData.equals("null")){
            return "";
        }
        try {
            return new Gson().fromJson(strData, _resultCallBack.mType);
        } catch (Exception e) {
            // 不是json则直接返回(可能是字符串)
            return strData;
        }
    }

    public static Object getBeanList(String strData, ResultCallBack _resultCallBack, int _pageNum) {
        String typeName = _resultCallBack.mType.toString().replace(" ", "").replaceFirst("class", "");
        if (null == strData || strData.equals("null")){
            return "";
        }
        try {
            return new Gson().fromJson(strData, _resultCallBack.mType);
        } catch (Exception e) {
            // 不是json则直接返回(可能是字符串)
            return strData;
        }
    }
}