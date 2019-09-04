package cn.router.wang.plugins

import cn.router.wang.tranforms.HandleTransform
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class WeRouterPlugins implements Plugin<Project> {

    @Override
    void apply(Project project) {
         project.getExtensions().getByType(AppExtension.class).registerTransform(new HandleTransform())
    }

}
