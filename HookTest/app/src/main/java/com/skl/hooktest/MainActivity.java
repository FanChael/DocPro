package com.skl.hooktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
*@Author: hl
*@Date: created at 2019/10/16 19:19
*@Description: https://www.jianshu.com/p/74c12164ffca?tdsourcetag=s_pcqq_aiomsg
 * 文章讲的蛮好的，不过新手理解还是需要先搞搞反射这些知识才行。其实代理还好。你就是要知道，怎么反射调方法，获取字段，设置字段等操作.
 *  慢慢熟悉吧！争取多屡屡逻辑这些！
*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.hookClick);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "点击事件继续");
                NotifyUtil.notifyUtil(MainActivity.this);
            }
        });

        // Hook一下这个点击事件，增加点预处理
        // 1.跟踪下setOnClickListener方法都做了啥，关键的监听设置在哪里：
        // public void setOnClickListener(@Nullable OnClickListener l) {
        //        if (!isClickable()) {
        //            setClickable(true);
        //        }
        //        getListenerInfo().mOnClickListener = l;
        //    }
        // static class ListenerInfo {
        //        /**
        //         * Listener used to dispatch click events.
        //         * This field should be made private, so it is hidden from the SDK.
        //         * {@hide}
        //         */
        //        public OnClickListener mOnClickListener;
        // }
        // 1.1 所以我们最终要实现替换getListenerInfo()->ListenerInfo的mOnClickListener为我们自己代理的监听方法

        try {
            Method method = View.class.getDeclaredMethod("getListenerInfo");
            method.setAccessible(true);
            Log.e("test", "method=" + method.getName());
            // 获取Button的ListenerInfo对象mListenerInfo
            Object mListenerInfo = method.invoke(button);

            // 内部类需要使用$分隔
            Class<?> classListenerInfo = Class.forName("android.view.View$ListenerInfo");
            // 获取内部Field mOnClickListener
            Field field = classListenerInfo.getDeclaredField("mOnClickListener");
            // 然后获取Button的ListenerInfo对象mListenerInfo的mOnClickListener变量
            // --这就是真正的拿到了Button的监听回调View.OnClickListener的实例对象
            final View.OnClickListener onClickListener = (View.OnClickListener) field.get(mListenerInfo);

            // 然后准备替换为我们自己的点击事件
            // 1. 创建代理点击对象，然后替换 (这里继承接口实现一个类也可以)
            Object proxyOnClickListener = Proxy.newProxyInstance(this.getClassLoader(),
                    new Class[]{View.OnClickListener.class},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            Toast.makeText(MainActivity.this,
                                    "你点击我嘛，我很烦的！",
                                    Toast.LENGTH_SHORT).show();
                            // 为了保证其点击逻辑，除了插入我们的操作，我们还是要处理正常的调用逻辑
                            return method.invoke(onClickListener, args);
                        }
                    });

            // 2. 然后替换掉Button的点击事件
            field.set(mListenerInfo, proxyOnClickListener);
            // End.当点击的时候就会执行我们代理对象的invoke方法。然后你可以在invoke里面增加自己额外的操作。
            // --甚至你啥都不做，就这么让点击事件失效了，哈哈！
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
