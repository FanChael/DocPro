package com.xxxx.app.net.controller.presenter;

import android.graphics.Rect;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.Wave;
import com.xxxx.app.app.MyApplication;
import com.xxxx.app.common.app.UserInfoManager;
import com.xxxx.app.common.utils.DensityUtil;
import com.xxxx.app.common.utils.FileHelperUtil;
import com.xxxx.app.common.utils.glide.RxLoadGlidePic;
import com.xxxx.app.common.utils.system.ScreenUtil;
import com.xxxx.app.net.BaseObserver;
import com.xxxx.app.net.ExceptionHandle;
import com.xxxx.app.net.HttpResponse;
import com.xxxx.app.net.NetUrlManager;
import com.xxxx.app.net.ResponseFunc;
import com.xxxx.app.net.ResultCallBack;
import com.xxxx.app.net.RetrofitManager;
import com.xxxx.app.net.controller.BaseControlContract;
import com.xxxx.app.net.service.BaseService;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pop.hl.com.poplibrary.utils.SystemUtils;

/*
 *@Description: 请求服务表示层
 *@Author: hl
 *@Time: 2018/11/21 13:54
 */
public class BaseControlPresenter<D> implements BaseControlContract.Presenter {
    private BaseControlContract.View view;                      ///< 数据【对象】请求
    private BaseControlContract.RefreshView refreshView;        ///< 数据【对象】列表请求
    private BaseControlContract.RefreshViewData refreshViewD;   ///< 数据【对象】列表请求
    private BaseService baseService;                            ///< 请求服务
    private List<Disposable> subscriptionList = new ArrayList();

    private HashMap<String, Object> externParams = new HashMap<>();
    private HashMap<String, String> headers = new HashMap<>();
    private String lastRequestTime = "";
    private boolean bNeedRequestime = false;

    /**
     * 请求绑定对应控件 - 比如按钮点击请求
     * 1. 如果此时列表存在该类的请求方法，则结束前不允许多次调用
     * 2. 此时可以针对按钮做加载动画；当然还可以设置禁止二次点击(不设置也可以，已经放置多次调用)；
     */
    private HashMap<Class, HashMap<View, Rect[]>> bindView = new HashMap<>();
    private Wave wave = new Wave();
    /**
     * 请求方法对应的类的列表 - 多次连续调用如果已经存在请求，不再请求；防止多次请求
     */
    private List<Class> requestList = new ArrayList();

    private BaseControlPresenter() {
        this.baseService = RetrofitManager.getInstance()
                .getmRetrofit()
                .create(BaseService.class);
    }

    public BaseControlPresenter(BaseControlContract.View _view) {
        this();
        this.view = _view;
    }

    public BaseControlPresenter(BaseControlContract.View _view, boolean _bHasCookie) {
        this();
        this.view = _view;
    }

    public BaseControlPresenter(BaseControlContract.RefreshView _refreshView) {
        this();
        this.refreshView = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.RefreshView _refreshView, boolean _bHasCookie) {
        this();
        this.refreshView = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshView _refreshView) {
        this();
        this.view = _view;
        this.refreshView = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshView _refreshView, boolean _bHasCookie) {
        this();
        this.view = _view;
        this.refreshView = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.RefreshViewData _refreshView) {
        this();
        this.refreshViewD = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.RefreshViewData _refreshView, boolean _bHasCookie) {
        this();
        this.refreshViewD = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshViewData _refreshView) {
        this();
        this.view = _view;
        this.refreshViewD = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshViewData _refreshView, boolean _bHasCookie) {
        this();
        this.view = _view;
        this.refreshViewD = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.RefreshView _refreshView, BaseControlContract.RefreshViewData _refreshViewData) {
        this();
        this.refreshView = _refreshView;
        this.refreshViewD = _refreshViewData;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshView _refreshView, BaseControlContract.RefreshViewData _refreshViewD) {
        this();
        this.view = _view;
        this.refreshView = _refreshView;
        this.refreshViewD = _refreshViewD;
    }

    /**
     * 添加请求参数
     *
     * @param key
     * @param value
     * @return
     */
    public BaseControlPresenter addParam(String key, Object value) {
        externParams.put(key, value);
        return this;
    }

    /**
     * 添加请求Header
     *
     * @param key
     * @param value
     * @return
     */
    public BaseControlPresenter addHeaders(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * 绑定控件(控件可以做动画)
     *
     * @param key
     * @param view
     * @return
     */
    public BaseControlPresenter bindView(Class key, View view,
                                         Rect oldPadding, Rect newPadding) {
        HashMap<View, Rect[]> viewRectHashMap = new HashMap<>();
        viewRectHashMap.put(view, new Rect[]{oldPadding, newPadding});
        bindView.put(key, viewRectHashMap);
        return this;
    }

    /**
     * 是否请求时带上一次请求时间RequestTime参数(当前页刷新的话，这个可能有用)
     *
     * @param bNeedRequestime
     */
    public BaseControlPresenter enableRequestTime(boolean bNeedRequestime) {
        this.bNeedRequestime = bNeedRequestime;
        return this;
    }

    @Override
    public void requestData(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog) {
        getPostData(_dataModelclass, _resultCallBack, _paramList, true, _bShowDialog);
    }

    @Override
    public void requestFile(Class _dataModelclass, ResultCallBack _resultCallBack, List<String> _fileList, boolean _bShowDialog) {
        getPostData(_dataModelclass, _resultCallBack, _fileList, true, _bShowDialog);
    }

    @Override
    public void postData(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog) {
        getPostData(_dataModelclass, _resultCallBack, _paramList, false, _bShowDialog);
    }

    @Override
    public void requestDataStr(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog) {
        getPostDataStr(_dataModelclass, _resultCallBack, _paramList, true, _bShowDialog);
    }

    @Override
    public void postDataStr(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog) {
        getPostDataStr(_dataModelclass, _resultCallBack, _paramList, false, _bShowDialog);
    }

    @Override
    public void postDataMultype(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, Object> _paramList, boolean _bShowDialog) {
        getPostDataMultype(_dataModelclass, _resultCallBack, _paramList, false, _bShowDialog);
    }

    @Override
    public void postFile(Class _dataModelclass, ResultCallBack _resultCallBack, List<String> _fileList, boolean _bShowDialog) {
        getPostData(_dataModelclass, _resultCallBack, _fileList, false, _bShowDialog);
    }

    @Override
    public void requestDataList(Class _dataModelclass, ResultCallBack _resultCallBack, final int _pageNum, final boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack, null, _pageNum, _bIsRefresh, true);
    }

    @Override
    public void requestDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack, _paramList, _pageNum, _bIsRefresh, true);
    }

    @Override
    public void postDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack, null, _pageNum, _bIsRefresh, false);
    }

    @Override
    public void postDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack, _paramList, _pageNum, _bIsRefresh, false);
    }

    @Override
    public void requestDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, final int _pageNum, final boolean _bIsRefresh) {
        getPostDataListData(_dataModelclass, _resultCallBack, null, _pageNum, _bIsRefresh, true);
    }

    @Override
    public void requestDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh) {
        getPostDataListData(_dataModelclass, _resultCallBack, _paramList, _pageNum, _bIsRefresh, true);
    }

    @Override
    public void postDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, boolean _bIsRefresh) {
        getPostDataListData(_dataModelclass, _resultCallBack, null, _pageNum, _bIsRefresh, false);
    }

    @Override
    public void postDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh) {
        getPostDataListData(_dataModelclass, _resultCallBack, _paramList, _pageNum, _bIsRefresh, false);
    }

    @Override
    public void releaseResource() {
        for (int i = 0; i < subscriptionList.size(); ++i) {
            if (null != subscriptionList.get(i) && !subscriptionList.get(i).isDisposed()) {
                subscriptionList.get(i).dispose();
            }
        }
        requestList.clear();
        subscriptionList.clear();
        ///< 清空cookiejar内容
        RetrofitManager.getInstance().clearByCookie();
    }

    /**
     * get post请求分别处理
     *
     * @param _dataModelclass
     * @param _paramList
     * @param bIsRequest
     */
    private void getPostData(final Class _dataModelclass, final ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean bIsRequest, final boolean _bShowDialog) {
        // 添加当前请求标识
        if (addRequestFlag(_dataModelclass)) {
            return;
        }

        ///< 显示进度条
        if (_bShowDialog) {
            view.showDialog();
        }
        HashMap<String, String> paramList = null;
        if (null == _paramList) {
            paramList = new HashMap<>();
        } else {
            paramList = new HashMap<>(_paramList);
        }
        if (null != UserInfoManager.getToken() &&
                !UserInfoManager.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("login_name", UserInfoManager.getPhone());
            paramList.put("access_token", UserInfoManager.getToken());
        }
        // 加入额外请求参数
        if (null != externParams && externParams.size() > 0) {
            for (Map.Entry<String, Object> entry : externParams.entrySet()) {
                if (entry.getValue() instanceof Integer) {
                    paramList.put(entry.getKey(), String.valueOf(entry.getValue()));
                } else {
                    paramList.put(entry.getKey(), (String) entry.getValue());
                }
            }
            externParams.clear();
        }
        // 是否带上上一次请求的时间
        if (bNeedRequestime && !lastRequestTime.equals("")) {
            paramList.put("starttime", lastRequestTime);
        }

        Observable<HttpResponse<String>> observable = null;
        String funName = NetUrlManager.getFunUrl(_dataModelclass);
        if (bIsRequest) {
            observable = baseService.getData(funName, paramList);
        } else {
            observable = baseService.postData(funName, paramList);
        }
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<D, String>() {
                    @Override
                    public D onCall(String data, String requestTime) {
                        lastRequestTime = requestTime;
                        return (D) NetUrlManager.getBean(data, _resultCallBack);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<D>(view) {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                view.onLoinOut(_dataModelclass, new Object[]{statusCode, responeThrowable.message});
                                view.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            case ExceptionHandle.ERROR.THIRD_BIND:
                                ///< 取消进度条
                                if (_bShowDialog) {
                                    view.retryDialog();
                                }
                                // 需要绑定账号 - 此时其实是成功的一种情况，所以直接return.
                                view.onThirdBind(_dataModelclass, 11);

                                // 清除本次请求
                                clearRequestFlag(_dataModelclass);
                                return;
                            default:
                                view.showToast(responeThrowable.message);
                                break;
                        }
                        ///< 取消进度条
                        if (_bShowDialog) {
                            view.retryDialog();
                        }
                        view.onFailed(_dataModelclass, responeThrowable.message);

                        // 清除本次请求
                        clearRequestFlag(_dataModelclass);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subscriptionList.add(d);
                    }

                    @Override
                    public void onNext(D data) {
                        view.<D>onSucess(_dataModelclass, data);
                        ///< 显示进度条
                        if (_bShowDialog) {
                            view.disDialog();
                        }

                        // 清除本次请求
                        clearRequestFlag(_dataModelclass);
                    }
                });
    }

    /**
     * get post请求分别处理
     *
     * @param _dataModelclass
     * @param _fileList
     * @param bIsRequest
     */
    private void getPostData(final Class _dataModelclass, final ResultCallBack _resultCallBack, List<String> _fileList, boolean bIsRequest, final boolean _bShowDialog) {
        // 添加当前请求标识
        if (addRequestFlag(_dataModelclass)) {
            return;
        }

        ///< 显示进度条
        if (_bShowDialog) {
            view.showDialog();
        }
        HashMap<String, String> paramList = new HashMap<>();
        if (null != UserInfoManager.getToken() &&
                !UserInfoManager.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("login_name", UserInfoManager.getPhone());
            paramList.put("access_token", UserInfoManager.getToken());
        }
        // 加入额外请求参数
        if (null != externParams && externParams.size() > 0) {
            for (Map.Entry<String, Object> entry : externParams.entrySet()) {
                if (entry.getValue() instanceof Integer) {
                    paramList.put(entry.getKey(), String.valueOf(entry.getValue()));
                } else {
                    paramList.put(entry.getKey(), (String) entry.getValue());
                }
            }
            externParams.clear();
        }
        // 是否带上上一次请求的时间
        if (bNeedRequestime && !lastRequestTime.equals("")) {
            paramList.put("starttime", lastRequestTime);
        }

        // 构造上传body
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);    ///< 表单类型
        for (Map.Entry<String, String> entry : paramList.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (int i = 0; i < _fileList.size(); ++i) {
            String fileUrl = _fileList.get(i);
            File file = new File(fileUrl);
            if (null == file || !file.exists()) {
                continue;
            }
            String mediaType = "image/png";
            if (file.getName().contains("png") || file.getName().contains("PNG")) {
                mediaType = "image/png";
            }
            if (file.getName().contains("jpg") || file.getName().contains("jpg")) {
                mediaType = "image/jpg";
            }
            if (file.getName().contains("jpeg") || file.getName().contains("JPEG")) {
                mediaType = "image/jpeg";
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), file);
            builder.addFormDataPart("Filedata", file.getName(), requestBody);   ///< 多个文件，多个requestbody就行..
        }

        List<MultipartBody.Part> partsBody = builder.build().parts();
        Observable<HttpResponse<String>> observable = null;
        String funName = NetUrlManager.getFunUrl(_dataModelclass);
        if (bIsRequest) {
            observable = baseService.getData(funName, partsBody);
        } else {
            observable = baseService.postData(funName, partsBody);
        }
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<D, String>() {
                    @Override
                    public D onCall(String data, String requestTime) {
                        lastRequestTime = requestTime;
                        return (D) NetUrlManager.getBean(data, _resultCallBack);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<D>(view) {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                view.onLoinOut(_dataModelclass, new Object[]{statusCode, responeThrowable.message});
                                view.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            case ExceptionHandle.ERROR.THIRD_BIND:
                                ///< 取消进度条
                                if (_bShowDialog) {
                                    view.retryDialog();
                                }
                                // 需要绑定账号 - 此时其实是成功的一种情况，所以直接return.
                                view.onThirdBind(_dataModelclass, 11);

                                // 清除当前请求标识
                                clearRequestFlag(_dataModelclass);
                                return;
                            default:
                                view.showToast(responeThrowable.message);
                                break;
                        }
                        ///< 取消进度条
                        if (_bShowDialog) {
                            view.retryDialog();
                        }
                        view.onFailed(_dataModelclass, responeThrowable.message);

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subscriptionList.add(d);
                    }

                    @Override
                    public void onNext(D data) {
                        view.<D>onSucess(_dataModelclass, data);
                        ///< 显示进度条
                        if (_bShowDialog) {
                            view.disDialog();
                        }

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }
                });
    }

    /**
     * get post请求分别处理
     *
     * @param _dataModelclass
     * @param _paramList
     * @param bIsRequest
     */
    private void getPostDataStr(final Class _dataModelclass, final ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean bIsRequest, final boolean _bShowDialog) {
        // 添加当前请求标识
        if (addRequestFlag(_dataModelclass)) {
            return;
        }

        ///< 显示进度条
        if (_bShowDialog) {
            view.showDialog();
        }
        HashMap<String, String> paramList = null;
        if (null == _paramList) {
            paramList = new HashMap<>();
        } else {
            paramList = new HashMap<>(_paramList);
        }
        if (null != UserInfoManager.getToken() &&
                !UserInfoManager.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("login_name", UserInfoManager.getPhone());
            paramList.put("access_token", UserInfoManager.getToken());
        }
        // 加入额外请求参数
        if (null != externParams && externParams.size() > 0) {
            for (Map.Entry<String, Object> entry : externParams.entrySet()) {
                if (entry.getValue() instanceof Integer) {
                    paramList.put(entry.getKey(), String.valueOf(entry.getValue()));
                } else {
                    paramList.put(entry.getKey(), (String) entry.getValue());
                }
            }
            externParams.clear();
        }

        Observable<String> observable = null;
        String funName = NetUrlManager.getFunUrl(_dataModelclass);
        if (bIsRequest) {
            observable = baseService.getDataStr(funName, paramList);
        } else {
            observable = baseService.postDataStr(funName, paramList);
        }
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, D>() {
                    @Override
                    public D apply(String s) throws Exception {
                        return (D) s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<D>(view) {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                view.onLoinOut(_dataModelclass, new Object[]{statusCode, responeThrowable.message});
                                view.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            default:
                                view.showToast(responeThrowable.message);
                                break;
                        }
                        ///< 显示进度条
                        if (_bShowDialog) {
                            view.retryDialog();
                        }
                        view.onFailed(_dataModelclass, responeThrowable.message);

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subscriptionList.add(d);
                    }

                    @Override
                    public void onNext(D data) {
                        view.<D>onSucess(_dataModelclass, data);
                        ///< 显示进度条
                        if (_bShowDialog) {
                            view.disDialog();
                        }

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }
                });
    }

    /**
     * get post请求分别处理
     *
     * @param _dataModelclass
     * @param _paramList
     * @param bIsRequest
     */
    private void getPostDataMultype(final Class _dataModelclass, final ResultCallBack _resultCallBack, HashMap<String, Object> _paramList, boolean bIsRequest, final boolean _bShowDialog) {
        // 添加当前请求标识
        if (addRequestFlag(_dataModelclass)) {
            return;
        }

        ///< 显示进度条
        if (_bShowDialog) {
            view.showDialog();
        }
        HashMap<String, Object> paramList = null;
        if (null == _paramList) {
            paramList = new HashMap<>();
        } else {
            paramList = new HashMap<>(_paramList);
        }
        if (null != UserInfoManager.getToken() &&
                !UserInfoManager.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("login_name", UserInfoManager.getPhone());
            paramList.put("access_token", UserInfoManager.getToken());
        }
        // 加入额外请求参数
        if (null != externParams && externParams.size() > 0) {
            for (Map.Entry<String, Object> entry : externParams.entrySet()) {
                if (entry.getValue() instanceof Integer) {
                    paramList.put(entry.getKey(), String.valueOf(entry.getValue()));
                } else {
                    paramList.put(entry.getKey(), (String) entry.getValue());
                }
            }
            externParams.clear();
        }
        // 是否带上上一次请求的时间
        if (bNeedRequestime && !lastRequestTime.equals("")) {
            paramList.put("starttime", lastRequestTime);
        }

        Observable<HttpResponse<String>> observable = null;
        String funName = NetUrlManager.getFunUrl(_dataModelclass);
        if (bIsRequest) {
            observable = baseService.getDataMultype(funName, paramList);
        } else {
            observable = baseService.postDataMultype(funName, paramList);
        }
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<D, String>() {
                    @Override
                    public D onCall(String data, String requestTime) {
                        lastRequestTime = requestTime;
                        return (D) NetUrlManager.getBean(data, _resultCallBack);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<D>(view) {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                view.onLoinOut(_dataModelclass, new Object[]{statusCode, responeThrowable.message});
                                view.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            default:
                                view.showToast(responeThrowable.message);
                                break;
                        }
                        ///< 显示进度条
                        if (_bShowDialog) {
                            view.retryDialog();
                        }
                        view.onFailed(_dataModelclass, responeThrowable.message);

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subscriptionList.add(d);
                    }

                    @Override
                    public void onNext(D data) {
                        view.<D>onSucess(_dataModelclass, data);
                        ///< 显示进度条
                        if (_bShowDialog) {
                            view.disDialog();
                        }

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }
                });
    }

    /**
     * get post数据请求
     *
     * @param _dataModelclass
     * @param _pageNum
     * @param _bIsRefresh
     */
    private void getPostDataList(final Class _dataModelclass, final ResultCallBack _resultCallBack, HashMap<String, String> _paramList, final int _pageNum, final boolean _bIsRefresh, boolean bIsRequest) {
        // 添加当前请求标识
        if (addRequestFlag(_dataModelclass)) {
            return;
        }

        ///< 不是刷新，则显示进度条加载
        if (!_bIsRefresh) {
            ///< 显示进度条 - 首次刷新会显示，其他都是空运行
            refreshView.showDialog();
        }
        HashMap<String, String> paramList = null;
        if (null == _paramList) {
            paramList = new HashMap<>();
        } else {
            paramList = new HashMap<>(_paramList);
        }
        if (-1 != _pageNum) {
            paramList.put("page", _pageNum + "");
        }
        if (null != UserInfoManager.getToken() &&
                !UserInfoManager.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("login_name", UserInfoManager.getPhone());
            paramList.put("access_token", UserInfoManager.getToken());
        }
        // 加入额外请求参数
        if (null != externParams && externParams.size() > 0) {
            for (Map.Entry<String, Object> entry : externParams.entrySet()) {
                if (entry.getValue() instanceof Integer) {
                    paramList.put(entry.getKey(), String.valueOf(entry.getValue()));
                } else {
                    paramList.put(entry.getKey(), (String) entry.getValue());
                }
            }
            externParams.clear();
        }
        // 是否带上上一次请求的时间
        if (bNeedRequestime && !lastRequestTime.equals("")) {
            paramList.put("starttime", lastRequestTime);
        }

        Observable<HttpResponse<String>> observable = null;
        String funName = NetUrlManager.getFunUrl(_dataModelclass);
        if (bIsRequest) {
            observable = baseService.getDataList(funName, paramList);
        } else {
            observable = baseService.postDataList(funName, paramList);
        }
        observable
                .subscribeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<List<D>, String>() {
                    @Override
                    public List<D> onCall(String data, String requestTime) {
                        lastRequestTime = requestTime;
                        return (List<D>) NetUrlManager.getBeanList(data, _resultCallBack, _pageNum);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<D>>(refreshView) {
                    @Override
                    public void onComplete() {
                        ///< 如果是刷新才调用刷新结束接口
                        if (_bIsRefresh) {
                            if (1 == _pageNum || -1 == _pageNum) {
                                refreshView.finishRefresh();
                            } else {
                                refreshView.finishLoadMore();
                            }
                        }
                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                refreshView.onLoinOut(_dataModelclass, new Object[]{statusCode, responeThrowable.message});
                                refreshView.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            default:
                                refreshView.showToast(responeThrowable.message);
                                break;
                        }
                        if (!_bIsRefresh) {
                            refreshView.retryDialog();
                        } else {
                            refreshView.onRequestFailer();
                        }
                        onComplete();

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subscriptionList.add(d);
                    }

                    @Override
                    public void onNext(List<D> dataList) {
                        if (dataList.size() < 1) {
                            if (1 == _pageNum) { ///< @再次请求首页如果没有数据的情况下，清空列表
                                refreshView.resetItemList();
                                if (!_bIsRefresh) {
                                    refreshView.emptyDialog();
                                }
                                refreshView.<D>onSucess(_dataModelclass, dataList);
                            } else {
                                // refreshView.showToast("沒有更多了!");
                                refreshView.onNoMoreData();
                            }
                        } else {
                            if (1 == _pageNum) {
                                refreshView.resetItemList();
                                refreshView.<D>onSucess(_dataModelclass, dataList);
                            } else {
                                refreshView.<D>onSucess(_dataModelclass, dataList);
                            }
                            if (!_bIsRefresh) {
                                refreshView.disDialog();
                            }
                        }

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }
                });
    }

    /**
     * get post数据请求
     *
     * @param _dataModelclass
     * @param _pageNum
     * @param _bIsRefresh
     */
    private void getPostDataListData(final Class _dataModelclass, final ResultCallBack _resultCallBack, HashMap<String, String> _paramList, final int _pageNum, final boolean _bIsRefresh, boolean bIsRequest) {
        // 添加当前请求标识
        if (addRequestFlag(_dataModelclass)) {
            return;
        }

        ///< 不是刷新，则显示进度条加载
        if (!_bIsRefresh) {
            ///< 显示进度条 - 首次刷新会显示，其他都是空运行
            refreshViewD.showDialog();
        }
        HashMap<String, String> paramList = null;
        if (null == _paramList) {
            paramList = new HashMap<>();
        } else {
            paramList = new HashMap<>(_paramList);
        }
        if (-1 != _pageNum) {
            paramList.put("page", _pageNum + "");
        }
        if (null != UserInfoManager.getToken() &&
                !UserInfoManager.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("login_name", UserInfoManager.getPhone());
            paramList.put("access_token", UserInfoManager.getToken());
        }
        // 加入额外请求参数
        if (null != externParams && externParams.size() > 0) {
            for (Map.Entry<String, Object> entry : externParams.entrySet()) {
                if (entry.getValue() instanceof Integer) {
                    paramList.put(entry.getKey(), String.valueOf(entry.getValue()));
                } else {
                    paramList.put(entry.getKey(), (String) entry.getValue());
                }
            }
            externParams.clear();
        }
        // 是否带上上一次请求的时间
        if (bNeedRequestime && !lastRequestTime.equals("")) {
            paramList.put("starttime", lastRequestTime);
        }

        Observable<HttpResponse<String>> observable = null;
        String funName = NetUrlManager.getFunUrl(_dataModelclass);
        if (bIsRequest) {
            // 如果有Header参数，则调用动态添加Header的方法
            if (headers.size() > 0) {
                observable = baseService.getDataList(funName, headers, paramList);
                // 用完就清除即可
                headers.clear();
            } else {
                observable = baseService.getDataList(funName, paramList);
            }
        } else {
            observable = baseService.postDataList(funName, paramList);
        }
        observable
                .subscribeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<D, String>() {
                    @Override
                    public D onCall(String data, String requestTime) {
                        lastRequestTime = requestTime;
                        return (D) NetUrlManager.getBeanList(data, _resultCallBack, _pageNum);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<D>(refreshView) {
                    @Override
                    public void onComplete() {
                        ///< 如果是刷新才调用刷新结束接口
                        if (_bIsRefresh) {
                            if (1 == _pageNum || -1 == _pageNum) {
                                refreshViewD.finishRefresh();
                            } else {
                                refreshViewD.finishLoadMore();
                            }
                        }
                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                refreshViewD.onLoinOut(_dataModelclass, new Object[]{statusCode, responeThrowable.message});
                                refreshViewD.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            default:
                                refreshViewD.showToast(responeThrowable.message);
                                break;
                        }
                        if (!_bIsRefresh) {
                            refreshViewD.retryDialog();
                        } else {
                            refreshViewD.onRequestFailer();
                        }
                        onComplete();

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subscriptionList.add(d);
                    }

                    @Override
                    public void onNext(D dataList) {
                        if (1 == _pageNum) {
                            refreshViewD.resetItemList();
                            refreshViewD.<D>onSucess(_dataModelclass, dataList);
                        } else {
                            refreshViewD.<D>onSucess(_dataModelclass, dataList);
                        }
                        if (!_bIsRefresh) {
                            refreshViewD.disDialog();
                        }

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }
                });
    }

    @Override
    public void replaceImgSrc(Class _dataModelclass, List<String> imgUrlList, List<String> srcId) {
        if (null != imgUrlList && imgUrlList.size() > 0) {
            for (int i = 0; i < imgUrlList.size(); ++i) {
                new RxLoadGlidePic(view).execute(_dataModelclass, imgUrlList.get(i),
                        srcId.get(i), imgUrlList.size());
            }
        }
    }

    @Override
    public void downLoadFile(Class _dataModelclass, String fileUrl, boolean _bShowDialog) {
        // 添加当前请求标识
        if (addRequestFlag(_dataModelclass)) {
            return;
        }

        ///< 显示进度条
        if (_bShowDialog) {
            view.showDialog();
        }

        Observable<ResponseBody> observable = baseService.downloadFileWithDynamicUrlSync(fileUrl);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        ///< 下载后的保存路径
                        String picPath = SystemUtils.getCacheDirectory(MyApplication.getInstance(),
                                Environment.DIRECTORY_DOWNLOADS) + "/" +
                                Calendar.getInstance().getTimeInMillis() + fileUrl.substring(fileUrl.lastIndexOf("."));
                        if (FileHelperUtil.writeFile(responseBody.byteStream(), picPath)) {
                            return picPath;
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showToast(e.getMessage());
                        ///< 取消进度条
                        if (_bShowDialog) {
                            view.retryDialog();
                        }
                        view.onFailed(_dataModelclass, e.getMessage());


                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subscriptionList.add(d);
                    }

                    @Override
                    public void onNext(String data) {
                        view.onSucess(_dataModelclass, data);
                        ///< 显示进度条
                        if (_bShowDialog) {
                            view.disDialog();
                        }

                        // 清除当前请求标识
                        clearRequestFlag(_dataModelclass);
                    }
                });
    }

    /**
     * 添加当前请求标识
     * @param _dataModelclass
     */
    private boolean addRequestFlag(Class _dataModelclass){
        if (requestList.contains(_dataModelclass)) {
            return true;
        }
        requestList.add(_dataModelclass);

        // 添加动画
        if (bindView.containsKey(_dataModelclass)){
            HashMap<View, Rect[]> viewHashMap = bindView.get(_dataModelclass);
            for (View view : viewHashMap.keySet()) {
                Rect[] rects = viewHashMap.get(view);
                Rect oldRect = rects[0];
                Rect newRect = rects[1];

                wave.setBounds(0, 0, DensityUtil.dip2px(oldRect.right), DensityUtil.dip2px(oldRect.top + oldRect.bottom));
                wave.setColor(0xFFFFFFFF);
                ScreenUtil.setPadding(view,
                        DensityUtil.dip2px(newRect.left), DensityUtil.dip2px(newRect.top),
                        DensityUtil.dip2px(newRect.right), DensityUtil.dip2px(newRect.bottom));
                ((TextView)view).setCompoundDrawables(null, null, wave, null);
                wave.start();
            }
        }
        return false;
    }

    /**
     * 清除当前请求标识
     * @param _dataModelclass
     */
    private void clearRequestFlag(Class _dataModelclass){
        // 清除当前请求标识
        if (requestList.contains(_dataModelclass)) {
            requestList.remove(_dataModelclass);
        }
        if (bindView.containsKey(_dataModelclass)){
            HashMap<View, Rect[]> viewHashMap = bindView.get(_dataModelclass);
            for (View view : viewHashMap.keySet()) {
                wave.stop();
                ((TextView)view).setCompoundDrawables(null, null, null, null);

                Rect[] rects = viewHashMap.get(view);
                Rect oldRect = rects[0];
                ScreenUtil.setPadding(view,
                        DensityUtil.dip2px(oldRect.left), DensityUtil.dip2px(oldRect.top),
                        DensityUtil.dip2px(oldRect.right), DensityUtil.dip2px(oldRect.bottom));
            }

            bindView.remove(_dataModelclass);
        }
    }
}