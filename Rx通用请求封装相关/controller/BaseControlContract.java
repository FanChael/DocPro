package com.xxxx.app.net.controller;

import com.xxxx.app.net.ResultCallBack;

import java.util.HashMap;
import java.util.List;

/*
*@Description: 服务+视图层的纽带
*@Author: hl
*@Time: 2018/11/21 13:55
*/
public interface BaseControlContract {
    interface View extends BaseView{
        <T> void onSucess(Class _dataModelclass, T t);
        void onFailed(Class _dataModelclass, String _message);
    }

    interface RefreshView extends BaseViewByRefresh{
        <T> void onSucess(Class _dataModelclass, List<T> listT);
    }

    interface RefreshViewData extends BaseViewByRefresh{
        <T> void onSucess(Class _dataModelclass, T listD);
    }

    /**
     * 负责请求网络获取数据
     */
    interface Presenter extends BasePresenter{
        /**
         * 请求数据【对象】
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         */
        void requestData(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog);
        /**
         * 请求数据【对象】
         * @param _dataModelclass - 实体类类型
         * @param _fileList
         */
        void requestFile(Class _dataModelclass, ResultCallBack _resultCallBack, List<String> _fileList, boolean _bShowDialog);
        /**
         * 请求数据【对象】
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         */
        void postData(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog);
        /**
         * 请求数据【对象】
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         */
        void requestDataStr(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog);
        /**
         * 请求数据【对象】
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         */
        void postDataStr(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, String> _paramList, boolean _bShowDialog);
        /**
         * 请求数据【对象】
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         */
        void postDataMultype(Class _dataModelclass, ResultCallBack _resultCallBack, HashMap<String, Object> _paramList, boolean _bShowDialog);
        /**
         * 请求数据【对象】
         * @param _dataModelclass - 实体类类型
         * @param _fileList - 文件列表
         */
        void postFile(Class _dataModelclass, ResultCallBack _resultCallBack, List<String> _fileList, boolean _bShowDialog);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _pageNum
         * @param _bIsRefresh
         */
        void requestDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, boolean _bIsRefresh);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         * @param _bIsRefresh
         */
        void requestDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _pageNum
         * @param _bIsRefresh
         */
        void postDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, boolean _bIsRefresh);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         * @param _bIsRefresh
         */
        void postDataList(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _pageNum
         * @param _bIsRefresh
         */
        void requestDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, boolean _bIsRefresh);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         * @param _bIsRefresh
         */
        void requestDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _pageNum
         * @param _bIsRefresh
         */
        void postDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, boolean _bIsRefresh);
        /**
         * 请求数据【对象】列表
         * @param _dataModelclass - 实体类类型
         * @param _paramList
         * @param _bIsRefresh
         */
        void postDataListData(Class _dataModelclass, ResultCallBack _resultCallBack, int _pageNum, HashMap<String, String> _paramList, boolean _bIsRefresh);

        /**
         * rx+glide图片下载 - TODO 后面单独封装一个纯rx版本的
         * @param imgUrlList
         */
        void replaceImgSrc(Class _dataModelclass, List<String> imgUrlList, List<String> srcId);

        /**
         * rx文件下载
         * @param fileUrl
         */
        void downLoadFile(Class _dataModelclass, String fileUrl, boolean _bShowDialog);
    }
}
