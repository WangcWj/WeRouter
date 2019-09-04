package cn.router.api.router;


import cn.router.api.base.DataStorage;
import cn.router.api.debug.DebugConstant;
import cn.router.api.exception.InitException;
import cn.router.api.exception.NoProviderFoundException;
import cn.router.api.exception.NoRouterFoundException;
import cn.router.api.method.WeRouterPath;
import cn.router.api.method.WeRouterProvider;
import cn.router.api.utils.ClassUtils;
import cn.router.werouter.annotation.bean.RouterBean;
import cn.router.werouter.annotation.enums.RouteType;

/**
 * Created to : 先这样吧  未完成 先去搞组件化  搞完再用它
 *
 * @author WANG
 * @date 2018/11/23
 */

public class LoadingCenter {

    private static String mTag = "WeRouter :";


    /**
     * 通过插件开始初始化工作
     */
    public static void init() {
        DataStorage.clear();
        //like this...
        // initByPlugin("cn.router.process.Router$Provider$root$app");
        // initByPlugin("cn.router.process.Router$Root$root$app");
        // initByPlugin("cn.router.process.Router$Provider$root$ceshimodule");
        // initByPlugin("cn.router.process.Router$Root$root$ceshimodule");
    }

    /**
     * <p>
     * 通过指定的路径去直接查找Router$Root$root类,这样做就需要去设置混淆规则了.
     * ARouter里面采用的是加载dex文件的方式去加载类.
     * </p>
     */
    public static void initByPlugin(String className) {
        try {
            Object instance = ClassUtils.findInstanceByClassPath(className);
            if (instance instanceof WeRouterPath) {
                WeRouterPath weRouterRoot = (WeRouterPath) instance;
                weRouterRoot.init(DataStorage.mGroups);
            } else if (instance instanceof WeRouterProvider) {
                WeRouterProvider provider = (WeRouterProvider) instance;
                provider.init(DataStorage.mProviders);
            }
        } catch (Exception e) {
            throw new InitException(mTag + ": 插件初始化时出现了问题 :LoadingCenter.init()" + e.getMessage());
        }
    }


    public static void completion(Transform transform) {
        //加载要使用到的分组的数据
        RouterBean routerBean = DataStorage.mGroups.get(transform.getPath());
        if (null == routerBean) {
            throw new NoRouterFoundException(mTag + "路径("+transform.getPath()+")丢失了~~~~~ ");
        } else {
            transform.setRouterBean(routerBean);
            if (transform.getRouteType() == RouteType.PROVIDE) {
                try {
                    Class<?> target = transform.getTarget();
                    Object instance = target.getConstructor().newInstance();
                    if (instance instanceof WeRouterProvider) {
                        transform.setRouterProvider((WeRouterProvider) instance);
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    public static Transform buildProvider(String className) {
        RouterBean routerBean = DataStorage.mProviders.get(className);
        if (null != routerBean) {
            Transform transform = new Transform(routerBean.getPath());
            transform.setRouterBean(routerBean);
            return transform;
        } else {
            throw new NoProviderFoundException(DebugConstant.TAG+ ": Can not find the specified class according to the class name ->"+className);
        }
    }

}
