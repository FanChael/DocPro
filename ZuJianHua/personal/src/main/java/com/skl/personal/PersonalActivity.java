package com.skl.personal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/ppx/personal")
public class PersonalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
    }

    /**
     * 获取登录页面个人信息并展示
     * @param view
     */
    public void getLoginInfo(View view) {

    }
}
