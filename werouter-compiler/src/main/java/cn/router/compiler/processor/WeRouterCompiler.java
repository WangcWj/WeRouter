package cn.router.compiler.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import cn.router.werouter.annotation.Router;
import cn.router.werouter.annotation.bean.RouterBean;
import cn.router.werouter.annotation.enums.RouteType;

import static cn.router.compiler.processor.BaseParams.KEY_MODULE_NAME;


/**
 * @author WANG
 * //这种方式涨见识了
 * ParameterizedTypeName rootTypeName = ParameterizedTypeName.get(
 * ClassName.get(Map.class),
 * ClassName.get(String.class),
 * ParameterizedTypeName.get(
 * ClassName.get(Class.class),
 * WildcardTypeName.subtypeOf(ClassName.get(mElementUtils.getTypeElement(BaseParams.WEROUTER_GROUP_TYPE)))
 * )
 * );
 */
@SupportedOptions(KEY_MODULE_NAME)
@AutoService(Processor.class)
public class WeRouterCompiler extends AbstractProcessor {

    private Map<String, RouterBean> mGroupDatas = new HashMap<>();
    private Messager mMessage;
    private Filer mFiler;
    private Types mTypeUtils;
    private Elements mElementUtils;
    private String moduleName;
    private Writer openWriter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessage = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mTypeUtils = processingEnvironment.getTypeUtils();
        mElementUtils = processingEnvironment.getElementUtils();
        Map<String, String> options = processingEnvironment.getOptions();
        moduleName = options.get(KEY_MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            //为了保证每个module生成的类都不一样
            moduleName = "$" + moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            try {
                openWriter = mFiler.createResource(StandardLocation.SOURCE_OUTPUT,
                        BaseParams.PACKAGE_NAME + ".doc",
                        "WeRouter.json")
                        .openWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("WeRouter::  Which module is used? Please set this code in your build.gradle. >>>  " +
                    "javaCompileOptions {\n" +
                    "            annotationProcessorOptions {\n" +
                    "                arguments = [WEROUTER_MODULE_NAME: project.getName()]\n" +
                    "            }\n" +
                    "        }"
            );
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.size() > 0) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Router.class);
            try {
                parseElements(elements);
            } catch (Exception e) {
                mMessage.printMessage(Diagnostic.Kind.NOTE, "================Exception: " + e);
            }
        }
        return true;
    }

    private void parseElements(Set<? extends Element> elements) throws Exception {
        if (elements.size() > 0) {

            TypeMirror typeActivity = mElementUtils.getTypeElement(BaseParams.ACTIVITY).asType();
            TypeMirror typeFragment = mElementUtils.getTypeElement(BaseParams.FRAGMENT).asType();
            TypeMirror typeFragmentV4 = mElementUtils.getTypeElement(BaseParams.FRAGMENT_V4).asType();
            TypeMirror provider = mElementUtils.getTypeElement(BaseParams.PROVIDER).asType();

            for (Element element : elements) {
                ElementKind kind = element.getKind();
                if (ElementKind.CLASS == kind) {
                    TypeElement typeElement = (TypeElement) element;
                    TypeMirror tm = typeElement.asType();
                    Router weRouter = typeElement.getAnnotation(Router.class);
                    if (null == weRouter) {
                        continue;
                    }
                    RouterBean routerBean;
                    String classPath = typeElement.getQualifiedName().toString();
                    String className = typeElement.getSimpleName().toString();
                    if (mTypeUtils.isSubtype(tm, typeActivity)) {
                        //Activity
                        routerBean = new RouterBean(weRouter, RouteType.ACTIVITY, element, classPath, className);
                    } else if (mTypeUtils.isSubtype(tm, typeFragment) || mTypeUtils.isSubtype(tm, typeFragmentV4)) {
                        //Fragment
                        routerBean = new RouterBean(weRouter, RouteType.FRAGMENT, element, classPath, className);
                    } else if (mTypeUtils.isSubtype(tm, provider)) {
                        //provider
                        routerBean = new RouterBean(weRouter, RouteType.PROVIDE, element, classPath, className);
                    } else {
                        throw new RuntimeException("WeRouter::Compiler >>> Found unsupported class type, type = [" + tm.toString() + "].");
                    }
                    //这里要把Group存放在Map中.key是分组名 value 是set<String,RouterBean>
                    if (handleGroup(routerBean)) {
                        String path = routerBean.getPath();
                        mGroupDatas.put(path, routerBean);
                    }
                }
            }
            Map<String, String> docData = new HashMap<>();
            ParameterizedTypeName groupTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(RouterBean.class));
            ParameterSpec groupParam = ParameterSpec.builder(groupTypeName, BaseParams.GROUP_PARAM_NAME).build();
            ParameterizedTypeName providerParams = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class)
            );
            ParameterSpec providerParamSpec = ParameterSpec.builder(providerParams, BaseParams.PROVIDER_PARAM_NAME).build();
            MethodSpec.Builder providerMethod = MethodSpec.methodBuilder(BaseParams.METHOD_INIT)
                    .addParameter(providerParamSpec)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
            MethodSpec.Builder groupMethodBuilder = MethodSpec.methodBuilder(BaseParams.METHOD_INIT)
                    .addParameter(groupParam)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);
            Set<Map.Entry<String, RouterBean>> entries = mGroupDatas.entrySet();
            boolean haveProvider = false;
            for (Map.Entry<String, RouterBean> entry : entries) {
                RouterBean routerBean = entry.getValue();
                //之前不知道ARouter为什么要保存当前的Element,原来再这里用的,
                //如果你不用也行  那就的自己用字符串去拼接了(MainActivity.class) 并且 你还得自己引入该Class包路径
                // $T 可以帮你import.
                RouteType routeType = routerBean.getRouteType();
                docData.put(routerBean.getPath(), routerBean.getClassName());
                if (RouteType.PROVIDE == routeType) {
                    haveProvider = true;
                    TypeElement element = (TypeElement) routerBean.getElement();
                    ClassName className = ClassName.get(element);
                    List<? extends TypeMirror> interfaces = element.getInterfaces();
                    for (TypeMirror typeMirror : interfaces) {
                        if (mTypeUtils.isSubtype(typeMirror, provider)) {
                            providerMethod.addStatement(BaseParams.PROVIDER_PARAM_NAME +
                                            ".put($S,$T.build($T." + routerBean.getRouteType() +
                                            ",$T.class,$S))",
                                    typeMirror.toString(),
                                    RouterBean.class,
                                    RouteType.class,
                                    className,
                                    routerBean.getPath()
                            );
                        }
                    }
                }
                ClassName className = ClassName.get((TypeElement) routerBean.getElement());
                groupMethodBuilder.addStatement(BaseParams.GROUP_PARAM_NAME +
                                ".put($S,$T.build($T." + routerBean.getRouteType() +
                                ",$T.class,$S))",
                        routerBean.getPath(),
                        RouterBean.class,
                        RouteType.class,
                        className,
                        routerBean.getPath()
                );

            }
            //SerializerFeature.PrettyFormat 让json格式化.
            String jsonString = JSON.toJSONString(docData, SerializerFeature.PrettyFormat);
            openWriter.append(jsonString);
            openWriter.flush();

            String groupClassName = BaseParams.GROUP_CLASS_NAME;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName + moduleName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get(mElementUtils.getTypeElement(BaseParams.WEROUTER_GROUP_TYPE)))
                    .addMethod(groupMethodBuilder.build())
                    .addJavadoc(BaseParams.WARNING_TIPS)
                    .build();

            JavaFile javaFile = JavaFile.builder(BaseParams.PACKAGE_NAME, typeSpec).build();
            javaFile.writeTo(mFiler);

            if(haveProvider) {
                TypeSpec providerType = TypeSpec.classBuilder(BaseParams.PROVIDER_CLASS_NAME + moduleName)
                        .addModifiers(Modifier.PUBLIC)
                        .addJavadoc(BaseParams.WARNING_TIPS)
                        .addSuperinterface(ClassName.get(mElementUtils.getTypeElement(BaseParams.ROUTER_PROVIDER)))
                        .addMethod(providerMethod.build())
                        .build();
                JavaFile file = JavaFile.builder(BaseParams.PACKAGE_NAME, providerType).build();
                file.writeTo(mFiler);
            }
        }

    }

    /**
     * 从 Path中获取Group  把直接设置Group的功能去掉
     *
     * @param routerBean
     * @return
     */

    private boolean handleGroup(RouterBean routerBean) {
        String path = routerBean.getPath();
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        String lowerCasePath = StringUtils.lowerCase(path);
        //本来想搞成native://
        if (lowerCasePath.startsWith("native://")) {
            return true;
        }
        return false;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Router.class.getCanonicalName());
        return annotations;
    }
}
