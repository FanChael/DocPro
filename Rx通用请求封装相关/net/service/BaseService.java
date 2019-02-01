package com.lieyunwang.liemine.net.service;

import com.lieyunwang.liemine.net.HttpResponse;

import java.util.HashMap;

import com.lieyunwang.liemine.mvp.view.user.bean.UserBean;
import com.lieyunwang.liemine.net.HttpResponse;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/*
*@Description: 服务请求方法
*@Author: hl
*@Params:
*  D - data对象、实体类、字符串....
*@Time: 2018/11/21 12:02
*/
public interface BaseService{
    @GET    ///< GET方法 - 获取数据【对象】
    Observable<HttpResponse<String>> getData(@Url String url, @QueryMap HashMap<String, String> paramMap);
    @GET    ///< GET方法 - 获取数据【对象】列表
    Observable<HttpResponse<String>> getDataList(@Url String url,
                                                 @Query("page") int pageNum,
                                                 @Query("access_token") String access_token);
    @GET    ///< GET方法 - 获取数据【对象】列表
    Observable<HttpResponse<String>> getDataList(@Url String url,
                                                 @Query("access_token") String access_token);
    @GET    ///< GET方法 - 获取数据【对象】列表
    Observable<HttpResponse<String>> getDataList(@Url String url,
                                                 @QueryMap HashMap<String, String> paramMap);
    @FormUrlEncoded
    @POST   ///< POST方法 - 获取数据【对象】
    Observable<HttpResponse<String>> postData(@Url String url, @FieldMap HashMap<String, String> paramMap);
    @FormUrlEncoded
    @POST    ///< POST方法 - 获取数据【对象】列表
    Observable<HttpResponse<String>> postDataList(@Url String url,
                                                  @Field("page") int pageNum,
                                                  @Field("access_token") String access_token);
    @FormUrlEncoded
    @POST    ///< POST方法 - 获取数据【对象】列表
    Observable<HttpResponse<String>> postDataList(@Url String url,
                                                  @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST    ///< POST方法 - 获取数据【对象】列表
    Observable<HttpResponse<String>> postDataList(@Url String url, @FieldMap HashMap<String, String> paramMap);
}
