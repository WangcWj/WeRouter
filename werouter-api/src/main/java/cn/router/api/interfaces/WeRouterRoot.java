package cn.router.api.interfaces;

import java.util.Map;

/**
 * Created to :管理分组
 *
 * @author WANG
 * @date 2018/11/21
 */

public interface WeRouterRoot {

    /**
     * 添加单个分组到集合中
     * @param routers
     */
    void init(Map<String, Class<? extends WeRouterGroup>> routers);
}
