package cn.router.api.router;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import cn.router.api.base.DataStorage;
import cn.router.api.exception.HandlerException;
import cn.router.api.exception.InitException;
import cn.router.api.interfaces.WeRouterGroup;
import cn.router.api.interfaces.WeRouterRoot;
import cn.router.api.utils.ClassUtils;
import cn.router.werouter.annotation.bean.RouterBean;
import cn.router.werouter.annotation.enums.RouteType;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/23
 */

public class LoadingCenter {

    private static String mRootClassPath = "cn.router.process.Router$Root$root";
    private static String mTag = "WeRouter :";

    /**
     * <p>
     * 通过指定的路径去直接查找Router$Root$root类,这样做就需要去设置混淆规则了.
     * ARouter里面采用的是加载dex文件的方式去加载类.
     * </p>
     */
    public static void init() {
        try {
            //这里面还差个 缓存的问题
            Object instance = ClassUtils.findInstanceByClassPath(mRootClassPath);
            if (instance instanceof WeRouterRoot) {
                WeRouterRoot weRouterRoot = (WeRouterRoot) instance;
                weRouterRoot.init(DataStorage.mRoots);
            }
        } catch (Exception e) {
            throw new InitException("Router : LoadingCenter.init方法在加载指定类的时候出现的问题 :  " + e.getMessage());
        }

    }

    public static void completion(Transform transform) {
        //加载要使用到的分组的数据
        RouterBean routerBean = DataStorage.mGroups.get(transform.getPath());
        if (null == routerBean) {
            Class<? extends WeRouterGroup> aClass = DataStorage.mRoots.get(transform.getGroup());
            if (null == aClass) {
                throw new HandlerException(mTag + "There is no route match the path [" + transform.getPath() + "], in group [" + transform.getGroup() + "]");
            } else {
                try {
                    Constructor<?> constructor = ClassUtils.findConstructorByClass(aClass);
                    WeRouterGroup instance = (WeRouterGroup) constructor.newInstance();
                    instance.init(DataStorage.mGroups);
                    DataStorage.mRoots.remove(transform.getGroup());
                } catch (Exception e) {
                    throw new HandlerException(mTag + "找不到该分组的数据. " + e.getMessage());
                }
                //reload
                completion(transform);
            }
        } else {
            transform.setRouterBean(routerBean);
        }

    }


}
