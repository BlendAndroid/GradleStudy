package com.blend.gradle

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor

// MethodVisitor是ASM（Java字节码操作库）中的一个重要类，用于访问和修改Java类的方法信息。
class TimeMethodVisitor extends MethodVisitor {

    private String className

    private String methodName

    private boolean isInject    // 是否为注解方法

    TimeMethodVisitor(MethodVisitor methodVisitor, String className, String methodName) {
        super(Opcodes.ASM5, methodVisitor)
        this.className = className
        this.methodName = methodName
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.contains("InjectTimestamp")) {
            isInject = true
        }
        return super.visitAnnotation(desc, visible)
    }

    // 访问方法的代码开始部分。
    @Override
    void visitCode() {

        if (isInject) {
            mv.visitLdcInsn(className + " -> TAG");
            mv.visitLdcInsn("开始时间:" + System.currentTimeMillis());
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);
        }
        super.visitCode()
    }

    @Override
    void visitInsn(int opcode) {

        if (isInject && opcode == Opcodes.RETURN) {
            mv.visitLdcInsn(className + " -> TAG");
            mv.visitLdcInsn("结束时间:" + System.currentTimeMillis());
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);
        }
        super.visitInsn(opcode)
    }
}
