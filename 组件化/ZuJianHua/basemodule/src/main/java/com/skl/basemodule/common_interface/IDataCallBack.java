package com.skl.basemodule.common_interface;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
*@Author: hl
*@Date: created at 2019/10/8 14:14
*@Description: 需要继承IProvider接口才行！！！
*/
public interface IDataCallBack extends IProvider {
    void setSomething(Object obj);
    Object getSomething();
}
