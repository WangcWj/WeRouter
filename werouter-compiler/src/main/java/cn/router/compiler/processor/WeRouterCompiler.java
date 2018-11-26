package cn.router.compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import cn.router.werouter.annotation.Router;
import cn.router.werouter.annotation.bean.RouterBean;
import cn.router.werouter.annotation.enums.RouteType;


/**
 * @author WANG
 */
@AutoService(Processor.class)
public class WeRouterCompiler extends AbstractProcessor {

    private Map<String, Set<RouterBean>> mGroupDatas = new HashMap<>();
    private Messager mMessage;
    private Filer mFiler;
    private Types mTypeUtils;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessage = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mTypeUtils = processingEnvironment.getTypeUtils();
        mElementUtils = processingEnvironment.getElementUtils();
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
                    } else {
                        throw new RuntimeException("Router::Compiler >>> Found unsupported class type, type = [" + tm.toString() + "].");
                    }
                    //这里要把Group存放在Map中.key是分组名 value 是set<String,RouterBean>
                    if (handleGroup(routerBean)) {
                        Set<RouterBean> routerBeans = mGroupDatas.get(routerBean.getGroup());
                        String group = routerBean.getGroup();
                        if (null == routerBeans) {
                            Set<RouterBean> beanSet = new TreeSet<>((routerBean1, t1) -> {
                                try {
                                    return routerBean1.getPath().compareTo(t1.getPath());
                                } catch (NullPointerException npe) {
                                    return 0;
                                }
                            });
                            beanSet.add(routerBean);
                            mGroupDatas.put(group, beanSet);
                        } else {
                            routerBeans.add(routerBean);
                        }
                    }
                }
            }

            ParameterizedTypeName groupTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(RouterBean.class));
            ParameterSpec groupParam = ParameterSpec.builder(groupTypeName, BaseParams.GROUP_PARAM_NAME).build();
            //这种方式涨见识了
            ParameterizedTypeName rootTypeName = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(mElementUtils.getTypeElement(BaseParams.WEROUTER_GROUP_TYPE)))
                    )
            );
            ParameterSpec rootParam = ParameterSpec.builder(rootTypeName, BaseParams.ROOT_PARAM_NAME).build();

            MethodSpec.Builder rootMethod = MethodSpec.methodBuilder(BaseParams.METHOD_INIT)
                    .addParameter(rootParam)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);

            Set<Map.Entry<String, Set<RouterBean>>> entries = mGroupDatas.entrySet();
            for (Map.Entry<String, Set<RouterBean>> entry : entries) {
                String groupName = entry.getKey();
                //首先构建单个分组的类.类名WeRouter$Group$分组名
                Set<RouterBean> routerBeans = entry.getValue();
                MethodSpec.Builder groupMethodBuilder = MethodSpec.methodBuilder(BaseParams.METHOD_INIT)
                        .addAnnotation(Override.class)
                        .addParameter(groupParam)
                        .addModifiers(Modifier.PUBLIC);

                for (RouterBean routerData : routerBeans) {
                    //之前不知道ARouter为什么要保存当前的Element,原来再这里用的,
                    //如果你不用也行  那就的自己用字符串去拼接了(MainActivity.class) 并且 你还得自己引入该Class包路径
                    // $T 可以帮你import.
                    ClassName className = ClassName.get((TypeElement) routerData.getElement());
                    groupMethodBuilder.addStatement(BaseParams.GROUP_PARAM_NAME +
                                    ".put($S,$T.build($T." + routerData.getRouteType() +
                                    ",$T.class,$S,$S))",
                            routerData.getPath(),
                            RouterBean.class,
                            RouteType.class,
                            className,
                            routerData.getPath(),
                            routerData.getGroup()
                    );
                }

                String lowerCase = StringUtils.lowerCase(groupName);
                String groupClassName = BaseParams.GROUP_CLASS_NAME + lowerCase;
                TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ClassName.get(mElementUtils.getTypeElement(BaseParams.WEROUTER_GROUP_TYPE)))
                        .addMethod(groupMethodBuilder.build())
                        .addJavadoc(BaseParams.WARNING_TIPS)
                        .build();

                JavaFile javaFile = JavaFile.builder(BaseParams.PACKAGE_NAME, typeSpec).build();
                javaFile.writeTo(mFiler);

                rootMethod.addStatement(BaseParams.ROOT_PARAM_NAME + ".put($S,$T.class)", lowerCase, ClassName.get(BaseParams.PACKAGE_OF_GENERATE_FILE, groupClassName));
            }

            TypeSpec rootType = TypeSpec.classBuilder(BaseParams.ROOT_CLASS_NAME)
                    .addSuperinterface(ClassName.get(mElementUtils.getTypeElement(BaseParams.WEROUTER_ROOT_TYPE)))
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc(BaseParams.WARNING_TIPS)
                    .addMethod(rootMethod.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(BaseParams.PACKAGE_NAME, rootType).build();
            javaFile.writeTo(mFiler);

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
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }
        String lowerCasePath = StringUtils.lowerCase(path);
        //本来想搞成native://
        if (StringUtils.isEmpty(routerBean.getGroup())) {
            try {
                String group = lowerCasePath.substring(1, lowerCasePath.indexOf("/", 1));
                if (StringUtils.isEmpty(group)) {
                    return false;
                }
                routerBean.setGroup(group);
                return true;
            } catch (Exception e) {
                return false;
            }
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
