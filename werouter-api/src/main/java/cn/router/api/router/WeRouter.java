package cn.router.api.router;

import android.app.Application;
import android.content.Context;

import cn.router.api.exception.InitException;

/**
 * Created to :
 * <p>
 *    1.首先再静态的方法init中区初始化一些日志文件和处理注解生成的一些类.
 *    2.再创建对象的时候先确保初始化过程已经完成.
 *    要找个类把Class对象,path ,type ,参数都先保存起来.
 *
 *
 * @author WANG
 * @date 2018/11/5
 */

public class WeRouter {

    private volatile static WeRouter instance = null;
    private volatile static boolean hasInit = false;
    private static Context context;

    public synchronized static void init(Application application) {
        context = application.getApplicationContext();
        hasInit = WeRouterManager.init(application);

    }

    private WeRouter() {

    }

    /**
     * Destroy arouter, it can be used only in debug mode.
     */
    public synchronized void destroy() {

    }

    public static WeRouter getInstance() {
        //当数据庞大的时候无疑会增加初始化的时间,这样呢为了更安全.
        if(!hasInit){
            throw new InitException("WeRouter::Init::Invoke init(context) first!");
        }else {
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

    public Transform build(String path){
       return WeRouterManager.getInstance().build(path);
    }


    public void navigation(String path) {

    }

}