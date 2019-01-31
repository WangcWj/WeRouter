package cn.router.api.method;

import java.util.Map;

import cn.router.werouter.annotation.bean.RouterBean;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/28
 */

public interface WeRouterProvider {

    /**
     * 将提供的一些服务暴露出去
     * @param provides
     */
    void init(Map<String,RouterBean> provides);

}
