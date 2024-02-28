package com.blend.gradle

// 定义Extension
class BlendExtension {
    String rootPath

    @Override
    String toString() {
        return "BlendExtension{rootPath='${rootPath}'}"
    }
}