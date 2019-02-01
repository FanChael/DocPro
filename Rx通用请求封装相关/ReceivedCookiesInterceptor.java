package com.x.finance.net;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;

/**
 * Created by hl on 2018/3/22.
 */

public class ReceivedCookiesInterceptor implements Interceptor{

    private Context context;

    public ReceivedCookiesInterceptor(Context context) {
        super();
        this.context = context;

    }
    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        ///< 每次接收到新的cookie则清楚掉之前的 -- 保证只有一个cookie
        NetUrl.cookies.clear();

        okhttp3.Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            for (String header : originalResponse.headers("Set-Cookie")) {
                Log.e("Cookie", "ReceivedCookiesInterceptor start 22222 header=" + header);
                NetUrl.cookies.add(header);
            }
        }
        return originalResponse;
//        Response originalResponse = chain.proceed(chain.request());
//        //这里获取请求返回的cookie
//        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
//            Log.e("Cookie", "ReceivedCookiesInterceptor start");
//            final StringBuffer cookieBuffer = new StringBuffer();
//            //最近在学习RxJava,这里用了RxJava的相关API大家可以忽略,用自己逻辑实现即可.大家可以用别的方法保存cookie数据
//            Observable.from(originalResponse.headers("Set-Cookie"))
//                    .map(new Func1<String, String>() {
//                        @Override
//                        public String call(String s) {
//                            String[] cookieArray = s.split(";");
//                            return cookieArray[0];
//                        }
//                    })
//                    .subscribe(new Action1<String>() {
//                        @Override
//                        public void call(String cookie) {
//                            cookieBuffer.append(cookie).append(";");
//                        }
//                    });
//            Log.e("Cookie", "ReceivedCookiesInterceptor start");
//            SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("cookie", cookieBuffer.toString());
//            editor.commit();
//        }
//
//        return originalResponse;
    }
}
