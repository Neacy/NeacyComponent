package com.component.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.util.List;


/**
 * 开始生成
 *
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/20
 */
public class ComponentInsertVisitor extends ClassVisitor {

    public ComponentInsertVisitor(int i) {
        super(i);
    }

    public ComponentInsertVisitor(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    private File componentFile;
    private List<ComponentVisitor> components;

    public void setData(File componentFile, List<ComponentVisitor> components) {
        this.componentFile = componentFile;
        this.components = components;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        NeacyLog.log("==== 调用的函数名称 = " + name);
        if (name.equals("<clinit>")) {// registerComponent <init>
            NeacyLog.log("==== 这里是静态方法 = " + name);
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
            NeacyLog.log("==== isStatic = " + isStatic);
        }

        boolean isStatic;

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                NeacyLog.log("==== components Size = " + components.size());
                for (ComponentVisitor cv : components) {
                    NeacyLog.log("==== type = " + cv.componentName);
                    //用无参构造方法创建一个组件实例
                    mv.visitTypeInsn(Opcodes.NEW, cv.componentName);
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, cv.componentName, "<init>", "()V", false);
                    NeacyLog.log("==== isStatic is true   start = ");
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/component/ComponentController", "registerComponent", "(Lcom/component/IComponent;)V", false);
                    NeacyLog.log("==== type = " + 3);
                }
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 2, maxLocals);
            NeacyLog.log("==== type = " + 4);
        }
    }
}
