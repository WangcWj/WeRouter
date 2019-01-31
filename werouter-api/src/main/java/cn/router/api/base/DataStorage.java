package cn.router.api.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.router.api.method.WeRouterPath;
import cn.router.werouter.annotation.bean.RouterBean;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/23
 */

public class DataStorage {

    public static Map<String, RouterBean> mGroups = new HashMap<>();

    public static Map<String, RouterBean> mProviders = new HashMap<>();

    /**
     * 可能一个group 对应多个class
     */
    public static Map<String, List<Class<? extends WeRouterPath>>> mRoots = new HashMap<>();

    public static void clear() {
        mGroups.clear();
        mRoots.clear();
        mProviders.clear();
    }


}
