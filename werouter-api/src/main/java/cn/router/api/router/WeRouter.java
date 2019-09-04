package cn.router.api.router;

import android.app.Application;
import android.content.Context;

import java.util.Map;

import cn.router.api.base.DataStorage;
import cn.router.api.debug.WeError;
import cn.router.api.exception.InitException;
import cn.router.werouter.annotation.bean.RouterBean;

/**
 * Created to :
 * <p>
 * 1.首先再静态的方法init中区初始化一些日志文件和处理注解生成的一些类.
 * 2.再创建对象的时候先确保初始化过程已经完成.
 * 要找个类把Class对象,path ,type ,参数都先保存起来.
 *
 * @author WANG
 * @date 2018/11/5
 */

public class WeRouter {

    private volatile static WeRouter instance = null;
    private volatile static boolean hasInit = false;

    private WeRouter() {

    }

    /**
     * ARouter设计必须每个依赖的module都要设置那个moduleName ,
     * 我猜是为了区别每个module里面生成类的类名称.
     *
     * @param application
     */
    public synchronized static void init(Application application) {
        hasInit = WeRouterManager.init(application);
        WeError.error(" WeRouter : Successful initialization!");
    }

    /**
     * Destroy arouter, it can be used only in debug mode.
     */
    public synchronized void destroy() {

        instance = null;
    }


    public static WeRouter getInstance() {
        //当数据庞大的时候无疑会增加初始化的时间,这样呢为了更安全.
        if (!hasInit) {
            throw new InitException("WeRouter::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (WeRouter.class) {
                    if (instance == null) {
                        instance = new WeRouter();
                    }
                }
            }
            return instance;
        }
    }

    public Transform build(String path) {
        return WeRouterManager.getInstance().build(path);
    }

    public static Map<String, RouterBean> getRouterMap(){
        return DataStorage.mGroups;
    }

    public static void celar() {
        DataStorage.clear();
    }

}
