package com.lf.inao.hook.XposedInit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lf.inao.R;
import com.lf.inao.ui.ModuleSettingActivity;
import com.lf.inao.utils.ClassUtils;
import com.lf.inao.utils.ConstructorUtils;
import com.lf.inao.utils.MethodUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Hook程序入口
 */

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static StartupParam cacheParam;


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 过滤Hook的软件
        if (!lpparam.packageName.equals("com.tencent.mobileqq")) return;
        XposedBridge.log("Loaded app: " + lpparam.packageName);

        if (!lpparam.isFirstApplication) return;

        if (cacheParam == null) {
            XposedBridge.log("[iNao]initZygote may not be invoke, please check your Xposed Framework!");
            return;
        }
        HostEnv.VERSION_NAME = "V001";
        HostEnv.ProcessName = lpparam.processName;
        HostEnv.AppDataDirPath = lpparam.appInfo.dataDir;

        ClassUtils.hostLoader = lpparam.classLoader;
        ClassUtils.moduleLoader = this.getClass().getClassLoader();

        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.qfix.QFixApplication", ClassUtils.hostLoader, "onCreate", new XC_MethodHook() {
            public static final AtomicBoolean isInit = new AtomicBoolean();

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ClassUtils.hostLoader = param.thisObject.getClass().getClassLoader();
//                HostEnv.AppContext = (Context) param.args[0];
                if (isInit.getAndSet(true)) return;
                init(lpparam);
            }
        });

    }

    private void init(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        initSettingHook(lpparam);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        cacheParam = startupParam;
    }

    private void initSettingHook(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        XposedBridge.hookMethod(
                MethodUtils.findMethod(ClassUtils.getClass("com.tencent.mobileqq.activity.QQSettingSettingActivity"),
                        "doOnCreate", boolean.class, new Class[]{Bundle.class}), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        {
                            // 获取当前Activity信息
                            Activity activity = (Activity) param.thisObject;
                            Context context = (Context) param.thisObject;
                            // 获取账户设置项目的对象
                            int resId = context.getResources().getIdentifier("account_switch", "id", lpparam.packageName);
                            ViewGroup accountItem = (ViewGroup) activity.findViewById(resId).getParent();
                            // 获取设置页面最外层列表
                            ViewGroup viewGroup = (ViewGroup) accountItem.getParent();
                            // 获取Item的类，创建模块入口Item
                            Class<?> clazz = ClassUtils.getClass("com.tencent.mobileqq.widget.FormSimpleItem");
                            View initView = ConstructorUtils.newInstance(clazz, context);
                            // 获取Item类中设置Item的方法
                            Method setLeftText = MethodUtils.findMethod(clazz, "setLeftText", void.class, new Class[]{CharSequence.class});
                            Method setRightText = MethodUtils.findMethod(clazz, "setRightText", void.class, new Class[]{CharSequence.class});
//                          Method setLeftIcon = MethodUtils.findMethod(clazz,"setLeftIcon",void.class,new Class[]{Drawable.class});
                            Method setLeftTextColor = MethodUtils.findMethod(clazz, "setLeftTextColor", void.class, new Class[]{int.class}); // (参数可选：0 黑色 1 蓝色 2 灰色 4 蓝色 5 白色)

                            // 设置入口Item
                            initView.setId(R.id.iNao_Setting);   // 设置出来的id类型是错误的，不知道为什么T_T
                            setLeftText.invoke(initView, "iNao");
                            setRightText.invoke(initView, HostEnv.VERSION_NAME);
                            setLeftTextColor.invoke(initView, 1);
                            // 监听入口被点击事件
                            initView.setOnClickListener(v -> {
                                try {
                                    Intent intent = new Intent(activity, ModuleSettingActivity.class);
                                    activity.startActivity(intent);
                                } catch (Exception e){
                                    Toast.makeText((Activity) param.thisObject, "[iNao]" + e, Toast.LENGTH_SHORT).show();
                                }
//                                Toast.makeText((Activity) param.thisObject, "[iNao]模块加载成功！", Toast.LENGTH_SHORT).show();
                            });
                            // 构造布局参数
                            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
                            marginLayoutParams.bottomMargin = 40;
                            // 获取accountItem索引
                            int index = 0;
                            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                                if (viewGroup.getChildAt(i) == accountItem) {
                                    index = i + 1;
                                    break;
                                }
                            }
                            // 添加视图
                            viewGroup.addView(initView, index + 1, marginLayoutParams);
                        }
                    }
                });
    }


}
