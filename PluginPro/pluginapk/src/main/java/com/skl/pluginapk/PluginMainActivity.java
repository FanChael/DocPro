package com.skl.pluginapk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skl.plugin.PluginBaseActivity;

/**
 * @Author: hl - 封装到Base页面，不然每次新页面都需要重写一堆方法
 * @Date: created at 2019/10/10 10:20
 * @Description: 插件模块的主页面
 */
public class PluginMainActivity extends PluginBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button button = appCompatActivity.findViewById(R.id.btnTestA);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("apkName", "plugina.apk");
                intent.putExtra("className", "com.skl.pluginapk.PluginMainActivity");
                appCompatActivity.startActivity(intent);
            }
        });
    }
}

//package com.skl.pluginapk;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.pm.ApplicationInfo;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//
//import com.skl.plugin.PluginInterface;
//
///**
// *@Author: hl
// *@Date: created at 2019/10/10 10:20
// *@Description: 插件模块的主页面
// */
//@SuppressLint("MissingSuperCall")
//public class PluginMainActivity extends AppCompatActivity implements PluginInterface {
//    // 注意：这里命名为protected，以便于子类使用
//    protected AppCompatActivity appCompatActivity;
//
//    @Override
//    public void onCreate(Bundle saveInstance) {
//        Log.e("PluginMainActivity", "onCreate");
//        setContentView(R.layout.activity_login);
//        Button button = appCompatActivity.findViewById(R.id.btn);
//        Log.e("PluginMainActivity", "onCreate button = " + button);
//    }
//
//    @Override
//    public void attachContext(AppCompatActivity context) {
//        Log.e("PluginMainActivity", "attachContext");
//        this.appCompatActivity = context;
//    }
//
//    @Override
//    public void setContentView(int layoutResID) {
//        Log.e("PluginMainActivity", "setContentView layoutResID");
//        appCompatActivity.setContentView(layoutResID);
//    }
//
//    @Override
//    public void setContentView(View view) {
//        Log.e("PluginMainActivity", "setContentView view");
//        appCompatActivity.setContentView(view);
//    }
//
//    @Override
//    public void setContentView(View view, ViewGroup.LayoutParams params) {
//        Log.e("PluginMainActivity", "setContentView view params");
//        appCompatActivity.setContentView(view, params);
//    }
//
//    @Override
//    public LayoutInflater getLayoutInflater() {
//        Log.e("PluginMainActivity", "getLayoutInflater");
//        return appCompatActivity.getLayoutInflater();
//    }
//
//    @Override
//    public Window getWindow() {
//        Log.e("PluginMainActivity", "getWindow");
//        return appCompatActivity.getWindow();
//    }
//
//    @Override
//    public View findViewById(int id) {
//        Log.e("PluginMainActivity", "findViewById");
//        return appCompatActivity.findViewById(id);
//    }
//
//    @Override
//    public ClassLoader getClassLoader() {
//        Log.e("PluginMainActivity", "ClassLoader");
//        return appCompatActivity.getClassLoader();
//    }
//
//    @Override
//    public WindowManager getWindowManager() {
//        Log.e("PluginMainActivity", "getWindowManager");
//        return appCompatActivity.getWindowManager();
//    }
//
//
//    @Override
//    public ApplicationInfo getApplicationInfo() {
//        Log.e("PluginMainActivity", "getApplicationInfo");
//        return appCompatActivity.getApplicationInfo();
//    }
//
//    @Override
//    public void finish() {
//        Log.e("PluginMainActivity", "finish");
//        appCompatActivity.finish();
//    }
//
//    public void onBackPressed() {
//        Log.e("PluginMainActivity", "onBackPressed");
//        appCompatActivity.onBackPressed();
//    }
//
//    @Override
//    public void onStart() {
//        Log.e("PluginMainActivity", "onStart");
//    }
//
//    @Override
//    public void onResume() {
//        Log.e("PluginMainActivity", "onResume");
//    }
//
//    @Override
//    public void onPause() {
//        Log.e("PluginMainActivity", "onPause");
//    }
//
//    @Override
//    public void onStop() {
//        Log.e("PluginMainActivity", "onStop");
//    }
//
//    @Override
//    public void onRestart() {
//        Log.e("PluginMainActivity", "onRestart");
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.e("PluginMainActivity", "onDestroy");
//    }
//
//    /**
//     * 跳转到下一个页面 - 同样也只能采用代理的方式
//     * @param view
//     */
//    public void goNextPage(View view) {
//        Intent intent = new Intent();
//        intent.putExtra("apkName", "plugin_1.0.apk");
//        intent.putExtra("className", "com.skl.pluginapk.PluginSecondActivity");
//        appCompatActivity.startActivity(intent);
//    }
//}