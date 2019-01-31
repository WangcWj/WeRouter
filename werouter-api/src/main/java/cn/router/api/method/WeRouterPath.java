package cn.router.api.method;

import java.util.Map;

import cn.router.werouter.annotation.bean.RouterBean;

/**
 * Created to :管理单个路径
 *
 * @author WANG
 * @date 2018/11/21
 */

public interface WeRouterPath {

    /**
     * 添加单个路由路径到集合中
     * @param routers
     */
    void init(Map<String,RouterBean> routers);
}
