package com.skl.plugin;

import android.content.pm.PackageInfo;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

public class PluginApk {
    public PackageInfo packageInfo;
    public DexClassLoader classLoader;
    public Resources pluginRes;

    public PluginApk(DexClassLoader classLoader, Resources pluginRes, PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
        this.classLoader = classLoader;
        this.pluginRes = pluginRes;
    }
}
