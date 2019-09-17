Step2:
2482        Object tag = v.getTag();
2483        if (tag instanceof ShortcutInfo) {
2484            onClickAppShortcut(v); // 调用快捷图标点击事件（内部做了一些内存判断，	正在安装呀（点击则是暂停）等判断）; 正常情况则直接启动App
2485        } else if (tag instanceof FolderInfo) {
2486            if (v instanceof FolderIcon) {
2487                onClickFolderIcon(v);
2488            }
2489        } else if (v == mAllAppsButton) {
2490            onClickAllAppsButton(v);
2491        } else if (tag instanceof AppInfo) {
2492            startAppShortcutOrInfoActivity(v);
2493        } else if (tag instanceof LauncherAppWidgetInfo) {
2494            if (v instanceof PendingAppWidgetHostView) {
2495                onClickPendingWidget((PendingAppWidgetHostView) v);
2496            }
2497        }
2498    }

Setp2.1: 2587    protected void onClickAppShortcut(final View v) {

2635        // Start activities 
2636        startAppShortcutOrInfoActivity(v);  // 启动APP了啦
2637
2638        if (mLauncherCallbacks != null) {
2639            mLauncherCallbacks.onClickAppShortcut(v);
2640        }

}

Setp2.2:
2643    @Thunk void startAppShortcutOrInfoActivity(View v) {
2644        Object tag = v.getTag();
2645        final ShortcutInfo shortcut;
2646        final Intent intent;
2647        if (tag instanceof ShortcutInfo) {
2648            shortcut = (ShortcutInfo) tag;
2649            intent = shortcut.intent;
2650            int[] pos = new int[2];
2651            v.getLocationOnScreen(pos);
2652            intent.setSourceBounds(new Rect(pos[0], pos[1],
2653                    pos[0] + v.getWidth(), pos[1] + v.getHeight()));
2654
2655        } else if (tag instanceof AppInfo) {
2656            shortcut = null;
2657            intent = ((AppInfo) tag).intent;
2658        } else {
2659            throw new IllegalArgumentException("Input must be a Shortcut or AppInfo");
2660        }
2661
2662        boolean success = startActivitySafely(v, intent, tag); // 安全的启动起来
2663        mStats.recordLaunch(v, intent, shortcut);
2664
2665        if (success && v instanceof BubbleTextView) {
2666            mWaitingForResume = (BubbleTextView) v;
2667            mWaitingForResume.setStayPressed(true);
2668        }
2669    }

Setp2.3:
66    private boolean startActivity(View v, Intent intent, Object tag) {
2867        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
2868        try {
2869            // Only launch using the new animation if the shortcut has not opted out (this is a
2870            // private contract between launcher and may be ignored in the future).
2871            boolean useLaunchAnimation = (v != null) &&
2872                    !intent.hasExtra(INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION);
2873            LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(this);
2874            UserManagerCompat userManager = UserManagerCompat.getInstance(this);
2875

.......

2924
2925            if (user == null || user.equals(UserHandleCompat.myUserHandle())) {
2926                // Could be launching some bookkeeping activity
2927                startActivity(intent, optsBundle);  // 启动起来，调用Activity.java的startActivity方法
2928            } else {
2929                // TODO Component can be null when shortcuts are supported for secondary user
2930                launcherApps.startActivityForProfile(intent.getComponent(), user,
2931                        intent.getSourceBounds(), optsBundle);
2932            }
2933            return true;
2934        } catch (SecurityException e) {
2935            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
2936            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
2937                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
2938                    "or use the exported attribute for this activity. "
2939                    + "tag="+ tag + " intent=" + intent, e);
2940        }
2941        return false;
2942    }


xref: /frameworks/base/core/java/android/app/Activity.java

Step3:
4171    /**
4172     * Launch a new activity.  You will not receive any information about when
4173     * the activity exits.  This implementation overrides the base version,
4174     * providing information about
4175     * the activity performing the launch.  Because of this additional
4176     * information, the {@link Intent#FLAG_ACTIVITY_NEW_TASK} launch flag is not
4177     * required; if not specified, the new activity will be added to the
4178     * task of the caller.
4179     *
4180     * <p>This method throws {@link android.content.ActivityNotFoundException}
4181     * if there was no Activity found to run the given Intent.
4182     *
4183     * @param intent The intent to start.
4184     * @param options Additional options for how the Activity should be started.
4185     * See {@link android.content.Context#startActivity(Intent, Bundle)
4186     * Context.startActivity(Intent, Bundle)} for more details.
4187     *
4188     * @throws android.content.ActivityNotFoundException
4189     *
4190     * @see {@link #startActivity(Intent)}
4191     * @see #startActivityForResult
4192     */
4193    @Override
4194    public void startActivity(Intent intent, @Nullable Bundle options) {
4195        if (options != null) {
4196            startActivityForResult(intent, -1, options);
4197        } else {
4198            // Note we want to go through this call for compatibility with
4199            // applications that may have overridden the method.
4200            startActivityForResult(intent, -1);
4201        }
4202    }

3880    /**
3881     * Launch an activity for which you would like a result when it finished.
3882     * When this activity exits, your
3883     * onActivityResult() method will be called with the given requestCode.
3884     * Using a negative requestCode is the same as calling
3885     * {@link #startActivity} (the activity is not launched as a sub-activity).
3886     *
3887     * <p>Note that this method should only be used with Intent protocols
3888     * that are defined to return a result.  In other protocols (such as
3889     * {@link Intent#ACTION_MAIN} or {@link Intent#ACTION_VIEW}), you may
3890     * not get the result when you expect.  For example, if the activity you
3891     * are launching uses the singleTask launch mode, it will not run in your
3892     * task and thus you will immediately receive a cancel result.
3893     *
3894     * <p>As a special case, if you call startActivityForResult() with a requestCode
3895     * >= 0 during the initial onCreate(Bundle savedInstanceState)/onResume() of your
3896     * activity, then your window will not be displayed until a result is
3897     * returned back from the started activity.  This is to avoid visible
3898     * flickering when redirecting to another activity.
3899     *
3900     * <p>This method throws {@link android.content.ActivityNotFoundException}
3901     * if there was no Activity found to run the given Intent.
3902     *
3903     * @param intent The intent to start.
3904     * @param requestCode If >= 0, this code will be returned in
3905     *                    onActivityResult() when the activity exits.
3906     * @param options Additional options for how the Activity should be started.
3907     * See {@link android.content.Context#startActivity(Intent, Bundle)
3908     * Context.startActivity(Intent, Bundle)} for more details.
3909     *
3910     * @throws android.content.ActivityNotFoundException
3911     *
3912     * @see #startActivity
3913     */
3914    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
3915        if (mParent == null) {
3916            Instrumentation.ActivityResult ar =
3917                mInstrumentation.execStartActivity(
3918                    this, mMainThread.getApplicationThread(), mToken, this,
3919                    intent, requestCode, options);  // 核心启动一个App(一个页面) - Instrumentation.java
						// 这里的mMainThread也是Activity类的成员变量，它的类型是ActivityThread，它代表的是应用程序的主线程，通过mMainThread.getApplicationThread获得它里面的ApplicationThread成员变量，它是一个Binder对象，后面我们会看到，ActivityManagerService会使用它来和ActivityThread来进行进程间通信。
						// 这里我们需注意的是，这里的mMainThread代表的是Launcher应用程序运行的进程。
3920            if (ar != null) {
3921                mMainThread.sendActivityResult(
3922                    mToken, mEmbeddedID, requestCode, ar.getResultCode(),
3923                    ar.getResultData());
3924            }
3925            if (requestCode >= 0) {
3926                // If this start is requesting a result, we can avoid making
3927                // the activity visible until the result is received.  Setting
3928                // this code during onCreate(Bundle savedInstanceState) or onResume() will keep the
3929                // activity hidden during this time, to avoid flickering.
3930                // This can only be done when a result is requested because
3931                // that guarantees we will get information back when the
3932                // activity is finished, no matter what happens to it.
3933                mStartedActivity = true;
3934            }
3935
3936            cancelInputsAndStartExitTransition(options);
3937            // TODO Consider clearing/flushing other event sources and events for child windows.
3938        } else {
3939            if (options != null) {
3940                mParent.startActivityFromChild(this, intent, requestCode, options);
3941            } else {
3942                // Note we want to go through this method for compatibility with
3943                // existing applications that may have overridden it.
3944                mParent.startActivityFromChild(this, intent, requestCode); // 启动App的某个页面，这就是平时我App里面调用startActiivty走的逻辑
3945            }
3946        }
3947    }


xref: /frameworks/base/core/java/android/app/Instrumentation.java

Step4:

1437    /**
1438     * Execute a startActivity call made by the application.  The default
1439     * implementation takes care of updating any active {@link ActivityMonitor}
1440     * objects and dispatches this call to the system activity manager; you can
1441     * override this to watch for the application to start an activity, and
1442     * modify what happens when it does.
1443     *
1444     * <p>This method returns an {@link ActivityResult} object, which you can
1445     * use when intercepting application calls to avoid performing the start
1446     * activity action but still return the result the application is
1447     * expecting.  To do this, override this method to catch the call to start
1448     * activity so that it returns a new ActivityResult containing the results
1449     * you would like the application to see, and don't call up to the super
1450     * class.  Note that an application is only expecting a result if
1451     * <var>requestCode</var> is &gt;= 0.
1452     *
1453     * <p>This method throws {@link android.content.ActivityNotFoundException}
1454     * if there was no Activity found to run the given Intent.
1455     *
1456     * @param who The Context from which the activity is being started.
1457     * @param contextThread The main thread of the Context from which the activity
1458     *                      is being started.
1459     * @param token Internal token identifying to the system who is starting
1460     *              the activity; may be null.
1461     * @param target Which activity is performing the start (and thus receiving
1462     *               any result); may be null if this call is not being made
1463     *               from an activity.
1464     * @param intent The actual Intent to start.
1465     * @param requestCode Identifier for this request's result; less than zero
1466     *                    if the caller is not expecting a result.
1467     * @param options Addition options.
1468     *
1469     * @return To force the return of a particular result, return an
1470     *         ActivityResult object containing the desired data; otherwise
1471     *         return null.  The default implementation always returns null.
1472     *
1473     * @throws android.content.ActivityNotFoundException
1474     *
1475     * @see Activity#startActivity(Intent)
1476     * @see Activity#startActivityForResult(Intent, int)
1477     * @see Activity#startActivityFromChild
1478     *
1479     * {@hide}
1480     */
1481    public ActivityResult execStartActivity(
1482            Context who, IBinder contextThread, IBinder token, Activity target,
1483            Intent intent, int requestCode, Bundle options) {
1484        IApplicationThread whoThread = (IApplicationThread) contextThread;
1485        Uri referrer = target != null ? target.onProvideReferrer() : null;
1486        if (referrer != null) {
1487            intent.putExtra(Intent.EXTRA_REFERRER, referrer);
1488        }
1489        if (mActivityMonitors != null) {
1490            synchronized (mSync) {
1491                final int N = mActivityMonitors.size();
1492                for (int i=0; i<N; i++) {
1493                    final ActivityMonitor am = mActivityMonitors.get(i);
1494                    if (am.match(who, null, intent)) {
1495                        am.mHits++;
1496                        if (am.isBlocking()) {
1497                            return requestCode >= 0 ? am.getResult() : null;
1498                        }
1499                        break;
1500                    }
1501                }
1502            }
1503        }
1504        try {
1505            intent.migrateExtraStreamToClipData();
1506            intent.prepareToLeaveProcess();
1507            int result = ActivityManagerNative.getDefault()   // 这里就是真的启动一个页面了， ActivityManagerNative是本地方法调用中间者.
																  // ActivityManagerNative.getDefault返回ActivityManagerService的远程接口，即ActivityManagerProxy接口
1508                .startActivity(whoThread, who.getBasePackageName(), intent,
1509                        intent.resolveTypeIfNeeded(who.getContentResolver()),
1510                        token, target != null ? target.mEmbeddedID : null,
1511                        requestCode, 0, null, options);
1512            checkStartActivityResult(result, intent);
1513        } catch (RemoteException e) {
1514            throw new RuntimeException("Failure from system", e);
1515        }
1516        return null;
1517    }

xref: /frameworks/base/core/java/android/app/ActivityManagerNative.java

Step4.1: 

.getDefault() 调用的就是如下对象的-> gDefault.get() == IActivityManager(就是这个东东)

2604    private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() {
2605        protected IActivityManager create() {
2606            IBinder b = ServiceManager.getService("activity"); // 这里获取到了ActivityManagerService服务（上面getDefault()就是这个服务，然后就可以startActivity）
2607            if (false) {
2608                Log.v("ActivityManager", "default service binder = " + b);
2609            }
2610            IActivityManager am = asInterface(b); // 这个就是通过IBinder获取了远程调用接口对象return new ActivityManagerProxy(obj);
2611            if (false) {
2612                Log.v("ActivityManager", "default service = " + am);
2613            }
2614            return am;
2615        }
2616    };


xref: /frameworks/base/core/java/android/util/Singleton.java

Step4.1.1: 

19/**
20 * Singleton helper class for lazily initialization.
21 *
22 * Modeled after frameworks/base/include/utils/Singleton.h
23 *
24 * @hide
25 */
26public abstract class Singleton<T> {
27    private T mInstance;
28
29    protected abstract T create();
30
31    public final T get() {
32        synchronized (this) {
33            if (mInstance == null) {
34                mInstance = create();
35            }
36            return mInstance;
37        }
38    }
39}

Step4.1.2: 
63    /**
64     * Cast a Binder object into an activity manager interface, generating
65     * a proxy if needed.
66     */
67    static public IActivityManager asInterface(IBinder obj) {
68        if (obj == null) {
69            return null;
70        }
71        IActivityManager in =
72            (IActivityManager)obj.queryLocalInterface(descriptor);
73        if (in != null) {
74            return in;
75        }
76
77        return new ActivityManagerProxy(obj); // 最终是获取了ActivityManagerProxy，下面的这个类
78    }
2619class ActivityManagerProxy implements IActivityManager
2620{
2621    public ActivityManagerProxy(IBinder remote)
2622    {
2623        mRemote = remote;
2624    }
2625
2626    public IBinder asBinder()
2627    {
2628        return mRemote;
2629    }
2630    ......
2631    public int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
2632            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
2633            int startFlags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
2634        Parcel data = Parcel.obtain();
2635        Parcel reply = Parcel.obtain();
2636        data.writeInterfaceToken(IActivityManager.descriptor);
2637        data.writeStrongBinder(caller != null ? caller.asBinder() : null); // 参数caller为ApplicationThread类型的Binder实体，通过Binder驱动程序进入到ActivityManagerService的startActivity函数
2638        data.writeString(callingPackage);
2639        intent.writeToParcel(data, 0);
2640        data.writeString(resolvedType);
2641        data.writeStrongBinder(resultTo);
2642        data.writeString(resultWho);
2643        data.writeInt(requestCode);
2644        data.writeInt(startFlags);
2645        if (profilerInfo != null) {
2646            data.writeInt(1);
2647            profilerInfo.writeToParcel(data, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
2648        } else {
2649            data.writeInt(0);
2650        }
2651        if (options != null) {
2652            data.writeInt(1);
2653            options.writeToParcel(data, 0);
2654        } else {
2655            data.writeInt(0);
2656        }
2657        mRemote.transact(START_ACTIVITY_TRANSACTION, data, reply, 0); // 最终通过transact实现了ACTIVITY的启动(START_ACTIVITY_TRANSACTION)
2658        reply.readException();
2659        int result = reply.readInt();
2660        reply.recycle();
2661        data.recycle();
2662        return result;
2663    }
2664    ......

Step4.2: 

xref: /frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java

3798    @Override
3799    public final int startActivity(IApplicationThread caller, String callingPackage,
3800            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
3801            int startFlags, ProfilerInfo profilerInfo, Bundle options) {
3802        return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
3803            resultWho, requestCode, startFlags, profilerInfo, options,
3804            UserHandle.getCallingUserId());
3805    }
3806
3807    @Override
3808    public final int startActivityAsUser(IApplicationThread caller, String callingPackage,
3809            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
3810            int startFlags, ProfilerInfo profilerInfo, Bundle options, int userId) {
3811        enforceNotIsolatedCaller("startActivity");
3812        userId = handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId,
3813                false, ALLOW_FULL_ONLY, "startActivity", null);
3814        // TODO: Switch to user app stacks here.
			// 408    /** Run all ActivityStacks through this */
			// 409    ActivityStackSupervisor mStackSupervisor;  Android N是ActivityStarter
3815        return mStackSupervisor.startActivityMayWait(caller, -1, callingPackage, intent,
3816                resolvedType, null, null, resultTo, resultWho, requestCode, startFlags,
3817                profilerInfo, null, null, options, false, userId, null, null);  // 最后调用ActivityStackSupervisor的startActivityMayWait
3818    }

Step4.3:

xref: /frameworks/base/services/core/java/com/android/server/am/ActivityStackSupervisor.java

925    final int startActivityMayWait(IApplicationThread caller, int callingUid,
926            String callingPackage, Intent intent, String resolvedType,
927            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
928            IBinder resultTo, String resultWho, int requestCode, int startFlags,
929            ProfilerInfo profilerInfo, WaitResult outResult, Configuration config,
930            Bundle options, boolean ignoreTargetSecurity, int userId,
931            IActivityContainer iContainer, TaskRecord inTask) {
932        // Refuse possible leaked file descriptors
933        if (intent != null && intent.hasFileDescriptors()) {
934            throw new IllegalArgumentException("File descriptors passed in Intent");
935        }
936        boolean componentSpecified = intent.getComponent() != null;
937
938        // Don't modify the client's object!
939        intent = new Intent(intent);
940
941        // Collect information about the target of the Intent.
942        ActivityInfo aInfo =
943                resolveActivity(intent, resolvedType, startFlags, profilerInfo, userId);
944
945        ActivityContainer container = (ActivityContainer)iContainer;
946        synchronized (mService) {
947            if (container != null && container.mParentActivity != null &&
948                    container.mParentActivity.state != RESUMED) {
949                // Cannot start a child activity if the parent is not resumed.
950                return ActivityManager.START_CANCELED;
951            }
952            final int realCallingPid = Binder.getCallingPid();
953            final int realCallingUid = Binder.getCallingUid();
954            int callingPid;
955            if (callingUid >= 0) {
956                callingPid = -1;
957            } else if (caller == null) {
958                callingPid = realCallingPid;
959                callingUid = realCallingUid;
960            } else {
961                callingPid = callingUid = -1;
962            }
963
964            final ActivityStack stack;
965            if (container == null || container.mStack.isOnHomeDisplay()) { // 当前只有系统主页
966                stack = mFocusedStack;
967            } else {
968                stack = container.mStack;
969            }
970            stack.mConfigWillChange = config != null && mService.mConfiguration.diff(config) != 0;
971            if (DEBUG_CONFIGURATION) Slog.v(TAG_CONFIGURATION,
972                    "Starting activity when config will change = " + stack.mConfigWillChange);
973
974            final long origId = Binder.clearCallingIdentity();
975
976            if (aInfo != null &&
977                    (aInfo.applicationInfo.privateFlags
978                            &ApplicationInfo.PRIVATE_FLAG_CANT_SAVE_STATE) != 0) {
979                // This may be a heavy-weight process!  Check to see if we already
980                // have another, different heavy-weight process running.
981                if (aInfo.processName.equals(aInfo.applicationInfo.packageName)) {
982                    if (mService.mHeavyWeightProcess != null &&
983                            (mService.mHeavyWeightProcess.info.uid != aInfo.applicationInfo.uid ||
984                            !mService.mHeavyWeightProcess.processName.equals(aInfo.processName))) {
985                        int appCallingUid = callingUid;
986                        if (caller != null) {
987                            ProcessRecord callerApp = mService.getRecordForAppLocked(caller);
988                            if (callerApp != null) {
989                                appCallingUid = callerApp.info.uid;
990                            } else {
991                                Slog.w(TAG, "Unable to find app for caller " + caller
992                                      + " (pid=" + callingPid + ") when starting: "
993                                      + intent.toString());
994                                ActivityOptions.abort(options);
995                                return ActivityManager.START_PERMISSION_DENIED;
996                            }
997                        }
998
999                        IIntentSender target = mService.getIntentSenderLocked(
1000                                ActivityManager.INTENT_SENDER_ACTIVITY, "android",
1001                                appCallingUid, userId, null, null, 0, new Intent[] { intent },
1002                                new String[] { resolvedType }, PendingIntent.FLAG_CANCEL_CURRENT
1003                                | PendingIntent.FLAG_ONE_SHOT, null);
1004
1005                        Intent newIntent = new Intent();
1006                        if (requestCode >= 0) {
1007                            // Caller is requesting a result.
1008                            newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_HAS_RESULT, true);
1009                        }
1010                        newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_INTENT,
1011                                new IntentSender(target));
1012                        if (mService.mHeavyWeightProcess.activities.size() > 0) {
1013                            ActivityRecord hist = mService.mHeavyWeightProcess.activities.get(0);
1014                            newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_CUR_APP,
1015                                    hist.packageName);
1016                            newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_CUR_TASK,
1017                                    hist.task.taskId);
1018                        }
1019                        newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_NEW_APP,
1020                                aInfo.packageName);
1021                        newIntent.setFlags(intent.getFlags());
1022                        newIntent.setClassName("android",
1023                                HeavyWeightSwitcherActivity.class.getName());
1024                        intent = newIntent;
1025                        resolvedType = null;
1026                        caller = null;
1027                        callingUid = Binder.getCallingUid();
1028                        callingPid = Binder.getCallingPid();
1029                        componentSpecified = true;
1030                        try {
1031                            ResolveInfo rInfo =
1032                                AppGlobals.getPackageManager().resolveIntent(
1033                                        intent, null,
1034                                        PackageManager.MATCH_DEFAULT_ONLY
1035                                        | ActivityManagerService.STOCK_PM_FLAGS, userId);
1036                            aInfo = rInfo != null ? rInfo.activityInfo : null;
1037                            aInfo = mService.getActivityInfoForUser(aInfo, userId);
1038                        } catch (RemoteException e) {
1039                            aInfo = null;
1040                        }
1041                    }
1042                }
1043            }
1044
1045            int res = startActivityLocked(caller, intent, resolvedType, aInfo,
1046                    voiceSession, voiceInteractor, resultTo, resultWho,
1047                    requestCode, callingPid, callingUid, callingPackage,
1048                    realCallingPid, realCallingUid, startFlags, options, ignoreTargetSecurity,
1049                    componentSpecified, null, container, inTask); // 准备启动啦，跳过去瞅瞅...
1050
1051            Binder.restoreCallingIdentity(origId);
1052
1053            if (stack.mConfigWillChange) {
1054                // If the caller also wants to switch to a new configuration,
1055                // do so now.  This allows a clean switch, as we are waiting
1056                // for the current activity to pause (so we will not destroy
1057                // it), and have not yet started the next activity.
1058                mService.enforceCallingPermission(android.Manifest.permission.CHANGE_CONFIGURATION,
1059                        "updateConfiguration()");
1060                stack.mConfigWillChange = false;
1061                if (DEBUG_CONFIGURATION) Slog.v(TAG_CONFIGURATION,
1062                        "Updating to new configuration after starting activity.");
1063                mService.updateConfigurationLocked(config, null, false, false);
1064            }
1065
1066            if (outResult != null) {
1067                outResult.result = res;
1068                if (res == ActivityManager.START_SUCCESS) {
1069                    mWaitingActivityLaunched.add(outResult);
1070                    do {
1071                        try {
1072                            mService.wait();
1073                        } catch (InterruptedException e) {
1074                        }
1075                    } while (!outResult.timeout && outResult.who == null);
1076                } else if (res == ActivityManager.START_TASK_TO_FRONT) {
1077                    ActivityRecord r = stack.topRunningActivityLocked(null);
1078                    if (r.nowVisible && r.state == RESUMED) {
1079                        outResult.timeout = false;
1080                        outResult.who = new ComponentName(r.info.packageName, r.info.name);
1081                        outResult.totalTime = 0;
1082                        outResult.thisTime = 0;
1083                    } else {
1084                        outResult.thisTime = SystemClock.uptimeMillis();
1085                        mWaitingActivityVisible.add(outResult);
1086                        do {
1087                            try {
1088                                mService.wait();
1089                            } catch (InterruptedException e) {
1090                            }
1091                        } while (!outResult.timeout && outResult.who == null);
1092                    }
1093                }
1094            }
1095
1096            return res;
1097        }
1098    }
1399    final int startActivityLocked(IApplicationThread caller,
1400            Intent intent, String resolvedType, ActivityInfo aInfo,
1401            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
1402            IBinder resultTo, String resultWho, int requestCode,
1403            int callingPid, int callingUid, String callingPackage,
1404            int realCallingPid, int realCallingUid, int startFlags, Bundle options,
1405            boolean ignoreTargetSecurity, boolean componentSpecified, ActivityRecord[] outActivity,
1406            ActivityContainer container, TaskRecord inTask) {

1675        err = startActivityUncheckedLocked(r, sourceRecord, voiceSession, voiceInteractor,
1676                startFlags, true, options, inTask); // 准备启动啦2，跳过去瞅瞅...
167

1686    }
1687

1828    final int startActivityUncheckedLocked(final ActivityRecord r, ActivityRecord sourceRecord,
1829            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, int startFlags,
1830            boolean doResume, Bundle options, TaskRecord inTask) {
2038        ActivityStack targetStack;
2083                    targetStack = intentActivity.task.stack;
2084                    targetStack.mLastPausedActivity = null;
....
2457        ActivityStack.logStartActivity(EventLogTags.AM_CREATE_ACTIVITY, r, r.task);
2458        targetStack.mLastPausedActivity = null;
2459        targetStack.startActivityLocked(r, newTask, doResume, keepCurTransition, options);
}


Step4.4:

xref: /frameworks/base/services/core/java/com/android/server/am/ActivityStack.java
1527    /**
1528     * Ensure that the top activity in the stack is resumed.
1529     *
1530     * @param prev The previously resumed activity, for when in the process
1531     * of pausing; can be null to call from elsewhere.
1532     *
1533     * @return Returns true if something is being resumed, or false if
1534     * nothing happened.
1535     */
1536    final boolean resumeTopActivityLocked(ActivityRecord prev) {
1537        return resumeTopActivityLocked(prev, null);
1538    }
1539
1540    final boolean resumeTopActivityLocked(ActivityRecord prev, Bundle options) {
1541        if (mStackSupervisor.inResumeTopActivity) {
1542            // Don't even start recursing.
1543            return false;
1544        }
1545
1546        boolean result = false;
1547        try {
1548            // Protect against recursion.
1549            mStackSupervisor.inResumeTopActivity = true;
1550            if (mService.mLockScreenShown == ActivityManagerService.LOCK_SCREEN_LEAVING) {
1551                mService.mLockScreenShown = ActivityManagerService.LOCK_SCREEN_HIDDEN;
1552                mService.updateSleepIfNeededLocked();
1553            }
1554            result = resumeTopActivityInnerLocked(prev, options);  // 这里resume应用，让其起了；然后到ActivityStackSupervisor
1555        } finally {
1556            mStackSupervisor.inResumeTopActivity = false;
1557        }
1558        return result;
1559    }
1561    private boolean resumeTopActivityInnerLocked(ActivityRecord prev, Bundle options) {
1562        if (DEBUG_LOCKSCREEN) mService.logLockScreen("");
				mStackSupervisor.startSpecificActivityLocked(next, true, false);
1664    }

Step4.4.1:
  
xref: /frameworks/base/services/core/java/com/android/server/am/ActivityStackSupervisor.java
  
1365    void startSpecificActivityLocked(ActivityRecord r,
1366            boolean andResume, boolean checkConfig) {
1367        // Is this activity's application already running?
1368        ProcessRecord app = mService.getProcessRecordLocked(r.processName,
1369                r.info.applicationInfo.uid, true);
1370
1371        r.task.stack.setLaunchTime(r);
1372
1373        if (app != null && app.thread != null) {
1374            try {
1375                if ((r.info.flags&ActivityInfo.FLAG_MULTIPROCESS) == 0
1376                        || !"android".equals(r.info.packageName)) {
1377                    // Don't add this if it is a platform component that is marked
1378                    // to run in multiple processes, because this is actually
1379                    // part of the framework so doesn't make sense to track as a
1380                    // separate apk in the process.
1381                    app.addPackage(r.info.packageName, r.info.applicationInfo.versionCode,
1382                            mService.mProcessStats);
1383                }
1384                realStartActivityLocked(r, app, andResume, checkConfig);
1385                return;
1386            } catch (RemoteException e) {
1387                Slog.w(TAG, "Exception when starting activity "
1388                        + r.intent.getComponent().flattenToShortString(), e);
1389            }
1390
1391            // If a dead object exception was thrown -- fall through to
1392            // restart the application.
1393        }
1394        // 然后到这里就是去启动一个App进程了...AMS调用startProcessLocked将会为应用创建一个新的进程并在该进程中启动activity
1395        mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0,
1396                "activity", r.intent.getComponent(), false, false, true);
1397    }


Step4.4.2:
xref: /frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java

3197    private final void startProcessLocked(ProcessRecord app, String hostingType,
3198            String hostingNameStr, String abiOverride, String entryPoint, String[] entryPointArgs) {
3199        long startTime = SystemClock.elapsedRealtime();
3200        if (app.pid > 0 && app.pid != MY_PID) {
3201            checkTime(startTime, "startProcess: removing from pids map");
3202            synchronized (mPidsSelfLocked) {
3203                mPidsSelfLocked.remove(app.pid);
3204                mHandler.removeMessages(PROC_START_TIMEOUT_MSG, app);
3205            }
3206            checkTime(startTime, "startProcess: done removing from pids map");
3207            app.setPid(0);
3208        }
            // 指定了一个进程绑定的Activity的入口ActivityThread
3209		if (entryPoint == null) entryPoint = "android.app.ActivityThread";
3324            Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "Start proc: " +
3325                    app.processName);
3210        if (DEBUG_PROCESSES && mProcessesOnHold.contains(app)) Slog.v(TAG_PROCESSES,
3211                "startProcessLocked removing on hold: " + app);
3212        mProcessesOnHold.remove(app);
3326            checkTime(startTime, "startProcess: asking zygote to start proc");
				// Process.start创建一个进程，新的进程会导入android.app.ActivityThread类android.app.ActivityThread，并且执行它的main函数
3327            Process.ProcessStartResult startResult = Process.start(entryPoint,
3328                    app.processName, uid, uid, gids, debugFlags, mountExternal,
3329                    app.info.targetSdkVersion, app.info.seinfo, requiredAbi, instructionSet,
3330                    app.info.dataDir, entryPointArgs);
3331            checkTime(startTime, "startProcess: returned from zygote!");
3332            Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
3333
3334            if (app.isolated) {
3335                mBatteryStatsService.addIsolatedUid(app.uid, app.info.uid);
3336            }
3337            mBatteryStatsService.noteProcessStart(app.processName, app.info.uid);
3338            checkTime(startTime, "startProcess: done updating battery stats");
3339
3340            EventLog.writeEvent(EventLogTags.AM_PROC_START,
3341                    UserHandle.getUserId(uid), startResult.pid, uid,
3342                    app.processName, hostingType,
3343                    hostingNameStr != null ? hostingNameStr : "");
3344
3345            if (app.persistent) {
3346                Watchdog.getInstance().processStarted(app.processName, startResult.pid);
3347            }
3348
3349            checkTime(startTime, "startProcess: building log message");
3350            StringBuilder buf = mStringBuilder;
3351            buf.setLength(0);
3352            buf.append("Start proc ");
3353            buf.append(startResult.pid);
3354            buf.append(':');
3355            buf.append(app.processName);
3356            buf.append('/');
3357            UserHandle.formatUid(buf, uid);
3358            if (!isActivityProcess) {
3359                buf.append(" [");
3360                buf.append(entryPoint);
3361                buf.append("]");
3362            }
3363            buf.append(" for ");
3364            buf.append(hostingType);
3365            if (hostingNameStr != null) {
3366                buf.append(" ");
3367                buf.append(hostingNameStr);
3368            }
3369            Slog.i(TAG, buf.toString());
3370            app.setPid(startResult.pid);
3371            app.usingWrapper = startResult.usingWrapper;
3372            app.removed = false;
3373            app.killed = false;
3374            app.killedByAm = false;

3395    }

Step4.5: // 经过一系列跳转，终于来到了ActivityThread

xref: /frameworks/base/core/java/android/app/ActivityThread.java

275    static Handler sMainThreadHandler;  // set once in main()  这个全局可以使用

5379    public static void main(String[] args) {
5380        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "ActivityThreadMain");
5381        SamplingProfilerIntegration.start();
5382
5383        // CloseGuard defaults to true and can be quite spammy.  We
5384        // disable it here, but selectively enable it later (via
5385        // StrictMode) on debug builds, but using DropBox, not logs.
5386        CloseGuard.setEnabled(false);
5387
5388        Environment.initForCurrentUser();
5389
5390        // Set the reporter for event logging in libcore
5391        EventLogger.setReporter(new EventLoggingReporter());
5392
5393        AndroidKeyStoreProvider.install();
5394
5395        // Make sure TrustedCertificateStore looks in the right place for CA certificates
5396        final File configDir = Environment.getUserConfigDirectory(UserHandle.myUserId());
5397        TrustedCertificateStore.setDefaultUserDirectory(configDir);
5398
5399        Process.setArgV0("<pre-initialized>");
5400
5401        Looper.prepareMainLooper(); // 混Looper眼熟的时候就会看这里，但是不知道ActivityThread怎么启动的鸭！
5402
5403        ActivityThread thread = new ActivityThread();
5404        thread.attach(false); // 这个地方就是启动的关键了！
5405
5406        if (sMainThreadHandler == null) {
5407            sMainThreadHandler = thread.getHandler();
5408        }
5409
5410        if (false) {
5411            Looper.myLooper().setMessageLogging(new
5412                    LogPrinter(Log.DEBUG, "ActivityThread"));
5413        }
5414
5415        // End of event ActivityThreadMain.
5416        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
5417        Looper.loop();
5418
5419        throw new RuntimeException("Main thread loop unexpectedly exited");
5420    }

5230    private void attach(boolean system) {
5231        sCurrentActivityThread = this;
5232        mSystemThread = system;
5233        if (!system) {
5234            ViewRootImpl.addFirstDrawHandler(new Runnable() {
5235                @Override
5236                public void run() {
5237                    ensureJitEnabled();
5238                }
5239            });
5240            android.ddm.DdmHandleAppName.setAppName("<pre-initialized>",
5241                                                    UserHandle.myUserId());
5242            RuntimeInit.setApplicationObject(mAppThread.asBinder());
5243            final IActivityManager mgr = ActivityManagerNative.getDefault();
5244            try {
5245                mgr.attachApplication(mAppThread);  // 最终调用的是ActivityManagerNative的attachApplication 
5246            } catch (RemoteException ex) {
5247                // Ignore
5248            }
5249            // Watch for getting close to heap limit.
5250            BinderInternal.addGcWatcher(new Runnable() {
5251                @Override public void run() {
5252                    if (!mSomeActivitiesChanged) {
5253                        return;
5254                    }
5255                    Runtime runtime = Runtime.getRuntime();
5256                    long dalvikMax = runtime.maxMemory();
5257                    long dalvikUsed = runtime.totalMemory() - runtime.freeMemory();
5258                    if (dalvikUsed > ((3*dalvikMax)/4)) {
5259                        if (DEBUG_MEMORY_TRIM) Slog.d(TAG, "Dalvik max=" + (dalvikMax/1024)
5260                                + " total=" + (runtime.totalMemory()/1024)
5261                                + " used=" + (dalvikUsed/1024));
5262                        mSomeActivitiesChanged = false;
5263                        try {
5264                            mgr.releaseSomeActivities(mAppThread);
5265                        } catch (RemoteException e) {
5266                        }
5267                    }
5268                }
5269            });
5270        } else {
5271           
5285        }
5286
5287        // add dropbox logging to libcore
5288        DropBox.setReporter(new DropBoxReporter());
5289
5290        ViewRootImpl.addConfigCallback(new ComponentCallbacks2() {
5291            @Override
5292            public void onConfigurationChanged(Configuration newConfig) {
5293                synchronized (mResourcesManager) {
5294                    // We need to apply this change to the resources
5295                    // immediately, because upon returning the view
5296                    // hierarchy will be informed about it.
5297                    if (mResourcesManager.applyConfigurationToResourcesLocked(newConfig, null)) {
5298                        // This actually changed the resources!  Tell
5299                        // everyone about it.
5300                        if (mPendingConfiguration == null ||
5301                                mPendingConfiguration.isOtherSeqNewer(newConfig)) {
5302                            mPendingConfiguration = newConfig;
5303
5304                            sendMessage(H.CONFIGURATION_CHANGED, newConfig);
5305                        }
5306                    }
5307                }
5308            }
5309            @Override
5310            public void onLowMemory() {
5311            }
5312            @Override
5313            public void onTrimMemory(int level) {
5314            }
5315        });
5316    }

Step4.5.1: 然后通过ActivityManagerNative调用ActivityManagerService.java的
xref: /frameworks/base/core/java/android/app/ActivityManagerNative.java

3093    public void attachApplication(IApplicationThread app) throws RemoteException
3094    {
3095        Parcel data = Parcel.obtain();
3096        Parcel reply = Parcel.obtain();
3097        data.writeInterfaceToken(IActivityManager.descriptor);
3098        data.writeStrongBinder(app.asBinder());
3099        mRemote.transact(ATTACH_APPLICATION_TRANSACTION, data, reply, 0); // 通过binder调用到ActivityManagerService的attachApplication
3100        reply.readException();
3101        data.recycle();
3102        reply.recycle();
3103    }

xref: /frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java

6184    @Override
6185    public final void attachApplication(IApplicationThread thread) {
6186        synchronized (this) {
6187            int callingPid = Binder.getCallingPid();
6188            final long origId = Binder.clearCallingIdentity();
6189            attachApplicationLocked(thread, callingPid);
6190            Binder.restoreCallingIdentity(origId);
6191        }
6192    }
5961    private final boolean attachApplicationLocked(IApplicationThread thread,
5962            int pid) {
6122
6123        // See if the top visible activity is waiting to run in this process...
6124        if (normalMode) {
6125            try {
6126                if (mStackSupervisor.attachApplicationLocked(app)) { // 又到了ActivityStackSupervisor.java
6127                    didSomething = true;
6128                }
6129            } catch (Exception e) {
6130                Slog.wtf(TAG, "Exception thrown launching activities in " + app, e);
6131                badApp = true;
6132            }
6133        }
....
}

Step4.5.2: 
xref: /frameworks/base/services/core/java/com/android/server/am/ActivityStackSupervisor.java

610    boolean attachApplicationLocked(ProcessRecord app) throws RemoteException {
611        final String processName = app.processName;
612        boolean didSomething = false;
613        for (int displayNdx = mActivityDisplays.size() - 1; displayNdx >= 0; --displayNdx) {
614            ArrayList<ActivityStack> stacks = mActivityDisplays.valueAt(displayNdx).mStacks;
615            for (int stackNdx = stacks.size() - 1; stackNdx >= 0; --stackNdx) {
616                final ActivityStack stack = stacks.get(stackNdx);
617                if (!isFrontStack(stack)) {
618                    continue;
619                }
620                ActivityRecord hr = stack.topRunningActivityLocked(null);
621                if (hr != null) {
622                    if (hr.app == null && app.uid == hr.info.applicationInfo.uid
623                            && processName.equals(hr.processName)) {
624                        try {
625                            if (realStartActivityLocked(hr, app, true, true)) { // 真正的启动Activity了...
626                                didSomething = true;
627                            }
628                        } catch (RemoteException e) {
629                            Slog.w(TAG, "Exception in new application when starting activity "
630                                  + hr.intent.getComponent().flattenToShortString(), e);
631                            throw e;
632                        }
633                    }
634                }
635            }
636        }
637        if (!didSomething) {
638            ensureActivitiesVisibleLocked(null, 0);
639        }
640        return didSomething;
641    }
1179    final boolean realStartActivityLocked(ActivityRecord r,
1180            ProcessRecord app, boolean andResume, boolean checkConfig)
1181            throws RemoteException {
1182          
.....           // IApplicationThread thread;  // the actual proc...  may be null only if
				 // 'persistent' is true (in which case we
				// are in the process of launching the app)
1283            app.forceProcessStateUpTo(mService.mTopProcessState);
				// 我要启动页面了，忒么终于启动了; app通过ProcessRecord查看变量，如上面描述.IApplicationThread
1284            app.thread.scheduleLaunchActivity(new Intent(r.intent), r.appToken,
1285                    System.identityHashCode(r), r.info, new Configuration(mService.mConfiguration),
1286                    new Configuration(stack.mOverrideConfig), r.compat, r.launchedFromPackage,
1287                    task.voiceInteractor, app.repProcState, r.icicle, r.persistentState, results,
1288                    newIntents, !andResume, mService.isNextTransitionForward(), profilerInfo);
1289
.....
1361
1362        return true;
1363    }

Step4.5.3: 
xref: /frameworks/base/core/java/android/app/IApplicationThread.java
xref: /frameworks/base/core/java/android/app/ActivityThread.java

627        // we use token to identify this activity without having to send the
628        // activity itself back to the activity manager. (matters more with ipc)
629        @Override
630        public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident,
631                ActivityInfo info, Configuration curConfig, Configuration overrideConfig,
632                CompatibilityInfo compatInfo, String referrer, IVoiceInteractor voiceInteractor,
633                int procState, Bundle state, PersistableBundle persistentState,
634                List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents,
635                boolean notResumed, boolean isForward, ProfilerInfo profilerInfo) {
636
637            updateProcessState(procState, false);
638
639            ActivityClientRecord r = new ActivityClientRecord();
640
641            r.token = token;
642            r.ident = ident;
643            r.intent = intent;
644            r.referrer = referrer;
645            r.voiceInteractor = voiceInteractor;
646            r.activityInfo = info;
647            r.compatInfo = compatInfo;
648            r.state = state;
649            r.persistentState = persistentState;
650
651            r.pendingResults = pendingResults;
652            r.pendingIntents = pendingNewIntents;
653
654            r.startsNotResumed = notResumed;
655            r.isForward = isForward;
656
657            r.profilerInfo = profilerInfo;
658
659            r.overrideConfig = overrideConfig;
660            updatePendingConfiguration(curConfig);
661
662            sendMessage(H.LAUNCH_ACTIVITY, r);
663        }
1227    private class H extends Handler {
1278
1279        String codeToString(int code) {
1334        }
1335        public void handleMessage(Message msg) {
1336            if (DEBUG_MESSAGES) Slog.v(TAG, ">>> handling: " + codeToString(msg.what));
1337            switch (msg.what) {
1338                case LAUNCH_ACTIVITY: {
1339                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
1340                    final ActivityClientRecord r = (ActivityClientRecord) msg.obj;
1341
1342                    r.packageInfo = getPackageInfoNoCheck(
1343                            r.activityInfo.applicationInfo, r.compatInfo);
1344                    handleLaunchActivity(r, null); // 处理启动
1345                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
1346                } break;

2456    private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {
2457        // If we are getting ready to gc after going to the background, well
2458        // we are back active so skip it.
....
2475
			// performLaunchActivity函数来加载Activity，然后调用它的onCreate函数
2476        Activity a = performLaunchActivity(r, customIntent);
2477
2478        if (a != null) {
2479            r.createdConfig = new Configuration(mConfiguration);
2480            Bundle oldState = r.state;
2481            handleResumeActivity(r.token, false, r.isForward,
2482                    !r.activity.mFinished && !r.startsNotResumed); // 可交互处理Resumed状态，即会调用这个Activity的onResume函数，这是遵循Activity的生命周期的
....
2525            }
2526        } else {
2527            // If there was an error, for any reason, tell the activity
2528            // manager to stop us.
2529            try {
2530                ActivityManagerNative.getDefault()
2531                    .finishActivity(r.token, Activity.RESULT_CANCELED, null, false);
2532            } catch (RemoteException ex) {
2533                // Ignore
2534            }
2535        }
2536    }

2293    private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
2294        // System.out.println("##### [" + System.currentTimeMillis() + "] ActivityThread.performLaunchActivity(" + r + ")");
2295

2313
2314        Activity activity = null;
2315        try {
2316            java.lang.ClassLoader cl = r.packageInfo.getClassLoader();
2317            activity = mInstrumentation.newActivity(
2318                    cl, component.getClassName(), r.intent);
2319            StrictMode.incrementExpectedActivityCount(activity.getClass());
2320            r.intent.setExtrasClassLoader(cl);
2321            r.intent.prepareToEnterProcess();
2322            if (r.state != null) {
2323                r.state.setClassLoader(cl);
2324            }
2325        } catch (Exception e) {
2326            if (!mInstrumentation.onException(activity, e)) {
2327                throw new RuntimeException(
2328                    "Unable to instantiate activity " + component
2329                    + ": " + e.toString(), e);
2330            }
2331        }
2332
2333        try {
				// Application创建（每个应用都有，如果不指定，则默认也会创建）
2334            Application app = r.packageInfo.makeApplication(false, mInstrumentation);
2335
2336            if (localLOGV) Slog.v(TAG, "Performing launch of " + r);
2337            if (localLOGV) Slog.v(
2338                    TAG, r + ": app=" + app
2339                    + ", appName=" + app.getPackageName()
2340                    + ", pkg=" + r.packageInfo.getPackageName()
2341                    + ", comp=" + r.intent.getComponent().toShortString()
2342                    + ", dir=" + r.packageInfo.getAppDir());
2343
2344            if (activity != null) {
2345                Context appContext = createBaseContextForActivity(r, activity);
2346                CharSequence title = r.activityInfo.loadLabel(appContext.getPackageManager());
2347                Configuration config = new Configuration(mCompatConfiguration);
2348                if (DEBUG_CONFIGURATION) Slog.v(TAG, "Launching activity "
2349                        + r.activityInfo.name + " with config " + config);
					// 这里Application会和activity进行绑定
2350                activity.attach(appContext, this, getInstrumentation(), r.token,
2351                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
2352                        r.embeddedID, r.lastNonConfigurationInstances, config,
2353                        r.referrer, r.voiceInteractor);
2354
2355                if (customIntent != null) {
2356                    activity.mIntent = customIntent;
2357                }
2358                r.lastNonConfigurationInstances = null;
2359                activity.mStartedActivity = false;
2360                int theme = r.activityInfo.getThemeResource();
2361                if (theme != 0) {
2362                    activity.setTheme(theme);
2363                }
2364
2365                activity.mCalled = false;
					// 调用Activity的OnCreate生命周期
2366                if (r.isPersistable()) {
2367                    mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
2368                } else {
2369                    mInstrumentation.callActivityOnCreate(activity, r.state);
2370                }
2371                if (!activity.mCalled) {
2372                    throw new SuperNotCalledException(
2373                        "Activity " + r.intent.getComponent().toShortString() +
2374                        " did not call through to super.onCreate()");
2375                }
2376                r.activity = activity;
2377                r.stopped = true;
2378                if (!r.activity.mFinished) {
2379                    activity.performStart();
2380                    r.stopped = false;
2381                }
2382                if (!r.activity.mFinished) {
2383                    if (r.isPersistable()) {
2384                        if (r.state != null || r.persistentState != null) {
2385                            mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state,
2386                                    r.persistentState);
2387                        }
2388                    } else if (r.state != null) {
2389                        mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state);
2390                    }
2391                }
2392                if (!r.activity.mFinished) {
2393                    activity.mCalled = false;
2394                    if (r.isPersistable()) {
2395                        mInstrumentation.callActivityOnPostCreate(activity, r.state,
2396                                r.persistentState);
2397                    } else {
2398                        mInstrumentation.callActivityOnPostCreate(activity, r.state);
2399                    }
2400                    if (!activity.mCalled) {
2401                        throw new SuperNotCalledException(
2402                            "Activity " + r.intent.getComponent().toShortString() +
2403                            " did not call through to super.onPostCreate()");
2404                    }
2405                }
2406            }
2407            r.paused = true;
2408
2409            mActivities.put(r.token, r);
2410
2411        } catch (SuperNotCalledException e) {
2412            throw e;
2413
2414        } catch (Exception e) {
2415            if (!mInstrumentation.onException(activity, e)) {
2416                throw new RuntimeException(
2417                    "Unable to start activity " + component
2418                    + ": " + e.toString(), e);
2419            }
2420        }
2421
2422        return activity;
2423    }