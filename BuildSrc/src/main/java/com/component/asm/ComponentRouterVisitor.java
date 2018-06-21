package com.component.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.util.List;


/**
 * 开始生成路由模块
 *
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/20
 */
public class ComponentRouterVisitor extends ClassVisitor {

    public ComponentRouterVisitor(int i) {
        super(i);
    }

    public ComponentRouterVisitor(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    private List<ComponentVisitor> components;

    public void setData(List<ComponentVisitor> components) {
        this.components = components;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        NeacyLog.log("==== Router 调用的函数名称 = " + name);
        if (name.equals("<clinit>")) {// addRouter <init>
            NeacyLog.log("==== Router 这里是静态方法 = " + name);
            boolean _static = (access & Opcodes.ACC_STATIC) > 0;
            mv = new CMethodVisitor(Opcodes.ASM5, mv, _static);
        }
        return mv;
    }

    class CMethodVisitor extends MethodVisitor {

        public CMethodVisitor(int api) {
            super(api);
        }

        public CMethodVisitor(int api, MethodVisitor mv, boolean isStatic) {
            super(api, mv);
            this.isStatic = isStatic;
            NeacyLog.log("==== Router isStatic = " + isStatic);
        }

        boolean isStatic;

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                NeacyLog.log("==== Router components Size = " + components.size());
                for (ComponentVisitor cv : components) {
                    NeacyLog.log("==== Router key = " + cv.routerAnnotationVisitor.annotationName);
                    NeacyLog.log("==== Router value = " + cv.componentName.replace("/", "."));
                    //用无参构造方法创建一个组件实例
                    mv.visitLdcInsn(cv.routerAnnotationVisitor.annotationName);
                    mv.visitLdcInsn(cv.componentName.replace("/", "."));
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/component/RouterController", "addRouter", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                }
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 2, maxLocals);
        }
    }
}
