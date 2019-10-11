package com.skl.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("MissingSuperCall")
public class PluginBaseActivity extends AppCompatActivity implements PluginInterface {
    // 注意：这里命名为protected，以便于子类使用
    protected Activity appCompatActivity;
    // private PluginApk pluginApk;

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void attachContext(Activity context/*,PluginApk pluginApk*/) {
        this.appCompatActivity = context;
        // this.pluginApk = pluginApk;
    }

    @Override
    public void setContentView(int layoutResID) {
        appCompatActivity.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        appCompatActivity.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        appCompatActivity.setContentView(view, params);
    }

    //    @Override
    //    public Resources getResources() {
    //        return pluginApk.pluginRes;
    //    }
    //
    //    @Override
    //    public String getPackageName() {
    //        return pluginApk.packageInfo.packageName;
    //    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return appCompatActivity.getLayoutInflater();
    }

    @Override
    public Window getWindow() {
        return appCompatActivity.getWindow();
    }

    @Override
    public View findViewById(int id) {
        return appCompatActivity.findViewById(id);
    }

    @Override
    public ClassLoader getClassLoader() {
        return appCompatActivity.getClassLoader();
    }

    @Override
    public WindowManager getWindowManager() {
        return appCompatActivity.getWindowManager();
    }


    @Override
    public ApplicationInfo getApplicationInfo() {
        return appCompatActivity.getApplicationInfo();
    }

    @Override
    public Context getApplicationContext() {
        return appCompatActivity.getApplicationContext();
    }

    @Override
    public MenuInflater getMenuInflater() {
        return appCompatActivity.getMenuInflater();
    }

    @Override
    public Intent getIntent() {
        return appCompatActivity.getIntent();
    }

    @Override
    public void finish() {
        appCompatActivity.finish();
    }

    public void onBackPressed() {
        appCompatActivity.onBackPressed();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onDestroy() {

    }
}