package com.lf.inao.utils;

import com.lf.inao.hook.XposedInit.HostEnv;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;

public class ClassUtils {
    private static final Map<String, Class<?>> classMap = new HashMap<>();

    public static ClassLoader hostLoader;
    public static ClassLoader moduleLoader;


    public static Class<?> getClass(String className) {
        //类缓存里有这个类就直接返回
        Class<?> clazz = classMap.get(className);
        if (clazz != null) {
            return clazz;
        }
        try {
            if (className.equals("void"))
                clazz = void.class;
            else {
                clazz = XposedHelpers.findClass(className, hostLoader);
            }
            classMap.put(className, clazz);
            return clazz;
        } catch (Throwable throwable) {
            throw new RuntimeException("没有找到类: " + className);
        }
    }

    public static Class<?> load(ClassLoader loader, String className) {
        //类缓存里有这个类就直接返回
        Class<?> clazz = classMap.get(className);
        if (clazz != null) {
            return clazz;
        }
        if (className.equals("void"))
            clazz = void.class;
        else {
            clazz = XposedHelpers.findClass(className, loader);
        }
        classMap.put(className, clazz);
        return clazz;
    }

    public static void setHostClassLoader(ClassLoader loader) {
        hostLoader = loader;
    }

    public static void setModuleLoader(ClassLoader loader) {
        moduleLoader = loader;
    }
}
