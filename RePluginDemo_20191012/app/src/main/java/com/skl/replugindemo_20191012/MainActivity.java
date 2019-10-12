package com.skl.replugindemo_20191012;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goPlugApk(View view) {
        // I:内置插件方式
        // 1.内置插件可以直接启动
        //        Intent intent = RePlugin.createIntent("com.skl.rppluginapk", "com.skl.rppluginapk.MainActivity");
        //        if (!RePlugin.startActivity(MainActivity.this, intent)) {
        //            Toast.makeText(getBaseContext(), "启动失败", Toast.LENGTH_LONG).show();
        //        }

        // E:外置插件方式 - 针对只有一个测试apk的情况: 测试时删除assets/plugins/xxx.jar，那样就直接去拷贝external/xxx.apk，然后install
        // 1. 首先判断下是否已经安装了外置插件
        // 内置插件也可以判断下是否安装，然后就可以直接启动
        if (RePlugin.isPluginInstalled("com.skl.rppluginapk")) {
            Intent intent = RePlugin.createIntent("com.skl.rppluginapk", "com.skl.rppluginapk.MainActivity");
            if (!RePlugin.startActivity(MainActivity.this, intent)) {
                Toast.makeText(getBaseContext(), "启动失败", Toast.LENGTH_LONG).show();
            }
            Log.e("goPlugApk", "直接启动 com.skl.rppluginapk");
        } else { // 2. 而外置插件每次我们都检查下存在性版本校验之类的，首先需要下载更新等操作
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO 可以从网络下载（关键是校验）；这里我直接拷贝assets目录下的吧
                    // 判断是否存在，不存在则从assert拷贝一份，如果还是不存在则返回null
                    // --另外如果存在还可以做各种校验判断是否更新，更新后再使用
                    // /data/data/包名/app_dex - 不用申请动态存储权限
                    String apkName = "rppluginapk-debug.apk";
                    String apkDir = getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
                    String apkPath = apkDir + "/" + apkName;
                    File file = new File(apkPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    apkPath = FileUtil.copyFilesFromAssets(MainActivity.this,
                            "external/" + apkName, apkDir, apkName);
                    if (null != apkPath) {
                        PluginInfo pi = RePlugin.install(apkPath);
                        if (pi != null) {
                            Intent intent = RePlugin.createIntent("com.skl.rppluginapk", "com.skl.rppluginapk.MainActivity");
                            if (!RePlugin.startActivity(MainActivity.this, intent)) {
                                Toast.makeText(getBaseContext(), "外置插件_启动失败", Toast.LENGTH_LONG).show();
                            }
                            Log.e("goPlugApk", "下载成功后启动 com.skl.rppluginapk");
                        }
                    }
                }
            }).start();
        }
    }
}
