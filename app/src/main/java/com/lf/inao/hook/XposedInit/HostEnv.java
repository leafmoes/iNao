package com.lf.inao.hook.XposedInit;

import android.app.Application;
import android.content.Context;

public class HostEnv {
    public static Application Application;
    public static Context AppContext;
    public static String VERSION_NAME;

    public static String ProcessName;
    public static String AppDataDirPath;  //App的私有数据目录 data/user/0/com.tencent.mobileqq
    public static Boolean isMainProcess;

}
