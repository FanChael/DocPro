package com.xxxx.app.net.service;

import com.xxxx.app.net.HttpResponse;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import io.reactivex.Observable;

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
    @GET    ///< GET方法 - 获取数据【对象】
    Observable<HttpResponse<String>> getData(@Url String url, @QueryMap HashMap<String, String[]> paramMapA, @QueryMap HashMap<String, String> paramMap);
    @GET    ///< GET方法 - 获取数据【对象】
    Observable<HttpResponse<String>> getDataMultype(@Url String url, @QueryMap HashMap<String, Object> paramMap);
    @GET    ///< GET方法 - 获取数据【对象】
    Observable<HttpResponse<String>> getData(@Url String url, @Part List<MultipartBody.Part> files);
    @GET    ///< GET方法 - 获取数据【对象】
    Observable<String> getDataStr(@Url String url, @QueryMap HashMap<String, String> paramMap);
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
    @GET    ///< GET方法 - 获取数据【对象】列表 - 额外增加了Header字段(动态添加Header)
    Observable<HttpResponse<String>> getDataList(@Url String url,
                                                 @HeaderMap HashMap<String, String> headers,
                                                 @QueryMap HashMap<String, String> paramMap);
    @FormUrlEncoded
    @POST   ///< POST方法 - 获取数据【对象】
    Observable<HttpResponse<String>> postData(@Url String url, @FieldMap HashMap<String, String> paramMap);
    @FormUrlEncoded
    @POST   ///< POST方法 - 带数组参数传递
    Observable<HttpResponse<String>> postData(@Url String url, @FieldMap HashMap<String, String[]> paramMapA, @FieldMap HashMap<String, String> paramMap);
    @FormUrlEncoded
    @POST   ///< POST方法 - 无参 - 获取数据【对象】
    Observable<String> postDataStr(@Url String url, @FieldMap HashMap<String, String> paramMap);
    @Multipart
    @POST   ///< POST方法 - 上传文件
    Observable<HttpResponse<String>> postData(@Url String url, @Part List<MultipartBody.Part> files);
    @FormUrlEncoded
    @POST   ///< POST方法 - 获取数据【对象】 - 参数为多类型，包含数组参数
    Observable<HttpResponse<String>> postDataMultype(@Url String url, @FieldMap HashMap<String, Object> paramMap);

    @FormUrlEncoded
    @POST    ///< POST方法 - 获取数据【对象】列表
    Observable<HttpResponse<String>> postDataList(@Url String url, @FieldMap HashMap<String, String> paramMap);
    @Streaming                      ///< 注明为流文件，防止retrofit将大文件读入内存
    @GET                            ///< 图片文件下载
    Observable<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);   ///< 通过@Url覆盖baseurl
}
