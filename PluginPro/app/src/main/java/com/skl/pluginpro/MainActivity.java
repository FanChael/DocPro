package com.skl.pluginpro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.skl.plugin.PluginManager;
import com.skl.plugin.ProxyActivity;

/**
*@Author: hl
*@Date: created at 2019/10/11 14:45
*@Description: App主页跳转到插件页面
*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化插件管理类的上下文
        PluginManager.getInstance().setContext(this);
    }

    /**
     * 跳转到插件主页面
     * @param view
     */
    public void goPlugin(View view) {
        Intent intent = new Intent(this, ProxyActivity.class);
        intent.putExtra("apkName", "plugina.apk");
        intent.putExtra("className", "com.skl.pluginapk.PluginMainActivity");
        startActivity(intent);
    }
}
