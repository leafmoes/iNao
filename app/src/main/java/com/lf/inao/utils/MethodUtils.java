package com.lf.inao.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MethodUtils {
    private static final Map<String, Method> methodList = new HashMap<>();

    public static <T> T callNoParamsMethod(Object obj, String methodName, Class<?> returnType) throws Exception {
        Method m = findNoParamsMethod(obj.getClass(), methodName, returnType);
        return (T) m.invoke(obj);
    }

    public static <T> T callMethod(Object obj, String methodName, Class<?> returnType) throws Exception {
        Method m = findNoParamsMethod(obj.getClass(), methodName, returnType);
        if (m == null) return null;
        return (T) m.invoke(obj);
    }

    public static <T> T callMethod(Object obj, String methodName, Class<?> returnType, Class<?>[] types, Object... params) throws Exception {
        Method method = findMethod(obj.getClass(), methodName, returnType, types);
        if (method == null) return null;
        return (T) method.invoke(obj, params);

    }

    public static <T> T callStaticNoParamsMethod(Class<?> clz, String name, Class<?> ReturnType, Object... param) throws Exception {
        Method m = findNoParamsMethod(clz, name, ReturnType);
        if (m == null) return null;
        return (T) m.invoke(null, param);
    }

    public static <T> T callStaticMethod(Class<?> clz, String name, Class<?> ReturnType, Class<?>[] params, Object... param) throws Exception {
        Method m = findMethod(clz, name, ReturnType, params);
        if (m == null) return null;
        return (T) m.invoke(null, param);
    }
    public static Method findNoParamsMethod(String FindClass, String MethodName, Class<?> ReturnType) {
        return findMethod(FindClass, MethodName, ReturnType, new Class<?>[0]);
    }

    public static Method findNoParamsMethod(Class<?> FindClass, String MethodName, Class<?> ReturnType) {
        return findMethod(FindClass, MethodName, ReturnType, new Class<?>[0]);
    }

    public static Method findUnknownReturnMethod(String ClassName, String methodName,
                                                 Class<?>[] paramsType) {
        return findUnknownReturnMethod(ClassUtils.getClass(ClassName), methodName, paramsType);
    }

    public static Method findUnknownReturnMethod(Class<?> target, String methodName, Class<?>[] paramsType) {
        if (target == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append(target.getName()).append(".").append(methodName).append("(");
        for (Class<?> type : paramsType) sb.append(type.getName()).append(",");
        sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        String key = sb.toString();
        if (methodList.containsKey(key)) {
            return methodList.get(key);
        }

        Class<?> Current_Find = target;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == paramsType.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!Objects.equals(params[i], paramsType[i])) continue Loop;
                        }
                        methodList.put(key, method);
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();//向父类查找
        }

        Current_Find = target;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == paramsType.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!CheckClassType.CheckClass(params[i], paramsType[i])) continue Loop;
                        }
                        methodList.put(key, method);
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();
        }
        throw new RuntimeException("没有查找到方法 : " + key);
    }

    public static Method findMethod(String FindClass, String MethodName, Class<?> ReturnType, Class<?>[] ParamTypes) {
        Class<?> clz = ClassUtils.getClass(FindClass);
        return findMethod(clz, MethodName, ReturnType, ParamTypes);
    }

    public static Method findMethod(Class<?> FindClass, String MethodName, Class<?> ReturnType, Class<?>[] ParamTypes) {
        if (FindClass == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append(FindClass.getName()).append(".").append(MethodName).append("(");
        for (Class<?> type : ParamTypes) sb.append(type.getName()).append(",");
        sb.delete(sb.length() - 1, sb.length());
        sb.append(")").append(ReturnType.getName());
        String signature = sb.toString();
        if (methodList.containsKey(signature)) {
            return methodList.get(signature);
        }

        Class<?> Current_Find = FindClass;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(MethodName) || MethodName == null) && method.getReturnType().equals(ReturnType)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == ParamTypes.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!Objects.equals(params[i], ParamTypes[i])) continue Loop;
                        }
                        method.setAccessible(true);
                        methodList.put(signature, method);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();//向父类查找
        }

        Current_Find = FindClass;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(MethodName) || MethodName == null) && method.getReturnType().equals(ReturnType)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == ParamTypes.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!CheckClassType.CheckClass(params[i], ParamTypes[i])) continue Loop;
                        }
                        method.setAccessible(true);
                        methodList.put(signature, method);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();
        }
        throw new RuntimeException("没有查找到方法 : " + signature);
    }

    public static <T> T callMethodByName(Object obj, String name, Object... params) {
        Method m = findMethodByName(obj.getClass(), name);
        if (m == null) return null;
        try {
            return (T) m.invoke(obj, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method findMethodByName(Class<?> clz, String Name) {
        for (Method method : clz.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().equals(Name)) return method;
        }
        return null;
    }


    public static String GetMethodInfoText(Method method) {
        if (method == null) return "方法可能为空没有获取到方法文本信息";
        /*MethodInfo info = GetMethodInfo(method);
        return info.DeclaringClassName+" -> "+info.Signature;*/
        return String.valueOf(method);
    }

    public static MethodInfo GetMethodInfo(Method method) {
        if (MethodInfo.MethodInfoCache.containsKey(method)) {
            return MethodInfo.MethodInfoCache.get(method);
        }
        StringBuilder sb = new StringBuilder();
        //方法签名
        sb.append(Modifier.toString(method.getModifiers()));
        sb.append(" ").append(method.getReturnType().getName());
        if (method.getReturnType().isArray()) sb.append("[]");
        sb.append(" ").append(method.getName());
        sb.append("(");
        //参数类型
        for (Class<?> paramsType : method.getParameterTypes()) {
            sb.append(paramsType.getName()).append(" , ");
        }
        if (sb.toString().endsWith(" , ")) {
            sb.delete(sb.length() - 3, sb.length());
        }
        sb.append(");");
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.DeclaringClassName = method.getDeclaringClass().getName();
        methodInfo.Signature = sb.toString();
        MethodInfo.MethodInfoCache.put(method, methodInfo);
        return methodInfo;
    }

    public static class MethodInfo {
        public static final HashMap<Method, MethodInfo> MethodInfoCache = new HashMap<>();
        public String DeclaringClassName;//com.android.view...
        public String Signature;//public int getInt(Object params);
    }
}
