package com.blend.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class BlendTransform extends Transform {

    /**
     * 当前Transform名称
     * @return
     */
    @java.lang.Override
    java.lang.String getName() {
        return "BlendTransform"
    }

    /**
     * 返回告知编译器，当前Transform需要消费的输入类型
     * 在这里是CLASS类型
     * @return
     */
    @java.lang.Override
    java.util.Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 告知编译器，当前Transform需要收集的范围
     * 官方文档 Scope 有 7 种类型：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    @java.lang.Override
    java.util.Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否支持增量
     * 通常返回False
     * @return
     */
    @java.lang.Override
    boolean isIncremental() {
        return false
    }

    /**
     * 所有的class收集好以后，会被打包传入此方法
     * transform方法的参数TransformInvocation是一个接口，提供一些关于输入的基本信息，利用这些接口就可以获得编译流程中的class文件进行操作
     * @param transformInvocation
     * @throws TransformException* @throws InterruptedException* @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        printCopyRight()

        // 从TransformInvocation获取输入，然后按照class文件夹和jar集合进行遍历，拿到所有的class文件，进行处理
        transformInvocation.inputs.each { TransformInput input ->

            // 把文件夹类型的输入，拷贝到目标目录
            input.directoryInputs.each { DirectoryInput directoryInput ->
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        transformFile(file)
                    }
                } else {
                    transformFile(file)
                }

                // Transform 拷贝文件到 transforms 目录
                File dest = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                // 将修改过的字节码copy到dest，实现编译期间干预字节码
                FileUtils.copyDirectory(directoryInput.getFile(), dest)
            }

            // 把JAR类型的输入，拷贝到目标目录
            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def dest = transformInvocation.outputProvider.getContentLocation(jarName,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                // 将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyFile(jarInput.getFile(), dest)
            }
        }

    }

    // 处理响应的文件
    private void transformFile(File file) throws IOException {
        def name = file.name
        if (filerClass(name)) {
            ClassReader reader = new ClassReader(file.bytes)
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
            ClassVisitor visitor = new TimePluginClassVisitor(writer)
            reader.accept(visitor, ClassReader.EXPAND_FRAMES)

            byte[] code = writer.toByteArray()
            def classPath = file.parentFile.absolutePath + File.separator + name
            FileOutputStream fos = new FileOutputStream(classPath)
            fos.write(code)
            fos.close()
        }
    }

    private static boolean filerClass(String name) {
        return name.endsWith("Activity.class")
    }

    static void printCopyRight() {
        println()
        println("******************************************************************************")
        println("******                                                                  ******")
        println("******            Welcome BlendTransform Compile Plugin                 ******")
        println("******                                                                  ******")
        println("******************************************************************************")
        println()
    }

}