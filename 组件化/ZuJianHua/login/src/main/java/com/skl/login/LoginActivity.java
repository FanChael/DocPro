package com.skl.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.skl.basemodule.common_class.MessageEvent;
import com.skl.basemodule.common_interface.IDataCallBack;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/hl/login")
public class LoginActivity extends AppCompatActivity {
    @BindView(R2.id.al_loginBtn)
    public Button al_loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        al_loginBtn.setText("验证下Butterknife");
    }

    /**
     * 登录 + 可以保存数据到本地(采用sharedpreferences的方式存储)
     * TODO 注意：这里为了模拟组件间的接口调用，采用其他方式
     *
     * @param view
     */
    public void logining(View view) {
        // -通知个人中心刷方式1. 登录成功后，本地保存登录信息，每次切换到个人中心页面都进行个人信息刷新；解决：采用Sharedpreferences存储
        // --通知个人中心刷方式1.1.下沉到底层模块basemodule里，所有业务组件可依赖，这样就解决了组件之间数据共享的问题;  解决：类似CompomentsService那样定义公共类，提供公共开放静态接口(set/get)...
        // -通知个人中心刷方式2. 主动调用刷新; 解决：其实也是通过扩展IAppComponent接口，但是只能到达组件的XxxxApplication类，还是需要通知到指定页面
        // -TODO 通知个人中心刷方式3. Eventbus通知刷新
        EventBus.getDefault().post(new MessageEvent("login_success", "name:hl,age:22"));
        // -TODO 通知个人中心刷方式4. ARouter路由通信 - 这里路由设置相关信息，个人中心获取进行刷新
        IDataCallBack provider = (IDataCallBack) ARouter.getInstance().build("/login/provider").navigation();
        if (null != provider) {
            provider.setSomething("name:hl,age:22");
        }
        finish();
        // TODO - 这里用登录成功来演示了一下组件的通信，但是要注意的是：
        //        我们登录成功的信息是持久化的，需要shared保存的哈，不然每次重新进入就没了！
    }
}
