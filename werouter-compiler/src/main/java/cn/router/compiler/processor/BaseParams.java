package cn.router.compiler.processor;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/5
 */

public class BaseParams {

    public static String PACKAGE_NAME ="cn.router.process";
    public static String GROUP_CLASS_NAME ="Router$Paths";
    public static String PROVIDER_CLASS_NAME ="Router$Provider$root";
    public static final String WARNING_TIPS = "DO NOT EDIT THIS FILE!!! IT WAS GENERATED BY WEROUTER.\n";
    public static final String ACTIVITY = "android.app.Activity";
    public static final String FRAGMENT = "android.app.Fragment";
    public static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";


    public static final String PROVIDER_PACKAGE = "cn.router.api.provider";
    public static final String PROVIDER = "cn.router.api.provider.WeProvider";
    public static final String ROUTER_PROVIDER = "cn.router.api.method.WeRouterProvider";
    public static final String PATH_REPLACE_PROVIDER = PROVIDER_PACKAGE+".PathReplaceProvider";
    public static final String JSON_PROVIDER = PROVIDER_PACKAGE+".JsonProvider";


    public static final String BASEROUTER = "cn.router.api.method";
    public static final String WEROUTER_GROUP_TYPE = BASEROUTER +".WeRouterPath";

    public static final String METHOD_INIT = "init";
    public static final String GROUP_PARAM_NAME = "groups";
    public static final String PROVIDER_PARAM_NAME = "providers";

    // Options of processor
    public static final String KEY_MODULE_NAME = "WEROUTER_MODULE_NAME";





}
