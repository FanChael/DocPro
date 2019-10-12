package com.skl.virtualapkdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didi.virtualapk.PluginManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.goPlugin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apkName = "virtualapkpluginapk-release.apk";
                String apkDir = getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
                String apkPath = apkDir + "/" + apkName;
                File file = new File(apkPath);
                if (file.exists()) {
                    file.delete();
                }
                apkPath = FileUtil.copyFilesFromAssets(MainActivity.this,
                        apkName, apkDir, apkName);
                if (null != apkPath) {
                    try {
                        // 判断plugin是否已经加载
                        if (null == PluginManager.getInstance(MainActivity.this).getLoadedPlugin("com.skl.virtualapkpluginapk")) {
                            PluginManager.getInstance(MainActivity.this).loadPlugin(new File(apkPath));
                        }
                        // Given "com.didi.virtualapk.demo" is the package name of plugin APK,
                        // and there is an activity called `MainActivity`.
                        Intent intent = new Intent();
                        intent.setClassName(
                                "com.skl.virtualapkpluginapk",
                                "com.skl.virtualapkpluginapk.PluginPage");
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
