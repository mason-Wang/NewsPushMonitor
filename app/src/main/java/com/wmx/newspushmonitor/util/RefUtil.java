package com.wmx.newspushmonitor.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangmingxing on 18-1-25.
 */

public class RefUtil {

    public static Object getDeclaredFieldValue(Object obj, String fieldName) {
        return getDeclaredFieldValue(obj, obj.getClass(), fieldName);
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj, obj.getClass(), fieldName);
    }

    public static Object callDeclaredMethod(Object obj, String name, Class[] parameterTypes, Object... args)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        if(parameterTypes == null) {
            parameterTypes = new Class[] {};
            args = new Object[] {};
        }

        Method method = null;
        if (method == null) {
            Class<?> clz = obj.getClass();
            do {
                try {
                    method = clz.getDeclaredMethod(name, parameterTypes);
                } catch (NoSuchMethodException e) {
                    // ignore
                }
                clz = clz.getSuperclass();
            } while (method == null && !clz.equals(Object.class));
        }

        if (method == null) {
            throw new NoSuchMethodException("method:" + name + "(" + parameterTypes + ") dont exists in " + obj.getClass()
                    + " and it's super classes");
        }

        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    public static Object getFieldValue(Object obj, Class cls, String fieldName) {
        try {
            Field field = cls.getField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getDeclaredFieldValue(Object obj, Class cls, String fieldName) {
        Field field;
        while (!cls.equals(Object.class)) {
            try {
                field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                // ignore
            }
            cls = cls.getSuperclass();
        }
        return null;
    }
}
