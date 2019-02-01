package com.lieyunwang.liemine.net;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*
*@Description: 获取泛型实体类类型
*@Author: hl
*@Time: 2019/1/18 14:10
*/
public abstract class ResultCallBack<T> {
    public Type mType;

    public ResultCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    public static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superClass = subclass.getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superClass;
        return $Gson$Types
                .canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    //public abstract void onResponse(T response);
}
