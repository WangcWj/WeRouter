package cn.router.api.router;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.Set;

import cn.router.api.method.WeRouterProvider;
import cn.router.werouter.annotation.bean.RouterBean;

/**
 * Created to :保存跳转所需的全部数据,包括传值什么的.
 *
 * @author WANG
 * @date 2018/11/23
 */

public class Transform extends RouterBean {
    private Bundle mBundle;
    private WeRouterProvider routerProvider;


    public Bundle getData() {
        return mBundle;
    }

    public void setData(Bundle data) {
        this.mBundle = data;
    }

    public WeRouterProvider getRouterProvider() {
        return routerProvider;
    }

    public void setRouterProvider(WeRouterProvider routerProvider) {
        this.routerProvider = routerProvider;
    }

    public Transform(String path) {
        setPath(path);
        mBundle = mBundle == null ? new Bundle() : mBundle;
    }

    public static Transform build(String path){
        return new Transform(path);
    }

    public static Transform build(String path, Map<String,String> params){
        Transform transform = new Transform(path);
        if(params.size() > 0){
            Set<String> keySet = params.keySet();
            for (String key:keySet) {
                String value = params.get(key);
                transform.withString(key,value);
            }
        }
        return transform;
    }

    public Transform withString( String key, String value){
        mBundle.putString(key,value);
        return this;
    }

    public Object navigation(){
       return navigation(null,-1);
    }

    public Object navigation(Context context){
        return navigation(null,-1);
    }

    public Object navigation(Context context,int requestCode){
     return WeRouterManager.getInstance().navigation(context,this,requestCode);
    }

    public void setRouterBean(RouterBean routerBean){
        setClassName(routerBean.getClassName());
        setPackagePath(routerBean.getPackagePath());
        setRouteType(routerBean.getRouteType());
        setTarget(routerBean.getTarget());
    }


}
