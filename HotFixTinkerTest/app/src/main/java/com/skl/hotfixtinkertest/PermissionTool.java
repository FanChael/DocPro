package com.skl.hotfixtinkertest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by hl on 2018/3/15.
 */

/**
 * 权限管理工具
 */
public class PermissionTool {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_ALL = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //            Manifest.permission.ACCESS_FINE_LOCATION,
            //            Manifest.permission.CALL_PHONE,
            //            Manifest.permission.READ_LOGS,
            //            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //            Manifest.permission.SET_DEBUG_APP,
            //            Manifest.permission.SYSTEM_ALERT_WINDOW,
            //            Manifest.permission.GET_ACCOUNTS,
            //            Manifest.permission.WRITE_APN_SETTINGS
            Manifest.permission.CAMERA
    };
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };

    /**
     * 动态申请权限(读写权限)
     *
     * @param context
     */
    public static void checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            ///< 检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                ///< 用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission
                                .WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(context, "请开通相关权限，否则有些功能无法正常使用！", Toast.LENGTH_SHORT).show();
                }
                ///< 申请权限
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        PERMISSIONS_ALL,
                        REQUEST_EXTERNAL_STORAGE
                );

            } else {
                //Toast.makeText(context, "授权成功！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
