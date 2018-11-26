package cn.router.api.utils;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/5
 */

public class ClassUtils {


    @Nullable
    @CheckResult
    @UiThread
    public static Class<?> findClassByPath(@NonNull String path) throws ClassNotFoundException {
        Class<?> aClass = Class.forName(path);
        return aClass;
    }

    public static Object findInstanceByClassPath(@NonNull String path) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> classByPath = findClassByPath(path);
        Constructor<?> constructor = findConstructorByClass(classByPath);
        return constructor.newInstance();
    }

    @Nullable
    @CheckResult
    @UiThread
    public static Constructor<?> findConstructorByClass(@NonNull Class<?> aClass) throws NoSuchMethodException, NullPointerException {
        return aClass.getConstructor();
    }
}
