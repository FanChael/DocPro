package com.lieyunwang.liemine.mvp.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lieyunwang.liemine.R;
import com.lieyunwang.liemine.mvp.presenter.tool.UserInfoControlPresenterTool;
import com.lieyunwang.liemine.net.BaseSubscribers;
import com.lieyunwang.liemine.net.ExceptionHandle;
import com.lieyunwang.liemine.net.HttpResponse;
import com.lieyunwang.liemine.net.NetUrlManager;
import com.lieyunwang.liemine.net.ResponseFunc;
import com.lieyunwang.liemine.net.ResultCallBack;
import com.lieyunwang.liemine.net.RetrofitManager;
import com.lieyunwang.liemine.net.service.BaseService;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
 *@Description: 请求服务表示层
 *@Author: hl
 *@Time: 2018/11/21 13:54
 */
public class BaseControlPresenter<D> implements BaseControlContract.Presenter {
    private BaseControlContract.View view;                  ///< 数据【对象】请求
    private BaseControlContract.RefreshView refreshView;   ///< 数据【对象】列表请求
    private BaseService baseService;                       ///< 请求服务
    private List<Subscription> subscriptionList = new ArrayList<Subscription>();
    private boolean bHasCookie = false;

    private BaseControlPresenter(boolean _bHasCookie) {
        if (bHasCookie){
            this.baseService = RetrofitManager.getInstanceByCookie()
                    .getmRetrofitByCookie().create(BaseService.class);
        }else{
            this.baseService = RetrofitManager.getInstance()
                    .getmRetrofit()
                    .create(BaseService.class);
        }
    }

    public BaseControlPresenter(BaseControlContract.View _view) {
        this(false);
        this.view = _view;
    }

    public BaseControlPresenter(BaseControlContract.View _view, boolean _bHasCookie) {
        this(_bHasCookie);
        this.view = _view;
    }

    public BaseControlPresenter(BaseControlContract.RefreshView _refreshView) {
        this(false);
        this.refreshView = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.RefreshView _refreshView, boolean _bHasCookie) {
        this(_bHasCookie);
        this.refreshView = _refreshView;
        this.bHasCookie = _bHasCookie;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshView _refreshView) {
        this(false);
        this.view = _view;
        this.refreshView = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshView _refreshView, boolean _bHasCookie) {
        this(_bHasCookie);
        this.view = _view;
        this.refreshView = _refreshView;
        this.bHasCookie = _bHasCookie;
    }

    @Override
    public void requestData(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog) {
        getPostData(_dataModelclass, _resultCallBack, _paramList, true, _bShowDialog);
    }

    @Override
    public void postData(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog) {
        getPostData(_dataModelclass, _resultCallBack, _paramList, false, _bShowDialog);
    }

    @Override
    public void requestDataList(final Class _dataModelclass, ResultCallBack _resultCallBack, final int _pageNum, final boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack, null, _pageNum, _bIsRefresh, true);
    }

    @Override
    public void requestDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack, _paramList, _pageNum, _bIsRefresh, true);
    }

    @Override
    public void postDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack,null, _pageNum, _bIsRefresh, false);
    }

    @Override
    public void postDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh) {
        getPostDataList(_dataModelclass, _resultCallBack, _paramList, _pageNum, _bIsRefresh, false);
    }

    @Override
    public void releaseResource() {
        for (int i = 0; i < subscriptionList.size(); ++i) {
            if (null != subscriptionList.get(i) && !subscriptionList.get(i).isUnsubscribed()) {
                subscriptionList.get(i).unsubscribe();
            }
        }
        subscriptionList.clear();
        if (bHasCookie){
            ///< 清空cookiejar内容
            RetrofitManager.getInstanceByCookie().clearByCookie();
        }
    }

    /**
     * get post请求分别处理
     *
     * @param _dataModelclass
     * @param _paramList
     * @param bIsRequest
     */
    private void getPostData(final Class _dataModelclass, final ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean bIsRequest, final boolean _bShowDialog) {
        ///< 显示进度条
        if (_bShowDialog){
            view.showDialog();
        }
        HashMap<String, String> paramList = null;
        if (null == _paramList) {
            paramList = new HashMap<>();
        } else {
            paramList = _paramList;
        }
        if (null != UserInfoControlPresenterTool.getToken() &&
                !UserInfoControlPresenterTool.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("access_token", UserInfoControlPresenterTool.getToken());
        }
        Observable<HttpResponse<String>> observable = null;
        if (bIsRequest) {
            observable = baseService.getData(NetUrlManager.getFunUrl(_dataModelclass), paramList);
        } else {
            observable = baseService.postData(NetUrlManager.getFunUrl(_dataModelclass), paramList);
        }
        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<D, String>() {
                    @Override
                    public D onCall(String data, String requestTime) {
                        return (D)NetUrlManager.getBean(data, _resultCallBack);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribers<D>(view) {
                    @Override
                    public void onCompleted() {
                        ///< 显示进度条
                        if (_bShowDialog){
                            view.disDialog();
                        }
                        view.onComplete();
                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                UserInfoControlPresenterTool.clearAcount();
                                view.showToast(R.string.accout_error);
                                break;
                            default:
                                view.showToast(responeThrowable.message);
                                break;
                        }
                        onCompleted();
                        view.onFailed(_dataModelclass, responeThrowable.message);
                    }

                    @Override
                    public void onNext(D data) {
                        view.<D>onSucess(data);
                    }
                });

        subscriptionList.add(subscription);
    }

    /**
     * get post数据请求
     *
     * @param _dataModelclass
     * @param _pageNum
     * @param _bIsRefresh
     */
    private void getPostDataList(final Class _dataModelclass, final ResultCallBack _resultCallBack, HashMap<String, String> _paramList, final int _pageNum, final boolean _bIsRefresh, boolean bIsRequest) {
        ///< 不是刷新，则显示进度条加载
        if (!_bIsRefresh) {
            ///< 显示进度条 - 首次刷新会显示，其他都是空运行
            refreshView.showDialog();
        }
        HashMap<String, String> paramList = null;
        if (null == _paramList) {
            paramList = new HashMap<>();
        } else {
            paramList = _paramList;
        }
        if (-1 != _pageNum)
        {
            paramList.put("page", _pageNum + "");
        }
        if (null != UserInfoControlPresenterTool.getToken() &&
            !UserInfoControlPresenterTool.getToken().equals("")) {
            ///< 加入Token参数
            paramList.put("access_token", UserInfoControlPresenterTool.getToken());
        }
        Observable<HttpResponse<String>> observable = null;
        if (bIsRequest) {
            observable = baseService.getDataList(NetUrlManager.getFunUrl(_dataModelclass), paramList);
        } else {
            observable = baseService.postDataList(NetUrlManager.getFunUrl(_dataModelclass), paramList);
        }
        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<List<D>, String>() {
                    @Override
                    public List<D> onCall(String data, String requestTime) {
                        return (List<D>) NetUrlManager.getBeanList(data, _resultCallBack, _pageNum);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribers<List<D>>(refreshView) {
                    @Override
                    public void onCompleted() {
                        ///< 如果是刷新才调用刷新结束接口
                        if (_bIsRefresh) {
                            if (1 == _pageNum) {
                                refreshView.finishRefresh();
                            } else {
                                refreshView.finishLoadMore();
                            }
                        }
                        refreshView.onComplete();
                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                UserInfoControlPresenterTool.clearAcount();
                                refreshView.showToast(R.string.accout_error);
                                break;
                            default:
                                refreshView.showToast(responeThrowable.message);
                                break;
                        }
                        if (!_bIsRefresh) {
                            refreshView.retryDialog();
                        }
                        refreshView.onRequestFailer();
                        onCompleted();
                    }

                    @Override
                    public void onNext(List<D> dataList) {
                        if (dataList.size() < 1) {
                            if (1 == _pageNum) { ///< @再次请求首页如果没有数据的情况下，清空列表
                                refreshView.resetItemList();
                                refreshView.<D>onSucess(dataList);
                                refreshView.emptyDialog();
                            } else {
                                refreshView.onNoMoreData();
                            }
                        } else {
                            if (1 == _pageNum) {
                                refreshView.resetItemList();
                                refreshView.<D>onSucess(dataList);
                            } else {
                                refreshView.<D>onSucess(dataList);
                            }
                            if (!_bIsRefresh) {
                                refreshView.disDialog();
                            }
                        }
                    }
                });

        subscriptionList.add(subscription);
    }
}