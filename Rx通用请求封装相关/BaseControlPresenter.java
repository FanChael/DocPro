package com.x.finance.controller.presenter;

import com.x.finance.controller.BaseControlContract;
import com.x.finance.net.BaseSubscribers;
import com.x.finance.net.ExceptionHandle;
import com.x.finance.net.NetUrlManager;
import com.x.finance.net.ResponseFunc;
import com.x.finance.net.RetrofitManager;
import com.x.finance.net.service.BaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
*@Description: 请求服务表示层
*@Author: hl
*@Time: 2018/11/21 13:54
*/
public class BaseControlPresenter<D> implements BaseControlContract.Presenter{
    private BaseControlContract.View view;                  ///< 数据【对象】请求
    private BaseControlContract.RefreshView refreshView;   ///< 数据【对象】列表请求
    private BaseService baseService;                       ///< 请求服务
    private List<Subscription> subscriptionList = new ArrayList<Subscription>();

    private BaseControlPresenter(){
        this.baseService = RetrofitManager.getInstance()
                .getmRetrofit()
                .create(BaseService.class);
    }

    public BaseControlPresenter(BaseControlContract.View _view){
        this();
        this.view = _view;
    }

    public BaseControlPresenter(BaseControlContract.RefreshView _refreshView){
        this();
        this.refreshView = _refreshView;
    }

    public BaseControlPresenter(BaseControlContract.View _view, BaseControlContract.RefreshView _refreshView){
        this();
        this.view = _view;
        this.refreshView = _refreshView;
    }

    @Override
    public void requestData(final Class _dataModelclass, HashMap<String, String> _paramList) {
        ///< 显示进度条
        view.showDialog();
        HashMap<String, String> paramList = null;
        if (null == _paramList){
            paramList = new HashMap<>();
        }else{
            paramList = _paramList;
        }
        ///< 加入Token参数
        paramList.put("access_token", UserInfoControlPresenter.getToken());
        Subscription subscription = baseService.getData(NetUrlManager.getFunUrl(_dataModelclass), paramList)
                .subscribeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<D, String>() {
                    @Override
                    public D onCall(String data, String requestTime) {
                        return (D) NetUrlManager.getBean(data, _dataModelclass);//dataHandler.String2Data(data);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribers<D>(view) {
                    @Override
                    public void onCompleted() {
                        view.disDialog();
                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                UserInfoControlPresenter.clearAcount();
                                view.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            default:
                                view.showToast(responeThrowable.message);
                                break;
                        }
                        onCompleted();
                    }

                    @Override
                    public void onNext(D data) {
                        view.<D>onSucess(data);
                    }
                });

        subscriptionList.add(subscription);
    }

    @Override
    public void requestDataList(final Class _dataModelclass, final int _pageNum, final boolean _bIsRefresh) {
        ///< 不是刷新，则显示进度条加载
        if (!_bIsRefresh){
            ///< 显示进度条 - 首次刷新会显示，其他都是空运行
            refreshView.showDialog();
        }
        Subscription subscription = baseService.getDataList(NetUrlManager.getFunUrl(_dataModelclass), _pageNum, UserInfoControlPresenter.getToken())
                .subscribeOn(Schedulers.io())
                .map(new ResponseFunc<>(new ResponseFunc.CallMe<List<D>, String>() {
                    @Override
                    public List<D> onCall(String data, String requestTime) {
                        return (List<D>) NetUrlManager.getBeanList(data, _dataModelclass);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribers<List<D>>(refreshView) {
                    @Override
                    public void onCompleted() {
                        ///< 如果是刷新才调用刷新结束接口
                        if (_bIsRefresh){
                            if (1 == _pageNum){
                                refreshView.finishRefresh();
                            }
                            else{
                                refreshView.finishLoadMore();
                            }
                        }
                        else{   ///< @首次请求, 不是刷新，取消进度条
                            refreshView.disDialog();
                        }
                    }

                    @Override
                    public void onErrors(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ///< 接下来就可以根据状态码进行处理...
                        int statusCode = responeThrowable.code;
                        switch (statusCode) {
                            case ExceptionHandle.ERROR.TOKEN:
                                UserInfoControlPresenter.clearAcount();
                                refreshView.showToast("你的账号异常，请重新登录，谢谢!");
                                break;
                            default:
                                refreshView.showToast(responeThrowable.message);
                                break;
                        }
                        if (!_bIsRefresh){
                            refreshView.retryDialog();
                        }
                        else{
                            refreshView.onRequestFailer();
                        }
                        onCompleted();
                    }

                    @Override
                    public void onNext(List<D> dataList) {
                        if (dataList.size() < 1){
                            if (1 == _pageNum){ ///< @再次请求首页如果没有数据的情况下，清空列表
                                refreshView.resetItemList();
                                refreshView.<D>onSucess(dataList);
                            }
                            else{
                                refreshView.showToast("沒有糖果了!");
                            }
                        }else{
                            if (1 == _pageNum){
                                refreshView.resetItemList();
                                refreshView.<D>onSucess(dataList);
                            }
                            else{
                                refreshView.<D>onSucess(dataList);
                            }
                        }
                    }
                });

        subscriptionList.add(subscription);
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
}
