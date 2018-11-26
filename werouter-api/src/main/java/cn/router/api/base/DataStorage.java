package cn.router.api.base;

import java.util.HashMap;
import java.util.Map;

import cn.router.api.interfaces.WeRouterGroup;
import cn.router.werouter.annotation.bean.RouterBean;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/23
 */

public class DataStorage {

    public static Map<String, RouterBean> mGroups = new HashMap<>();
    public static Map<String, Class<? extends WeRouterGroup>> mRoots = new HashMap<>();

    public static void clear() {
        mGroups.clear();
        mRoots.clear();
    }


}
