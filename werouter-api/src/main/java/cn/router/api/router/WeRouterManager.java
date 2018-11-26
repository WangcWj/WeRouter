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

import cn.router.api.exception.HandlerException;
import cn.router.api.exception.InitException;
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
    private String mTag = "WeRouter :";


    public static boolean init(Application application) {
        mContext = application;
        mHandle = new Handler(Looper.getMainLooper());
        LoadingCenter.init();
        hasInit = true;
        return true;
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
        String group;
        if (TextUtils.isEmpty(path)) {
            throw new HandlerException(mTag + "请检查跳转路径是否正确!");
        } else {
            group = handleGroup(path);
        }
        return Transform.build(path, group);
    }


    public Object navigation(Context context, final Transform transform , final int requestCode) {
        try {
            //首次使用到某个分组的时候再去初始化某个分组的信息
            LoadingCenter.completion(transform);
        } catch (Exception e) {

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
                if(null != data){
                    intent.putExtras(data);
                }

                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    startActivity(currentContext,intent,requestCode,transform);
                } else {
                    mHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(currentContext,intent,requestCode,transform);
                        }
                    });
                }

                break;
            case SERVICE:
                break;
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

    private void startActivity(Context currentContext, Intent intent, int requestCode,Transform transform) {
        // Need start for result
        if (requestCode >= 0 && currentContext instanceof Activity) {
            Activity activity = (Activity) currentContext;
            activity.startActivityForResult(intent,requestCode);

        } else {
            currentContext.startActivity(intent);
        }

    }


    /**
     * 从 Path中获取Group  把直接设置Group的功能去掉
     *
     * @return
     */
    private String handleGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new HandlerException(mTag + "the path must be start with '/' and contain more than 2 '/'!");
        }
        String lowerCasePath = path.toLowerCase();
        //本来想搞成native://
        try {
            String group = lowerCasePath.substring(1, lowerCasePath.indexOf("/", 1));
            if (TextUtils.isEmpty(group)) {
                throw new HandlerException(mTag + "the path must be start with '/' and contain more than 2 '/'!");
            } else {
                return group;
            }
        } catch (Exception e) {

        }
        return null;
    }


}
