package com.skl.plugin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import dalvik.system.DexClassLoader;

/**
*@Author: hl
*@Date: created at 2019/10/9 18:46
*@Description: 代理Activity - 所有需要加载的页面都先跳转到这个代理页面，然后由代理页面创建插件页面PluginActivity，
 * 此时创建的插件就可以跟随代理页面的生命周期，完成加载
 * - 未用AppCompatActivity 目前控件id兼容性有问题！
 *  * 空指针： this.mDecorContentParent = (DecorContentParent)subDecor.findViewById(id.decor_content_parent);
 *  *                     this.mDecorContentParent.setWindowCallback(this.getWindowCallback());
*/
public class ProxyActivity extends Activity {
    // PluginActivity实例对象
    private PluginInterface pluginInterface;
    // apk名称
    private String apkName;
    // 待跳转页面全路径 = 包名+页面名称
    private String className;
    // 插件Dex加载器和Resources
    private DexClassLoader dexClassLoader;
    private Resources resources;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apkName = getIntent().getStringExtra("apkName");
        className = getIntent().getStringExtra("className");
        if (null != apkName && null != className) {
            try {
                // 加载apk获取 DexClassLoader Resources
                Object[] objects = PluginManager.getInstance().getDexClassLoader(apkName);
                this.dexClassLoader = (DexClassLoader) objects[0];
                this.resources = (Resources) objects[1];

                // 通过DexClassLoader加载对应的类
                Class<?> activityClass = dexClassLoader.loadClass(className);
                // 然后创建一个实例对象
                Object newInstance =  activityClass.newInstance();

                // 如果该实例对象实现了PluginInterface接口，则将该代理插件Activity的当前上下文扔过去，同时回调各个生命周期
                if (newInstance instanceof PluginInterface){
                    pluginInterface = (PluginInterface) newInstance;
                    // 将代理Activity的实例上下文传递给插件的Activity
                    pluginInterface.attachContext(this/*,
                            new PluginApk((DexClassLoader) objects[0],
                                    (Resources) objects[1],
                                    (PackageInfo) objects[2])*/);
                    // 创建bundle用来与三方apk传输数据
                    Bundle bundle = new Bundle();
                    // 调用三方Activity的onCreate
                    pluginInterface.onCreate(bundle);
                    Log.e("ProxyActivity", "onCreate=" + pluginInterface.getClass());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 要拿插件的Resources - 不然你可能还是跳转到了当前页的感觉，至少页面样式看起来是那样！
     * 再点击跳转可能就崩溃了（就是你的插件页面是当前页面，一切点击事件都没有）:  Could not find method goPlugin(View) in a parent or ancestor Context for android:onClick
     * @return
     */
    @Override
    public Resources getResources() {
        if (null == resources){
            return super.getResources();
        }
        return resources;
    }

    @Override
    public AssetManager getAssets() {
        if (null == resources){
            return super.getAssets();
        }
        return resources.getAssets();
    }

    @Override
    public Resources.Theme getTheme() {
        return super.getTheme();
    }

    /**
     * 插件页面跳转也是需要代理页面，所以我们重新startActivity方法，跳转到代理页面实现跳转
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        Log.e("ProxyActivity", "startActivity");
        Intent newIntent = new Intent(this, ProxyActivity.class);
        newIntent.putExtra("apkName", intent.getStringExtra("apkName"));
        newIntent.putExtra("className", intent.getStringExtra("className"));
        super.startActivity(newIntent);
    }

    @Override
    protected void onStart() {
        if (null != pluginInterface){
            Log.e("ProxyActivity", "onStart");
            pluginInterface.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (null != pluginInterface){
            Log.e("ProxyActivity", "onResume");
            pluginInterface.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (null != pluginInterface){
            Log.e("ProxyActivity", "onPause");
            pluginInterface.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (null != pluginInterface){
            Log.e("ProxyActivity", "onStop");
            pluginInterface.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        if (null != pluginInterface){
            Log.e("ProxyActivity", "onRestart");
            pluginInterface.onRestart();
        }
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        if (null != pluginInterface){
            Log.e("ProxyActivity", "onDestroy");
            pluginInterface.onDestroy();
        }
        super.onDestroy();
    }
}
