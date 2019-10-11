package com.skl.plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * @Author: hl
 * @Date: created at 2019/10/9 18:33
 * @Description: 插件工具类
 */
public class PluginManager {
    private static PluginManager pluginManager = new PluginManager();
    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public static PluginManager getInstance() {
        if (null == pluginManager) {
            pluginManager = new PluginManager();
        }

        return pluginManager;
    }

    /**
     * 加载apk并获取DexClassLoader对象，如果有.so需要考虑第三个参数
     *
     * @param apkName
     * @return
     */
    public Object[] getDexClassLoader(String apkName) {
        // 判断是否存在，不存在则从assert拷贝一份，如果还是不存在则返回null
        // --另外如果存在还可以做各种校验判断是否更新，更新后再使用
        // /data/data/包名/app_dex
        String apkDir = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
        String apkPath = apkDir + "/" + (apkName.contains(".apk") ? apkName : (apkName + ".apk"));
        File file = new File(apkPath);
        if (file.exists()) {
            file.delete();
        }
        apkPath = FileUtil.copyFilesFromAssets(context, apkName, apkDir);
        if (null == apkPath) {
            return null;
        }

        AssetManager assets = null;
        try {
            assets = AssetManager.class.newInstance();
            Method addAssetPath = assets.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assets, apkPath);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return new Object[]{
                new DexClassLoader(apkPath, apkDir, null, context.getClassLoader()),
                new Resources(assets, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration()),
                queryPackageInfo(apkPath)};
    }

    /**
     * 获取插件的Resources
     *
     * @param apkName
     * @return
     */
    public Resources getPluginResources(String apkName) {
        // 判断是否存在，不存在则从assert拷贝一份，如果还是不存在则返回null
        // --另外如果存在还可以做各种校验判断是否更新，更新后再使用
        // /data/data/包名/app_dex
        String apkDir = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
        String apkPath = apkDir + "/" + (apkName.contains(".apk") ? apkName : (apkName + ".apk"));
        File file = new File(apkPath);
        if (file.exists()) {
            file.delete();
        }
        apkPath = FileUtil.copyFilesFromAssets(context, apkName, apkDir);
        if (null == apkPath) {
            return null;
        }
        AssetManager assets = null;
        try {
            assets = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assets, apkPath);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new Resources(assets, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
    }

    private PackageInfo queryPackageInfo(String apkPath) {
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return null;
        }
        return packageInfo;
    }
}

//
//package com.skl.plugin;
//
//        import android.content.Context;
//        import android.content.pm.ActivityInfo;
//        import android.content.pm.PackageInfo;
//        import android.content.pm.PackageManager;
//        import android.content.res.AssetManager;
//        import android.content.res.Resources;
//        import android.os.Build;
//
//        import java.io.File;
//        import java.lang.reflect.InvocationTargetException;
//        import java.lang.reflect.Method;
//
//        import dalvik.system.DexClassLoader;
//
///**
// * @Author: hl
// * @Date: created at 2019/10/9 18:33
// * @Description: 插件工具类
// */
//public class PluginManager {
//    private static PluginManager pluginManager = new PluginManager();
//    private Context context;
//
//    public void setContext(Context context) {
//        this.context = context.getApplicationContext();
//    }
//
//    public static PluginManager getInstance() {
//        if (null == pluginManager) {
//            pluginManager = new PluginManager();
//        }
//
//        return pluginManager;
//    }
//
//    /**
//     * 加载apk并获取DexClassLoader对象，如果有.so需要考虑第三个参数
//     *
//     * @param apkName
//     * @return
//     */
//    public Object[] getDexClassLoader(String apkName) {
//        // 判断是否存在，不存在则从assert拷贝一份，如果还是不存在则返回null
//        // --另外如果存在还可以做各种校验判断是否更新，更新后再使用
//        // /data/data/包名/app_dex
//        String apkDir = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
//        String apkPath = apkDir + "/" + (apkName.contains(".apk") ? apkName : (apkName + ".apk"));
//        File file = new File(apkPath);
////        if (!file.exists())
//        {
//            apkPath = FileUtil.copyFilesFromAssets(context, apkName, apkDir);
//        }
//        if (null == apkPath){
//            return null;
//        }
//        ActivityInfo activityInfo = initializeActivityInfo(apkPath);
//        AssetManager assets = null;
//        try {
//            assets = AssetManager.class.newInstance();
//            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
//            addAssetPath.invoke(assets, apkPath);
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        // Dex加载器
//        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, apkDir,null, context.getClassLoader());
//        if (activityInfo.theme > 0) {
//            context.setTheme(activityInfo.theme);
//        }
//        // 资源
//        Resources resources = new Resources(assets, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
//
//        // 主题
//        Resources.Theme mTheme = resources.newTheme();
//        mTheme.setTo(context.getTheme());
//        try {
//            mTheme.applyStyle(activityInfo.theme, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return new Object[]{ dexClassLoader, resources, mTheme};
//    }
//
//    private ActivityInfo initializeActivityInfo(String dexPath) {
//        ActivityInfo mActivityInfo = null;
//        PackageInfo packageInfo = context.getApplicationContext().getPackageManager().getPackageArchiveInfo(dexPath,
//                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
//        if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {
////            if (mClass == null) {
////                mClass = packageInfo.activities[0].name;
////            }
//
//            //Finals 修复主题BUG
//            int defaultTheme = packageInfo.applicationInfo.theme;
//            for (ActivityInfo a : packageInfo.activities) {
////                if (a.name.equals(mClass)) {
//                mActivityInfo = a;
//                // Finals ADD 修复主题没有配置的时候插件异常
//                if (mActivityInfo.theme == 0) {
//                    if (defaultTheme != 0) {
//                        mActivityInfo.theme = defaultTheme;
//                    } else {
//                        if (Build.VERSION.SDK_INT >= 14) {
//                            mActivityInfo.theme = android.R.style.Theme_DeviceDefault;
//                        } else {
//                            mActivityInfo.theme = android.R.style.Theme;
//                        }
//                    }
////                    }
//                }
//            }
//
//        }
//        return mActivityInfo;
//    }
//
//    /**
//     * 获取插件的Resources
//     * @param apkName
//     * @return
//     */
//    public Resources getPluginResources(String apkName) {
//        // 判断是否存在，不存在则从assert拷贝一份，如果还是不存在则返回null
//        // --另外如果存在还可以做各种校验判断是否更新，更新后再使用
//        // /data/data/包名/app_dex
//        String apkDir = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
//        String apkPath = apkDir + "/" + (apkName.contains(".apk") ? apkName : (apkName + ".apk"));
//        File file = new File(apkPath);
////        if (!file.exists())
//        {
//            apkPath = FileUtil.copyFilesFromAssets(context, apkName, apkDir);
//        }
//        if (null == apkPath){
//            return null;
//        }
//        AssetManager assets = null;
//        try {
//            assets = AssetManager.class.newInstance();
//            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
//            addAssetPath.invoke(assets, apkPath);
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return new Resources(assets, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
//    }
//}

