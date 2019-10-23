package com.skl.hooktest;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class NotifyHookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_hook);
    }

    /**
     * Hook通知
     * @param view
     */
    public void hook(View view) {

        // 跟踪下notificationManager.notify的调用流程;
        // 最终调用的是INotificationManager的enqueueNotificationWithTag方法，
        // --这就涉及到Android Service通信(Binder机制)的相关知识了。
        //        1.
        //        public void notify(int id, @NonNull Notification notification) {
        //            this.notify((String)null, id, notification);
        //        }
        //
        //        2.
        //        public void notify(@Nullable String tag, int id, @NonNull Notification notification) {
        //            ....
        //            this.mNotificationManager.notify(tag, id, notification);
        //        }
        //
        //        3.
        //        public void notify(String tag, int id, Notification notification)
        //        {
        //            notifyAsUser(tag, id, notification, mContext.getUser());
        //        }
        //
        //        4.
        //        /**
        //         * @hide
        //         */
        //        public void notifyAsUser(String tag, int id, Notification notification, UserHandle user)
        //        {
        //            INotificationManager service = getService();
        //            ....
        //            service.enqueueNotificationWithTag(pkg, mContext.getOpPackageName(), tag, id,
        //                    copy, user.getIdentifier());
        //        }
        //
        //        5.
        //        /** @hide */
        //        static public INotificationManager getService()
        //        {
        //            if (sService != null) {
        //                return sService;
        //            }
        //            IBinder b = ServiceManager.getService("notification");
        //            sService = INotificationManager.Stub.asInterface(b);
        //            return sService;
        //        }

        /**sService只创建一次，INotificationManager是接口，到这里我们就可以Hook替换掉这个sService*/
        // 老版本api可以用这个方法获取NotificationManager
        // NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
        // 内部最终还是会获取NotificationManager
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        try {
            // 1. 通过反射获取NotificationManager对象，新版本可以这样。其实也可以直接如下获取：
            // -- (NotificationManager)this.mContext.getSystemService("notification");
            Field mNotificationManager = NotificationManagerCompat.class.getDeclaredField("mNotificationManager");
            mNotificationManager.setAccessible(true);
            final NotificationManager notificationManager1 = (NotificationManager) mNotificationManager.get(notificationManagerCompat);

            // 2. 获取将要被代理对象替换的INotificationManager变量- 实际是一个IxxxxService实例对象(该对象可以调用服务对应的方法)
            Field sService = NotificationManager.class.getDeclaredField("sService");
            sService.setAccessible(true);

            // 3. 创建一个代理接口对象 - 针对INotificationManager接口做代理
            Class iNotiMngClz = Class.forName("android.app.INotificationManager");
            final Object iNotiMngClzLast = sService.get(notificationManager1);
            Object proxyNotificationManager = Proxy.newProxyInstance(getClassLoader(), new Class[]{iNotiMngClz}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Log.e("test", "通知被拦截了！");
                    // 为了正常运行，我们还是执行下人家的通知吧！
                    return method.invoke(iNotiMngClzLast, args);
                    // return null;
                }
            });

            // 4. 替换NotificationManager的sService
            sService.set(notificationManager1, proxyNotificationManager);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送通知（支持8.0+）
     * @param view
     */
    public void nofify(View view) {
        NotifyUtil.notifyUtil(this);
    }

    /**
     * 跳转到其他页面，发送通知； 验证下Hook是不是App全局有效；
     * 答案：是的
     * 因为：我们的context获取的通知服务时针对当前App的，不会干扰到其他App，系统的更不可能了。
     * TODO 下一步：我们去跟踪下获取通知服务的源码流程，应该就大概知道原因了....
     * @param view
     */
    public void goNextPage(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
