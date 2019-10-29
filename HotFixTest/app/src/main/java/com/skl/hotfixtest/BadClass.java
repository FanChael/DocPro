package com.skl.hotfixtest;

import android.util.Log;

public class BadClass {
    // 这里就可以添加Modify，然后重写修复方法，然后切换到apply plugin: 'auto-patch-plugin'，进行assembleRelease打包
    //    @Modify
    public static void badMethod(){
        Log.e("test", "我是一个坏方法");
    }
}
