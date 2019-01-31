package cn.router.wang.main;

import android.app.Application;

import cn.router.api.router.WeRouter;


/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/23
 */

public class APP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WeRouter.init(this);
    }
}
