package com.blend.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class BlendPlugin implements Plugin<Project> {

    // 实现apply方法，注入插件的逻辑
    // ARouter中都是先使用APT,生成分组表和路由表
    // 区别就是:第一种方式是在运行时通过反射获取路由类信息
    // 而这里是第二种方式,可以通过ASM字节码插桩,在编译时期,将需要的信息插入到class文件中
    void apply(Project project) {

        // 注册Transform
        // 字节码插桩
        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            Transform transform = new BlendTransform()
            appExtension.registerTransform(transform)
        }

        println "<--->I am form BlendPlugin,apply form ${project.name}"

        // 注册扩展属性Extension
        project.getExtensions().create("blendParam", BlendExtension)

        project.afterEvaluate {
            // 获取传参
            BlendExtension extension = project["blendParam"]
            println "<--->buildSrc User's root path is:${extension}"
        }
    }
}