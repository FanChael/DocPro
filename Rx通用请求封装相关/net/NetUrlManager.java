package com.lieyunwang.liemine.net;

import com.alibaba.fastjson.JSON;
import com.lieyunwang.liemine.mvp.view.user.bean.UserLoginBean;
import com.lieyunwang.liemine.mvp.view.user.bean.UserRegisterBean;

import java.util.HashMap;

import com.lieyunwang.liemine.mvp.view.user.bean.UserLoginBean;
import com.lieyunwang.liemine.mvp.view.user.bean.UserRegisterBean;

/*
*@Description: 方法url管理+类型转换管理
*@Author: hl
*@Time: 2018/11/22 16:18
*/
public class NetUrlManager {
    private static HashMap<Class, String> classStringHashMap = new HashMap<Class, String>() {
        {
            put(UserRegisterBean.class, "register");
            put(UserLoginBean.class, "login");
        }
    };

    public static String getFunUrl(Class _class) {
        return classStringHashMap.get(_class);
    }

    /**
     *  更通用的泛型转换 - 对象/非列表
     * @param strData
     * @param _resultCallBack
     * @return
     */
    public static Object getBean(String strData, ResultCallBack _resultCallBack) {
        String typeName = _resultCallBack.mType.toString().replace(" ", "").replaceFirst("class", "");
        //if (typeName.equals(TestBean.class.getName())){  ///< com.generic.test.TestBean
        //
        //}
        //Gson gson = new Gson();
        //return gson.fromJson(strData, _resultCallBack.mType);
        return JSON.parseObject(strData, _resultCallBack.mType);
    }

    /**
     * 获取json对象列表
     * @param strData
     * @param _resultCallBack
     * @param _pageNum
     * @return
     */
    public static Object getBeanList(String strData, ResultCallBack _resultCallBack, int _pageNum) {
        String typeName = _resultCallBack.mType.toString().replace(" ", "").replaceFirst("class", "");
        //if (typeName.contains(TestBean.class.getName())){ ///< java.util.List<com.generic.test.TestBean>
        //
        //}
        return JSON.parseObject(strData, _resultCallBack.mType);
    }
}
