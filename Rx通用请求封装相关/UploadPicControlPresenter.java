package com.x.finance.controller.presenter;

import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.x.finance.app.MyApplication;
import com.x.finance.bean.UserInfoBean;
import com.x.finance.controller.UploadPicControlContract;
import com.x.finance.controller.tool.UserInfoControlPresenterTool;
import com.x.finance.net.BaseSubscriber;
import com.x.finance.net.RetrofitManager;
import com.x.finance.net.service.UploadDownService;
import com.x.finance.tools.SharedPreferencesUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hl on 2018/3/23.
 */

public class UploadPicControlPresenter implements UploadPicControlContract.Presenter{

    private UploadPicControlContract.View view;
    private UploadDownService uploadService;
    private List<Subscription> subscriptionList = new ArrayList<Subscription>();

    public UploadPicControlPresenter(UploadPicControlContract.View view){
        this.view = view;
        uploadService = RetrofitManager.getInstance()
                .getmRetrofit()
                .create(UploadDownService.class);
    }

    @Override
    public void releaseResource() {
        for (int i = 0; i < subscriptionList.size(); ++i){
            if (null != subscriptionList.get(i) && !subscriptionList.get(i).isUnsubscribed()) {
                subscriptionList.get(i).unsubscribe();
            }
        }
        subscriptionList.clear();
    }

    @Override
    public void uploadPic(Uri uri) {
        view.showDialog();
        File file = null;
        try {
            file = new File(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            view.showToast("" + e.getMessage());
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)    ///< 表单类型
                .addFormDataPart("access_token", UserInfoControlPresenterTool.getToken());  ///< ParamKey.TOKEN 自定义参数key常量类，即参数
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/from-data"), file);
        builder.addFormDataPart("file", file.getName(), requestBody);   ///< 多张图片，多个requestbody就行..
        List<MultipartBody.Part> partsBody  = builder.build().parts();

        Subscription subscription = uploadService.upload(partsBody)
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, String>() {
                    @Override
                    public String call(ResponseBody responseBody) {
                        try {
                            return responseBody.string();
                        } catch (IOException e) {
                            /// do nothing
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>(view) {
                    @Override
                    public void onCompleted() {
                        view.disDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //view.showToast("有点小问题，再试下吧!");
                        if (e.getMessage().contains("当前网络不可用")){
                            view.showToast("当前网络不可用");
                        }
                        onCompleted();
                    }

                    @Override
                    public void onNext(String o) {
                        JSONObject jsonObject = JSONObject.parseObject(o);
                        int code = jsonObject.getInteger("code");
                        if (0 == code) {
                            ///< 把最新的头像路径保存起来
                            SharedPreferencesUtil.getInstance(MyApplication.getInstance()).putSP(UserInfoBean.get_user_avatar_key(), jsonObject.getString("data"));
                            view.uploadSuccess();
                        } else {
                            view.showToast(jsonObject.getString("message"));
                        }
                    }
                });
        subscriptionList.add(subscription);
    }
}
