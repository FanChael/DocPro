package com.x.finance.net;

import com.x.finance.anotation.NotProguard;

/*
*@Description: 基础网络数据封装
*@Author: hl
*@Time: 2018/9/27 16:16
*/
@NotProguard
public class HttpResponse<T> {
    private int code;
    private String message;
    private String request_time;
    private T data;

    public HttpResponse(){}
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "status=" + code +
                ", msg='" + message + '\'' +
                ", data=" + data +
                ", data=" + request_time +
                '}';
    }
}
