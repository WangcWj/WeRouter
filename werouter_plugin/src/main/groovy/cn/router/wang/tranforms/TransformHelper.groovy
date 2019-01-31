package cn.router.wang.tranforms

import java.util.concurrent.ConcurrentHashMap

class TransformHelper {

    static String targetFilePath = "cn.router.api.router.LoadingCenter"
    static String targetFilePath_replace = targetFilePath.replace(".","/")
    static String findMethodName = "init"
    static String generaMethodName = "initByPlugin"
    static String method_provider = "cn.router.api.method.WeRouterProvider"
    static String method_root = "cn.router.api.method.WeRouterPath"

    static String findClassPackage = "cn.router.process"
    static File targetFile
    static Set<String> initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>())
}
