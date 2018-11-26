package cn.router.api.router;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import cn.router.werouter.annotation.bean.RouterBean;

/**
 * Created to :保存跳转所需的全部数据,包括传值什么的.
 *
 * @author WANG
 * @date 2018/11/23
 */

public class Transform extends RouterBean {
    private Bundle mBundle;


    public Bundle getData() {
        return mBundle;
    }

    public void setData(Bundle data) {
        this.mBundle = data;
    }

    public Transform(String path, String grorp) {
        setPath(path);
        setGroup(grorp);
        mBundle = mBundle == null ? new Bundle() : mBundle;
    }

    public static Transform build(String path, String group){
        return new Transform(path,group);
    }

    public Transform withString(@NonNull String key, @NonNull String value){
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