package com.skl.hotfixtinkertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String APATCH_PATH = "/sdcard/patch_signed.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionTool.checkPermission(this);
    }

    /**
     * 修复方法
     * @param view
     */
    public void fixFun(View view) {
        File file = new File(APATCH_PATH);
        if (file.exists()) {
            TinkerInstaller.onReceiveUpgradePatch(getApplication(), APATCH_PATH);
            Log.i("TAG", "补丁包存在>>>>" + APATCH_PATH);
        } else {
            Log.i("TAG", "补丁包不存在");
        }
    }

    /**
     * 点击方法
     * @param view
     */
    public void clickFun(View view) {
        Toast.makeText(this, "这是补丁包哟，嘻嘻...", Toast.LENGTH_SHORT).show();
    }
}
