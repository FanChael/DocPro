package com.lieyunwang.liemine.net;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by hl on 2018/3/22.
 */

public class AddCookiesInterceptor implements Interceptor {
    private Context context;

    public AddCookiesInterceptor(Context context) {
        super();
        this.context = context;

    }
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        ///< @a 由于BEC的问题【百度服务器负载均衡增加了一些cook信息...这个有顺序问题，所以导致获取短信验证码出问题】
        ///< 所以把除了包含frontend的放到最后面，另外一个都放到最前面
        List<String> fontendList = new ArrayList<String>();
        List<String> otherList = new ArrayList<String>();
        for (String cookie : NetUrl.cookies) {
            if (cookie.contains("frontend")){
                fontendList.add(cookie);
            }
            else{
                otherList.add(cookie);
            }
            Log.e("Cookie", "AddCookiesInterceptor: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
            //builder.addHeader("cookie", cookie);
        }
        ///< @b 界面添加顺序导致的无法获取验证码的问题
        for (int i = 0; i < otherList.size(); ++i){
            builder.addHeader("cookie", otherList.get(i));
        }
        for (int i = 0; i < fontendList.size(); ++i){
            builder.addHeader("cookie", fontendList.get(i));
        }
        return chain.proceed(builder.build());
        //        final Request.Builder builder = chain.request().newBuilder();
        //        SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
        //        //最近在学习RxJava,这里用了RxJava的相关API大家可以忽略,用自己逻辑实现即可
        //        Observable.just(sharedPreferences.getString("cookie", ""))
        //                .subscribe(new Action1<String>() {
        //                    @Override
        //                    public void call(String cookie) {
        //                        Log.e("Cookie", "AddCookiesInterceptor start");
        //                        //添加cookie
        //                        builder.addHeader("Cookie", cookie);
        //                    }
        //                });
        //        return chain.proceed(builder.build());
    }
}