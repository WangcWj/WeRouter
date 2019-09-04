package cn.router.werouter.annotation.bean;

import javax.lang.model.element.Element;

import cn.router.werouter.annotation.Router;
import cn.router.werouter.annotation.enums.RouteType;

/**
 * Created to :
 *
 * @author WANG
 * @since  2018/11/5
 */

public class RouterBean {
    private Element element;
    /**
     * 跳转目标的Type类型
     */
    private RouteType routeType;
    /**
     * 跳转目标的CLASS对象
     */
    private Class<?> target;
    /**
     * 跳转的Path
     */
    private String path;

    /**
     * 跳转目标的包名
     */
    private String packagePath;
    /**
     * 跳转目标的类名
     */
    private String className;

    public RouterBean() {

    }

    public RouterBean(Router weRouter, RouteType routeType, Element element, String packagePath , String className) {
        this.routeType = routeType;
        this.path = weRouter.path();
        this.packagePath = packagePath;
        this.className = className;
        this.element = element;
    }

    public RouterBean(RouteType routeType, Class<?> target, String path) {
        this.routeType = routeType;
        this.target = target;
        this.path = path;
    }

    public static RouterBean build(RouteType routeType, Class<?> target, String path) {
        return new RouterBean(routeType,target,path);
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "path='" + path + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
