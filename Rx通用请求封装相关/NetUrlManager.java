package com.x.finance.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.x.finance.adapter.InfoFragmentFlashAdapter;
import com.x.finance.adapter.SeninarActivityAdapter;
import com.x.finance.adapter.base.BaseMulDataModel;
import com.x.finance.bean.xxxxBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
*@Description: 方法url管理+类型转换管理
*@Author: hl
*@Time: 2018/11/22 16:18
*/
public class NetUrlManager {
    private static HashMap<Class, String> classStringHashMap = new HashMap<Class, String>() {
        {
            put(xxxxBean.ethAddr.class, "xxxx");
           

            /**
             * 快讯
             */
            put(xxxxBean.class, "xxxxxxx");

            /**
             * 分享图片相关
             */
            put(xxxxBean.class, "xxxxxxxxxx");

            /**
             * 邀请
             */
            put(xxxxBean.class, "xxxxxxxxxxxxxxx");
        }
    };

    public static String getFunUrl(Class _class) {
        return classStringHashMap.get(_class);
    }

    /**
     * 快讯时间戳处理
     */
    ///< 记录上一次分页日期 - 决定下一页添加是否需要添加日期的问题（避免同一个日期重复添加显示的问题）
    private static String lastDay = "";
    ///< 存储临时信息，方便根据当前是否已经是不同的日期（以方便设置上一条信息为结束信息的标志）
    private static List<BaseMulDataModel> dataListBak = new ArrayList<>();

    public static Object getBean(String strData, Class _class) {
        if (_class.getName().equals(xxxxBean.class.getName())){
            return JSON.parseObject(strData, new TypeReference<xxxxBean>() {});
        }
        ///< 资讯详情页相关
        else if (_class.getName().equals(xxxxBean.class.getName())){
            return JSON.parseObject(strData, new TypeReference<xxxxBean>() {});
        }else if (_class.getName().equals(xxxxBean.class.getName())){
            if (null == strData || strData.equals("null")){
                return new xxxxBean("", 0, 0);
            }
            return JSON.parseObject(strData, new TypeReference<xxxxBean>() {});
        }

        /**
         * 分享 - 获取的是图片url地址
         */
        else if (_class.getName().equals(xxxxBean.class.getName())){
            return new xxxxBean(strData);
        }
        return null;
    }

    public static Object getBeanList(String strData, Class _class, int _pageNum) {
        if (_class.getName().equals(xxxxBean.class.getName())){
            return JSON.parseObject(strData, new TypeReference<List<xxxxBean>>() {});
        }else if (_class.getName().equals(xxxxBean.class.getName())){
            JSONArray jsonArray = JSONObject.parseObject(strData).getJSONArray("focusList");
            if (null == jsonArray) {
                return null;
            }
            return JSON.parseObject(jsonArray.toJSONString(), new TypeReference<List<xxxxBean>>() {});
        }else if (_class.getName().equals(xxxxBean.class.getName())){
            xxxxBean infoAuthorBean = JSON.parseObject(strData, new TypeReference<xxxxBean>() {});
            List<xxxxBean> dataList = infoAuthorBean.getPostList();
            ///< 抬头的是否关注和文章数量给每个条目都设置上
            for (int i = 0; i < dataList.size(); ++i) {
                dataList.get(i).setIs_focus(infoAuthorBean.getIs_focus());
                dataList.get(i).setCount(infoAuthorBean.getCount());
            }
            return dataList;
        }else if (_class.getName().equals(xxxxBean.class.getName())){
            xxxxBean newsBean = JSON.parseObject(strData, new TypeReference<xxxxBean>() {});
            ///< 获取列表时已经包含了是否有banner的处理
            return newsBean.getPosts();
        }else if (_class.getName().equals(xxxxBean.class.getName())){
            ///< 刷新清空上一次快讯分页日期标题
            if (1 == _pageNum){
                lastDay = "";
                dataListBak.clear();
            }
            List<xxxxBean> infoFlashBean = JSON.parseObject(strData, new TypeReference<List<xxxxBean>>() {});
            List<BaseMulDataModel> dataList = new ArrayList<BaseMulDataModel>();
            if (infoFlashBean.size() > 0){
                for (int i = 0; i < infoFlashBean.size(); ++i){
                    ///< 如果时间有变化则添加新的日期标题
                    if (!lastDay.equals(infoFlashBean.get(i).getDay())){
                        ///< 如果日期变了，那么我们就要把上一条信息设置为结尾信息，这样最后一条信息就不用绘制时间轴了!
                        if (!lastDay.equals("") && dataListBak.size() > 0){
                            ///< 如果是内容条目，根据当前时间已经发生变化（表示将新增时间分类），
                            ///< 那我们就设置上一条条目为那个日期的最后一条条目
                            if (dataListBak.get(dataListBak.size() - 1) instanceof xxxxBean.ListBean){
                                xxxxBean.ListBean infoFlashContentBean = (xxxxBean.ListBean) dataListBak.get(dataListBak.size() - 1);
                                infoFlashContentBean.setbIsEnd(true);
                            }
                        }

                        xxxxBean.InfoFlashTitleBean infoFlashTitleBean = new xxxxBean.InfoFlashTitleBean(infoFlashBean.get(i).getDay(), InfoFragmentFlashAdapter.TYPE_ONE);
                        dataList.add(infoFlashTitleBean);

                        dataListBak.add(infoFlashTitleBean);
                    }
                    List<xxxxBean.ListBean> jsonArrayChild = infoFlashBean.get(i).getList();
                    if (jsonArrayChild.size() > 0){
                        for (int j = 0; j < jsonArrayChild.size(); ++j){
                            InfoFlashBean.ListBean listBean = jsonArrayChild.get(j);
                            ///< 需要根据后面内容是否还有该日期的条目来判断是否是最后一条
                            {
                                listBean.setDrawType(InfoFragmentFlashAdapter.TYPE_TWO);
                                dataList.add(listBean);
                                dataListBak.add(listBean);
                            }
                        }
                    }
                    lastDay = infoFlashBean.get(i).getDay();
                }
            }
            return dataList;
        }
        return null;
    }
}
