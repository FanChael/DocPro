package com.skl.hotfixtest;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchExecutor;
import com.meituan.robust.PatchManipulate;
import com.meituan.robust.RobustCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new PatchExecutor(this, new PatchManipulate() {
            @Override
            protected List<Patch> fetchPatchList(Context context) {
                // TODO 从服务器下载补丁文件哟

                // 从Assets拷贝一个补丁到应用缓存中，然后返回改补丁列表
                String patchPath = SystemUtils.getCacheDirectory(context,
                        Environment.DIRECTORY_DOWNLOADS).getPath() +
                        File.separator + "robust";
                File dir = new File(patchPath);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(patchPath + File.separator + "patch_123.jar");
                if (file.exists()) file.delete();
                if (null == FileUtil.copyFilesFromAssets(context, "patch_123.jar", patchPath, "patch_temp.jar")){
                    Log.e("robust","没有这个path");
                    return new ArrayList<>();
                }

                Patch patch = new Patch();
                patch.setName("patch_temp");
                patch.setLocalPath(patchPath);
                //setPatchesInfoImplClassFullName 设置项各个App可以独立定制，需要确保的是setPatchesInfoImplClassFullName设置的包名是和xml配置项patchPackname保持一致，而且类名必须是：PatchesInfoImpl
                //请注意这里的设置 - 与robust.xml的patchPackname一致
                patch.setPatchesInfoImplClassFullName("com.skl.hotfixtest.patch.PatchesInfoImpl");
                patch.setTempPath(patchPath + File.separator + "patch");

                // 添加到列表并返回
                List<Patch> patchList = new ArrayList<>();
                patchList.add(patch);
                return patchList;
            }

            @Override
            protected boolean verifyPatch(Context context, Patch patch) {
                // TODO 可以加上自己的验证逻辑
                // 简单点，都通过验证
                return true;
            }

            @Override
            protected boolean ensurePatchExist(Patch patch) {
                // TODO 可以加上自己的验证逻辑
                return true;
            }
        }, new RobustCallBack() {
            @Override
            public void onPatchListFetched(boolean result, boolean isNet, List<Patch> patches) {
                Log.e("robust","robust arrived in onPatchListFetched");
            }

            @Override
            public void onPatchFetched(boolean result, boolean isNet, Patch patch) {
                Log.e("robust","robust arrived in onPatchListFetched");
            }

            @Override
            public void onPatchApplied(boolean result, Patch patch) {
                Log.e("robust","robust arrived in onPatchApplied");
            }

            @Override
            public void logNotify(String log, String where) {
                Log.e("robust"," robust arrived in logNotify " + where);
            }

            @Override
            public void exceptionNotify(Throwable throwable, String where) {
                Log.e("robust"," robust arrived in exceptionNotify " + where);
            }
        }).start();
    }

    /**
     * 错误方法调用
     * @param view
     */
    public void getFuction(View view) {
        NeedFixFunction needFixFunction  = new NeedFixFunction();
        needFixFunction.getMaxNumber(10.0f);
    }
}
