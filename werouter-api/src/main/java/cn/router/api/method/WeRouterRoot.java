package cn.router.api.method;

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
     */
    Map<String,Class<? extends WeRouterPath>> init();
}
