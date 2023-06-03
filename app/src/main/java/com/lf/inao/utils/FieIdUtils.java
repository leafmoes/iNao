package com.lf.inao.utils;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FieIdUtils {
    private static final HashMap<String, Field> FIELD_CACHE = new HashMap<>();

    public static void setField(Object target, String FieldName, Object v) throws Exception {
        setField(target, target.getClass(), FieldName, v.getClass(), v);
    }

    public static void setField(Object target, String fieldName, Class<?> type, Object v) throws Exception {
        setField(target, target.getClass(), fieldName, type, v);
    }

    public static void setField(Object CheckObj, Class<?> CheckClass, String FieldName, Class<?> FieldClass, Object Value) throws Exception {
        String SignText = CheckClass.getName() + " " + FieldName;
        if (FIELD_CACHE.containsKey(SignText)) {
            Field field = FIELD_CACHE.get(SignText);
            field.set(CheckObj, Value);
            return;
        }
        Class<?> Check = CheckClass;
        while (Check != null) {
            for (Field f : Check.getDeclaredFields()) {
                if (f.getName().equals(FieldName)) {
                    if (CheckClassType.CheckClass(f.getType(), FieldClass)) {
                        f.setAccessible(true);
                        FIELD_CACHE.put(SignText, f);
                        f.set(CheckObj, Value);
                        return;
                    }
                }
            }
            Check = Check.getSuperclass();
        }
        throw new RuntimeException("查找不到字段 : " + SignText);
    }

    public static <T> T getFirstField(Object checkObj, Class<?> type) throws Exception {
        return getFirstField(checkObj, checkObj.getClass(), type);
    }

    public static <T> T getFirstField(Object CheckObj, Class<?> CheckClass, Class<?> FieldClass) throws Exception {
        String key = CheckClass.getName() + " type(" + FieldClass.getName() + ")";
        if (FIELD_CACHE.containsKey(key)) {
            Field f = FIELD_CACHE.get(key);
            return (T) f.get(CheckObj);
        }
        Class<?> Check = CheckClass;
        while (Check != null) {
            for (Field f : Check.getDeclaredFields()) {
                if (FieldClass == f.getType()) {
                    FIELD_CACHE.put(key, f);
                    f.setAccessible(true);
                    return (T) f.get(CheckObj);
                }
            }
            Check = Check.getSuperclass();
        }
        throw new RuntimeException("查找不到字段 " + "(" + FieldClass.getName() + ") in class " + CheckClass.getName());
    }

    public static <T> T getField(Object object, String name, Class<?> FieldClass) throws Exception {
        return getField(object, object.getClass(), name, FieldClass);
    }

    public static <T> T getField(Object CheckObj, Class<?> CheckClass, String FieldName, Class<?> FieldClass) throws Exception {
        String key = CheckClass.getName() + " Name=" + FieldName;
        if (FIELD_CACHE.containsKey(key)) {
            return (T) FIELD_CACHE.get(key).get(CheckObj);
        }
        Class<?> Check = CheckClass;
        while (Check != null) {
            for (Field f : Check.getDeclaredFields()) {
                if (f.getName().equals(FieldName)) {
                    if (CheckClassType.CheckClass(f.getType(), FieldClass)) {
                        f.setAccessible(true);
                        FIELD_CACHE.put(key, f);
                        return (T) f.get(CheckObj);
                    }
                }
            }
            Check = Check.getSuperclass();
        }
        throw new RuntimeException("查找不到字段 " + FieldName + "(" + FieldClass.getName() + ") in class " + CheckClass.getName());
    }

    public static <T> T getStaticFieId(Class<?> clazz, String name) {
        String key = clazz.getName() + ":" + name;
        if (FIELD_CACHE.containsKey(key)) {
            Field f = FIELD_CACHE.get(key);
            try {
                return (T) f.get(null);
            } catch (IllegalAccessException ignored) {

            }
        }
        try {
            Class<?> clz = clazz;
            while (clz != null) {
                for (Field field : clz.getDeclaredFields()) {
                    if (field.getName().equals(name)) {
                        field.setAccessible(true);
                        return (T) field.get(null);
                    }
                }
                clz = clz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("获取静态字段异常 " + key);
    }

    public static Field findFirstField(Class<?> ObjClass, Class<?> FieldType) {
        Class<?> FindClass = ObjClass;
        String key = ObjClass.getName() + " type" + "=" + FieldType.getName();
        if (FIELD_CACHE.containsKey(key)) {
            return FIELD_CACHE.get(key);
        }
        while (FindClass != null) {
            for (Field f : FindClass.getDeclaredFields()) {
                if (f.getType().equals(FieldType)) {
                    f.setAccessible(true);
                    FIELD_CACHE.put(key, f);
                    return f;
                }
            }
            FindClass = FindClass.getSuperclass();
        }
        throw new RuntimeException("查找首个字段(不含内容)异常 " + key);
    }

    public static Field findField(Class<?> ObjClass, String FieldName, Class<?> FieldType) {
        String key = ObjClass.getName() + " " + FieldName + "=" + FieldType.getName();
        if (FIELD_CACHE.containsKey(key)) {
            return FIELD_CACHE.get(key);
        }
        Class<?> FindClass = ObjClass;
        while (FindClass != null) {
            for (Field f : FindClass.getDeclaredFields()) {
                if (f.getName().equals(FieldName) && f.getType().equals(FieldType)) {
                    f.setAccessible(true);
                    FIELD_CACHE.put(key, f);
                    return f;
                }
            }
            FindClass = FindClass.getSuperclass();
        }
        throw new RuntimeException("查找字段(不含内容)异常 " + key);
    }

}