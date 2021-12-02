package com.blend.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class BlendPlugin implements Plugin<Project> {

    // 实现apply方法，注入插件的逻辑
    void apply(Project project) {

        //注册Transform
        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            Transform transform = new BlendTransform()
            appExtension.registerTransform(transform)
        }

        println "<--->I am form BlendPlugin,apply form ${project.name}"

        project.getExtensions().create("blend", BlendExtension)

        project.afterEvaluate {
            BlendExtension extension = project["blend"]
            println "<--->User's root path is:${extension.rootPath}"
        }
    }
}