package com.skl.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/hl/login")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * 登录 + 可以保存数据到本地(采用sharedpreferences的方式存储)
     * TODO 注意：这里为了模拟组件间的接口调用，采用其他方式
     * @param view
     */
    public void logining(View view) {

    }
}
