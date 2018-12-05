package com.x.finance.controller.presenter;

import android.os.Environment;

import com.bumptech.glide.request.target.Target;
import com.x.finance.app.MyApplication;
import com.x.finance.controller.DownloadPicControlContract;
import com.x.finance.net.RetrofitManager;
import com.x.finance.net.service.UploadDownService;
import com.x.finance.tools.SDFileHelper;
import com.x.finance.tools.SystemUtils;
import com.x.finance.ui.GlideApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;

/*
 *@Description: Glide下载图片的方式+retrofit的方式
 *@Author: hl
 *@Time: 2018/10/26 10:17
 */
public class DownloadPicControlPresenter implements DownloadPicControlContract.Presenter {

    private DownloadPicControlContract.View view;
    private UploadDownService uploadDownService;
    private List<Subscription> subscriptionList = new ArrayList<Subscription>();

    public DownloadPicControlPresenter(DownloadPicControlContract.View view) {
        this.view = view;
        this.uploadDownService = RetrofitManager.getInstance()
                .getmRetrofit()
                .create(UploadDownService.class);
    }

    @Override
    public void releaseResource() {
        for (int i = 0; i < subscriptionList.size(); ++i) {
            if (null != subscriptionList.get(i) && !subscriptionList.get(i).isUnsubscribed()) {
                subscriptionList.get(i).unsubscribe();
            }
        }
        subscriptionList.clear();
    }

    @Override
    public void downloadPic(final String url, final Object externObj) {
        ///< Object Integer是一个状态码，目前我们就返回1吧；这个不一定是integer类型，你可以定义为字符串等
        Subscription subscription = Observable.create(new SyncOnSubscribe<Integer, File>() {
            @Override
            protected Integer generateState() {
                return 1;
            }

            @Override
            protected Integer next(Integer state, Observer<? super File> observer) {
                try {
                    File file = GlideApp
                            .with(MyApplication.getInstance())
                            .downloadOnly()
                            .load(url)
                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get() // needs to be called on background thread
                            ;
                    observer.onNext(file);
                    observer.onCompleted();
                } catch (InterruptedException e) {
                    observer.onError(e);
                } catch (ExecutionException e) {
                    observer.onError(e);
                }
                return state;
            }
        }).subscribeOn(Schedulers.io())
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        if (null == file) {
                            return null;
                        }
                        /**
                         * 重新拼接图片名称，因为glide加密了文件名，然后拷贝一份文件即可!
                         */
                        String pngName = file.getAbsolutePath().
                                substring(0, file.getAbsolutePath().lastIndexOf(".")) +
                                url.substring(url.lastIndexOf("."));
                        ///< 拷贝一个文件
                        if (SDFileHelper.copyFileIfNotExsit(file.getAbsolutePath(), pngName)) {
                            return pngName;
                        }
                        return null;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.downloadSuccess(null, externObj);
                    }

                    @Override
                    public void onNext(String src) {
                        view.downloadSuccess(src, externObj);
                    }
                });
        subscriptionList.add(subscription);
    }

    @Override
    public void downloadPic(final String url, final boolean bIsShowDialog) {
        if (bIsShowDialog) {
            view.showDialog();
        }
        Subscription subscriptionNext = uploadDownService.downloadFileWithDynamicUrlSync(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, String>() {
                    @Override
                    public String call(ResponseBody responseBody) {
                        ///< 下载后的保存路径
                        String picPath = SystemUtils.getCacheDirectory(MyApplication.getInstance(),
                                Environment.DIRECTORY_DOWNLOADS) + "/" +
                                Calendar.getInstance().getTimeInMillis() + url.substring(url.lastIndexOf("."));
                        if (SDFileHelper.writeFile(responseBody.byteStream(), picPath)) {
                            return picPath;
                        }
                        return null;
                    }
                })
                //.observeOn(Schedulers.computation()) ///< 用于计算任务
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (bIsShowDialog) {
                            view.retryDialog();
                        } else {
                            view.downloadFailer((null == e || null == e.getMessage()) ? "" : e.getMessage());
                            view.showToast("获取分享信息出错!");
                        }
                    }

                    @Override
                    public void onNext(String filePath) {
                        if (null == filePath) {
                            if (bIsShowDialog) {
                                view.retryDialog();
                            } else {
                                view.downloadFailer("未获取分享信息!");
                                view.showToast("未获取分享信息!");
                            }
                        } else {
                            view.downloadSuccess(filePath, url);
                            if (bIsShowDialog) {
                                view.disDialog();
                            }
                        }
                    }
                });
        subscriptionList.add(subscriptionNext);
    }
}
