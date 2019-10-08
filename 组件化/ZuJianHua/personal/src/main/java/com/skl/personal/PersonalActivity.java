package com.skl.personal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.skl.basemodule.common_interface.IDataCallBack;

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
        IDataCallBack provider = (IDataCallBack) ARouter.getInstance().build("/login/provider").navigation();
        if (null != provider){
            ((TextView)view).setText("" + provider.getSomething());
        }
    }
}
