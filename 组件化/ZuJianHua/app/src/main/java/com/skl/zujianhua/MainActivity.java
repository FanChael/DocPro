package com.skl.zujianhua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 跳转到登录页面
     *
     * @param view
     */
    public void GoLogin(View view) {
        // 跳转方式1： 通过反射
//        try {
//            Class clazz = Class.forName("com.skl.login.LoginActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);
//            // startActivityForResult(intent, 110);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        // 跳转方式2：每个页面都注册到base模块，由base模块提供跳转服务
//        CompomentsService.getiAppComponentHashMap(AppConfig.PAGE_TYPE.LOGIN).launch(this, null);

        // 跳转方式3：采用URI方式
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("activity://login")));

        // 跳转方式4：采用 Arouter
        ARouter.getInstance().build("/hl/login")
                // .withString("nothing", "Gone with the Wind")
                // .withObject("author", Object)
                .navigation(this, 110);
    }

    /**
     * 跳转到个人中心
     *
     * @param view
     */
    public void GoPersonal(View view) {
        // 跳转方式4：采用 Arouter
        ARouter.getInstance().build("/ppx/personal")
                // .withString("nothing", "Gone with the Wind")
                // .withObject("author", Object)
                .navigation(this, 110);
    }
}
