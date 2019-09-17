package com.xxxx.app.net;

import io.reactivex.functions.Function;

/*
 *@Description: 请求数据封装 - 增加中间处理过程(可以选择处理或者直接返回)
 *@Author: hl
 *@Time: 2018/9/29 15:38
 * W - 表示输入的数据，也就是请求获得的data数据(对象，字符串等格式)
 * T - 表示返回的数据，最终到onNext(Ojbect o)
 * W输入数据通过回调CallMe.onCall可以进行中间处理过程，然后返回T
 */
public class ResponseFunc<T, W> implements Function<HttpResponse<W>, T> {
    private CallMe<T, W> callMe;

    public ResponseFunc(CallMe<T, W> callMe) {
        this.callMe = callMe;
    }

    @Override
    public T apply(HttpResponse<W> wHttpResponse) {
        if (0 == wHttpResponse.getCode()) {
            ///< 成功
            return callMe.onCall(wHttpResponse.getData(), wHttpResponse.getRequest_time());
        } else if (2 == wHttpResponse.getCode()) {
            ///< Token过期
            throw new ApiException(ExceptionHandle.ERROR.TOKEN, wHttpResponse.getMessage());
        } else if (11 == wHttpResponse.getCode()) {
            ///< 需要绑定账号(第三方登录)
            throw new ApiException(ExceptionHandle.ERROR.THIRD_BIND, wHttpResponse.getMessage());
        }
        else {
            ///< 其他情况 - 返回真实的message信息
            throw new ApiException(ExceptionHandle.ERROR.REQUEST_ERROR, wHttpResponse.getMessage());
        }
    }

    public interface CallMe<T, W> {
        public T onCall(W data, String requestTime);
    }
}
