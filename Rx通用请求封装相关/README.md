# DocPro

#### 项目介绍
通用网络请求封装参考 - 基于Rx

#### 使用说明
内部相关错误代码，请酌情删除即可； 
我自己额外增加了一些项目需求的参数，以及增加了控件点击请求后相关动画的处理，所以有第三方库的使用，不采用即可；
主要是封装的大概想法是这样，仅供参考！

```Java
    /**
     * 请求服务
     */
    protected BaseControlPresenter presenter;
	
	presenter = new BaseControlPresenter<Object>(this);
	
	// 比如获取验证码
	presenter
                    .addParam("mobile", mobileNumber)
                    .addParam("type", type + "")
                    .postData(MobileCodeBean.class, new ResultCallBack<String>() {},null,false);
					
	// 比如账号密码登录、bindView用来处理请求过程动画
            presenter.bindView(LoginBean.class, v,
                                new Rect(88, 10, 88, 10),
                                new Rect(88, 10, 0, 10))
                    .addParam("login_name", inputPhone.getText().toString())
                    .addParam("password", inputPassEt.getText().toString())
                    .requestData(LoginBean.class, new ResultCallBack<UserBean>() {
                    }, null, false);	

	// 比如列表请求request形式，还有postDataList形式，以及特殊的请求处理的接口封装提供
    baseControlPresenter.addParam("type", 1) // 合作活动
                    .requestDataList(PromotionListBean.class, new ResultCallBack<List<PromotionListBean>>() {
                    }, currentPageIndex, bIsRefresh);	

	// 比如列表请求，但是列表包含在对象当中，同时此时不分页，则如下使用；
    baseControlPresenter
                .requestDataListData(ActivityHomeBean.class,
                        new ResultCallBack<ActivityHomeBean>() {
                        },
                        -1, false);			
    
    // 基本上目前接口类型满足了所有分页，不分页，只有一页，以及特殊参数的情况。。。根据实际项目后台可以修改完善；
	 当然还可以封装的更简洁，这意味着内部需要处理更多的情况，或许不太建议封装太高度了！那样内部维护也麻烦；	
```

配置新的方法
```Java
/*
 *@Description: 方法url管理+类型转换管理
 *@Author: hl
 *@Time: 2018/11/22 16:18
 */
public class NetUrlManager {
    private static HashMap<Class, String> classStringHashMap = new HashMap<Class, String>() {
        {
            put(xxxxx.class, "test");
	    }
	......
```

结果回调处理
```Java
    比如用户登录回调处理
    @Override
    public <T> void onSucess(Class _dataModelclass, T t) {
        super.onSucess(_dataModelclass, t);
        if (UserBean.class.isAssignableFrom(_dataModelclass)) {
            UserBean userBean = (UserBean) t;
            Log.e("test", userBean.getAccess_token());
            ///< 存储用户基本信息
            UserInfoManager.saveUserBaseInfo(
                    userBean.getDisplay_name(), userBean.getUser_phone(),
                    userBean.getUser_avatar(), userBean.getAccess_token(),
                    userBean.getUser_sign(), userBean.getId());
            // 广播出去，以便需要的地方更新相关用户状态(目前主要个人中心)
            userBean.setDrawType(1);
            EventBus.getDefault().post(userBean);
            finish();
        }
    }
	
	比如列表请求回调处理
	@Override
    public <T> void onSucess(Class _dataModelclass, List<T> listT) {
        if (_dataModelclass.getName().equals(FengHuiListBean.class.getName())) {
            List<FengHuiListBean> fengHuiListBeanList = (List<FengHuiListBean>) listT;
            for (int i = 0; i < fengHuiListBeanList.size(); ++i) {
                fengHuiListBeanList.get(i).setType(from);
                fengHuiListBeanList.get(i).setDrawType(2); // 峰会活动
            }
            mList.addAll(fengHuiListBeanList);
            activityListAdapter.notifyDataSetChanged();
        } else if (_dataModelclass.getName().equals(PromotionListBean.class.getName())) {
            List<PromotionListBean> promotionListBeanList = (List<PromotionListBean>) listT;
            /// 字段不太一样，本来想利用ActivityBean的，发现不太一样...后续再重构工程里抽出基础实体类就行
            for (int i = 0; i < promotionListBeanList.size(); ++i) {
                ActivitysBean activityBean = new ActivitysBean();
                activityBean.setId(promotionListBeanList.get(i).getId() + "");
                activityBean.setTitle(promotionListBeanList.get(i).getTitle());
                // 内部采用addr字段，但是字段不太统一; 重新做个赋值
                activityBean.setAddress(promotionListBeanList.get(i).getCity_name());
                activityBean.setStime(promotionListBeanList.get(i).getStime());
                activityBean.setStatus(promotionListBeanList.get(i).getStatus());
                activityBean.setStatus_html(promotionListBeanList.get(i).getStatus_html());
                activityBean.setPoster(promotionListBeanList.get(i).getCover());
                activityBean.setUrl(promotionListBeanList.get(i).getLink());
                activityBean.setType(from);
                activityBean.setDrawType(1); // 合作活动
                mList.add(activityBean);
            }
            activityListAdapter.notifyDataSetChanged();
        }
    }
```

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request