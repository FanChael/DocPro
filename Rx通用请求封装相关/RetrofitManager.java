package com.xxxx.app.net;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.xxxx.app.app.MyApplication;
import com.xxxx.app.common.app.UserInfoManager;
import com.xxxx.app.net.converter.FastJsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * Created by hl on 2018/3/11.
 */

public class RetrofitManager {
    private Retrofit mRetrofit = null;
    private volatile static RetrofitManager instance = null;
    ///< 利用CookieJar来管理cookie，省的自己去保存设置【有时候自己设置还会涉及到header顺序的问题】
    private ClearableCookieJar cookieJar = null;

    /**
     * 带Cookie的定义
     */
    private RetrofitManager(){
        cookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(MyApplication.getInstance()));

        ///< 定义cookie请求 - 采用开源库PersistentCookieJar
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // header添加token等信息
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();
                        if (!UserInfoManager.getToken().equals("")){
                            requestBuilder.header("access-token", UserInfoManager.getToken());
                        }
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .cookieJar(cookieJar)
                .connectTimeout(30, TimeUnit.SECONDS).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetUrl.base_url)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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

    public Retrofit getmRetrofit(){
        if (null == mRetrofit){
            mRetrofit = RetrofitManager.getInstance().initRetrofit();
        }
        return mRetrofit;
    }

    private Retrofit initRetrofit() {
        return mRetrofit;
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