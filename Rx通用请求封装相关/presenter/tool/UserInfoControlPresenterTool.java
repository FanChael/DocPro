package com.lieyunwang.liemine.mvp.presenter.tool;

import com.lieyunwang.liemine.MainApplication;
import com.lieyunwang.liemine.mvp.view.user.bean.UserBean;

import com.lieyunwang.liemine.MainApplication;
import com.lieyunwang.liemine.mvp.view.user.bean.UserBean;
import com.lieyunwang.liemine.common.Constants;
import com.lieyunwang.liemine.utils.storage.SharedPreferencesUtil;

/**
 * Created by hl on 2018/3/23.
 */

public class UserInfoControlPresenterTool {
    /**
     * 判断是否登录
     * @return
     */
    public static boolean bIsLogined() {
        return !(SharedPreferencesUtil.getInstance(MainApplication.getInstance())
                .getSP(Constants.User.get_access_token_key()).equals(""));
    }

    /**
     * 获取用户token
     * @return
     */
    public static String getToken(){
        return SharedPreferencesUtil.getInstance(MainApplication.getInstance())
                .getSP(Constants.User.get_access_token_key());
    }

    /**
     * 用户ID
     * @return
     */
    public static int getUserId(){
        return SharedPreferencesUtil.getInstance(MainApplication.getInstance())
                .getSInt(Constants.User.get_id_key());
    }

    /**
     * 获取用户昵称
     * @return
     */
    public static String getDisName(){
        return SharedPreferencesUtil.getInstance(MainApplication.getInstance())
                .getSP(Constants.User.get_display_name_key());
    }

    /**
     * 获取头像
     * @return
     */
    public static String getAvater(){
        return SharedPreferencesUtil.getInstance(MainApplication.getInstance())
                .getSP(Constants.User.get_user_avatar_key());
    }

    /**
     * 保存用户基本信息
     * 主要是注册或登录成功后使用 - 由于涉及到密码保存，所以需要同时把密码传递过来
     * @param userBean
     */
    public static void saveUserBaseInfo(UserBean userBean){
        if (null != userBean.getNickname()){
            SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSP(Constants.User.get_display_name_key(), userBean.getNickname());
        }
        if (null != userBean.getUser_avatar()){
            SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSP(Constants.User.get_user_avatar_key(), userBean.getUser_avatar());
        }
        if (null != userBean.getAccess_token()){
            SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSP(Constants.User.get_access_token_key(), userBean.getAccess_token());
        }
        if (-1 != userBean.getId()){
            SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSInt(Constants.User.get_id_key(), userBean.getId());
        }
    }

    /**
     * 清空账号
     */
    public static void clearAcount(){
        SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSP(Constants.User.get_display_name_key(), "");
        SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSP(Constants.User.get_user_avatar_key(), "");
        SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSP(Constants.User.get_access_token_key(), "");
        SharedPreferencesUtil.getInstance(MainApplication.getInstance()).putSInt(Constants.User.get_id_key(), 0);
    }
}
