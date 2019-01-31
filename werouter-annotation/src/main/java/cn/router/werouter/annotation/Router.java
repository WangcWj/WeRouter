package cn.router.werouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created to :Mark a page can be route by cn.router.
 *
 * @author WANG
 * @since  2018/11/2
 */

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface Router {

    /**
     * 路由路径的唯一标示 路由框架将根据这个路径去查找要启动的页面
     * @return
     */
    String path();
}
