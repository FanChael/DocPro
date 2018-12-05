package com.lieyunwang.finance.net.service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by hl on 2018/3/23.
 */

/**
 * 用户信息相关
 */
public interface UploadDownService {
    @Multipart
    @POST("upload-user-head")
    Observable<ResponseBody> upload(@Part List<MultipartBody.Part> imgs);
    /****文件下载部分****/
    @Streaming     ///< 注明为流文件，防止retrofit将大文件读入内存
    @GET
    Observable<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);    ///< 通过@Url覆盖baseurl
}
