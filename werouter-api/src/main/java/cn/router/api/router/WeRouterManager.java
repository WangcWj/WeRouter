package cn.router.api.router;


import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import cn.router.api.base.DataStorage;
import cn.router.api.debug.DebugConstant;
import cn.router.api.debug.WeError;
import cn.router.api.exception.HandlerException;
import cn.router.api.exception.InitException;
import cn.router.api.exception.NoRouterFoundException;
import cn.router.api.provider.PathReplaceProvider;
import cn.router.werouter.annotation.enums.RouteType;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/5
 */

public class WeRouterManager {

    private static WeRouterManager instance;
    private static boolean hasInit = false;
    private static Context mContext;
    private static Handler mHandle;
    private final String NATIVE = "native://";
    private final String URL = "http://";
    private final String URLS = "https://";
    private final String DIVISION = "&";

    public static boolean init(Application application) {
        mContext = application;
        mHandle = new Handler(Looper.getMainLooper());
        LoadingCenter.init();
        hasInit = true;
        WeError.error("  LoadingCenter : Successful initialization!");
        return true;
    }

    public void destroy(){
        DataStorage.clear();
        hasInit = false;
        instance = null;
        mHandle = null;
        mContext = null;
    }

    public static WeRouterManager getInstance() {
        //当数据庞大的时候无疑会增加初始化的时间,这样呢为了更安全.
        if (!hasInit) {
            throw new InitException("WeRouter::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (WeRouter.class) {
                    if (instance == null) {
                        instance = new WeRouterManager();
                    }
                }
            }
            return instance;
        }
    }

    public Transform build(String path) {
        WeError.error(": 当前跳转的路径是->"+path);
        PathReplaceProvider replaceProvider = navigation(PathReplaceProvider.class);
        if (null != replaceProvider) {
            path = replaceProvider.format(path);
            WeError.error(": 通过PathReplaceProvider替换掉之后的路径是->"+path);
        }
        if (TextUtils.isEmpty(path)) {
            throw new HandlerException(DebugConstant.TAG + ": 请检查跳转路径是否正确!");
        }
        Map<String,String> params = new HashMap<>();
        path = handlePath(path,params);
        return Transform.build(path,params);
    }

    /**
     * 从 Path中获取Group  把直接设置Group的功能去掉
     * native://MainActivity&name=wang&tag=Chao 带参数的
     * native://MainActivity   不带参数的
     * @return
     */
    private String handlePath(String path,Map params) {
        if (TextUtils.isEmpty(path) ) {
            throw new HandlerException(DebugConstant.TAG + ": the path must be not null !");
        }
        if(path.startsWith(NATIVE) || path.startsWith(URL) || path.startsWith(URLS)){
            String replacePath = path.replace(NATIVE,"").replace(URL,"").replace(URLS,"");
            WeError.error(": 准备处理的跳转路径->"+replacePath);
            if(replacePath.contains(DIVISION)){
                handleParams(replacePath,params);
                int index = path.indexOf(DIVISION);
                path = path.substring(0,index);
                WeError.error(": 最后的路径为 ->"+path);
            }
            return path;
        }else {
            throw new HandlerException(DebugConstant.TAG + ": the path must be start with :  ( native:// or https:// or http://) !");
        }
    }

    /**
     * 截取路径里面携带的参数
     * @param replacePath 去掉 "native:// https:// http://"之后的路径.
     * @param params
     */
    private void handleParams(String replacePath, Map params) {
        String[] split = replacePath.split(DIVISION);
        for (int i = 0; i <split.length; i++) {
            String item = split[i];
            WeError.error(": 发现参数->"+item);
            if(item.contains("=")){
                String[] values = item.split("=");
                String key = values[0];
                String value = values[1];
                params.put(key,value);
                WeError.error(": 参数的 key->"+key+"   value -> "+value);
            }
        }
    }

    /**
     *  ARouter的设计:
     *               首先 ARouter分了一个 provider功能出来, 也就是如果一个类实现了IProvider接口这边就能完成一些重要的服务功能.
     *               ARouter提供的有 PathReplaceService :可以处理跳转的path.   SerializationService:用来序列化对象.   DegradeService : 当跳转不成功的时候能有感知.
     *               ClassLoaderService 等等.这些都是IProvider接口的子类,使用就是实现这些接口,用@Router注释.这样再ARouter的工作中就会根据场景去获取我们实现的服务类.
     *
     *               ARouter管理Provider也是采用了两种方式:
     *               第一:  采用分组管理 , ARouter$$Root$$app 里面管理所有的分组.ARouter$$Group$$xxxx 里面关系该分组的所有成员.
     *                     这种方式是可以用指定的path去获取该provider的实例.跟IRouteGroup实现跳转的原理一样的.
     *
     *               第二: 直接用Class对象去获取Provider的实例.这个就需要ARouter$$Providers$$app类,该类存储了以实现了IProvider接口的实现类的CLass对象为key存储,
     *               这样就可以使用Class对象去查找我们需要的Provider对象了.
     *
     *               设计的目的并不单单的是给用户特定的几个实现类,而是把更多的自定义权限给了用户.ARouter要求的那几个实现类是对跳转过程中起到一定作用的.
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T navigation(Class<? extends T> service) {
        try {
            Transform transform = LoadingCenter.buildProvider(service.getName());
            if (null == transform) {
                transform = LoadingCenter.buildProvider(service.getSimpleName());
            }

            if (transform == null) {
                return null;
            }

            Constructor<?> constructor = transform.getTarget().getConstructor();
            Object instance = constructor.newInstance();
            return (T) instance;
        } catch (Exception e) {
            return null;
        }
    }

    public Object navigation(Context context, final Transform transform, final int requestCode) {
        try {
            LoadingCenter.completion(transform);
        } catch (Exception e) {
           throw new NoRouterFoundException("分组数据异常!");
        }
        final Context currentContext = null == context ? mContext : context;

        RouteType routeType = transform.getRouteType();

        switch (routeType) {
            case ACTIVITY:
                final Intent intent = new Intent(currentContext, transform.getTarget());
                // Non activity, need less one flag.
                if (!(currentContext instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                Bundle data = transform.getData();
                if (null != data) {
                    intent.putExtras(data);
                }

                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    startActivity(currentContext, intent, requestCode, transform);
                } else {
                    mHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(currentContext, intent, requestCode, transform);
                        }
                    });
                }

                break;
            case SERVICE:
                break;
            case PROVIDE:
            case FRAGMENT:
                Class fragmentMeta = transform.getTarget();
                try {
                    Object instance = fragmentMeta.getConstructor().newInstance();
                    if (instance instanceof Fragment) {
                        //参数
                        ((Fragment) instance).setArguments(transform.getData());

                    } else if (instance instanceof android.support.v4.app.Fragment) {
                        //参数
                        ((android.support.v4.app.Fragment) instance).setArguments(transform.getData());
                    }
                    return instance;
                } catch (Exception ex) {
                }
            default:
                return null;
        }
        return null;

    }

    private void startActivity(Context currentContext, Intent intent, int requestCode, Transform transform) {
        // Need start for result
        if (requestCode >= 0 && currentContext instanceof Activity) {
            Activity activity = (Activity) currentContext;
            activity.startActivityForResult(intent, requestCode);

        } else {
            currentContext.startActivity(intent);
        }

    }

}
