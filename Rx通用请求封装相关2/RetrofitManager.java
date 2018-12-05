package com.x.finance.net;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.x.finance.app.MyApplication;
import com.x.finance.net.Converter.FastJsonConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by hl on 2018/3/11.
 */

public class RetrofitManager {
    private Retrofit mRetrofit = null;
    private Retrofit mRetrofitCookie = null;
    private volatile static RetrofitManager instance = null;
    private volatile static RetrofitManager instanceCookie = null;
    ///< 利用CookieJar来管理cookie，省的自己去保存设置【有时候自己设置还会涉及到header顺序的问题】
    private ClearableCookieJar cookieJar = null;

    private RetrofitManager(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetUrl.base_url)
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    /**
     * 带Cookie的定义
     * @param bycookie
     */
    private RetrofitManager(int bycookie){
        //        ///< 自定义拦截器
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
        //                .addInterceptor(new ReceivedCookiesInterceptor(MyApplication.getInstance()))
        //                .addInterceptor(new AddCookiesInterceptor(MyApplication.getInstance()))
        //                .connectTimeout(30, TimeUnit.SECONDS).build();
        //
        //        mRetrofit = new Retrofit.Builder()
        //                .baseUrl(NetUrl.base_url)
        //                .addConverterFactory(ScalarsConverterFactory.create())
        //                .addConverterFactory(GsonConverterFactory.create())
        //                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        //                .client(okHttpClient)
        //                .build();

        cookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(MyApplication.getInstance()));

        ///< 定义cookie请求 - 采用开源库PersistentCookieJar
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(30, TimeUnit.SECONDS).build();

        mRetrofitCookie = new Retrofit.Builder()
                .baseUrl(NetUrl.base_url)
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static RetrofitManager getInstance(){
        if (null == instance){
            synchronized (RetrofitManager.class){
                if (null == instance){
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    /**
     * 带Cookie的定义
     * @return
     */
    public static RetrofitManager getInstanceByCookie(){
        if (null == instanceCookie){
            synchronized (RetrofitManager.class){
                if (null == instanceCookie){
                    instanceCookie = new RetrofitManager(0);
                }
            }
        }
        return instanceCookie;
    }

    public Retrofit getmRetrofit(){
        if (null == mRetrofit){
            mRetrofit = RetrofitManager.getInstance().initRetrofit();
        }
        return mRetrofit;
    }

    public Retrofit getmRetrofitByCookie(){
        if (null == mRetrofitCookie){
            mRetrofitCookie = RetrofitManager.getInstanceByCookie().initRetrofitByCookie();
        }
        return mRetrofitCookie;
    }

    private Retrofit initRetrofit() {
        return mRetrofit;
    }

    private Retrofit initRetrofitByCookie() {
        return mRetrofitCookie;
    }

    /**
     * 带Cookie的释放
     */
    public void clearByCookie(){
        if (null != cookieJar){
            cookieJar.clearSession();
            cookieJar.clear();
        }
    }
}