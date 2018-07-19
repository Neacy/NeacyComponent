package com.component.asm;

import com.component.annotation.NeacyInitMethod;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * 直接获取getIntent中数据
 *
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/7/18
 */
public class ComponentInitVisitor extends ClassVisitor {

    public ComponentInitVisitor(int api) {
        super(api);
    }

    public ComponentInitVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    private String className;// eg: com.neacy
    private String classOriginName;// eg:  com/neacy

    private HashMap<String, LinkedList<String>> datas = new HashMap<>();

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        // com/neacy/component/MainActivity
        NeacyLog.log("===== name = " + name);
        classOriginName = name;
        className = name.replace("/", ".");
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        NeacyLog.log("==== FinderClassVisitor visitField name ==== " + name);
        NeacyLog.log("==== FinderClassVisitor visitField desc ==== " + desc);
        FieldVisitor fieldVisitor = super.visitField(access, name, desc, signature, value);
        FinderFieldVisitor mFieldVisitor = new FinderFieldVisitor(Opcodes.ASM5, fieldVisitor, name, desc, datas);
        return mFieldVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (datas == null || datas.isEmpty()) {
            NeacyLog.log("并没有需要处理的Intent数据");
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        NeacyLog.log("==== visitMethod-> name = " + name);
        NeacyLog.log("==== visitMethod-> desc = " + desc);
        NeacyLog.log("==== visitMethod-> signature = " + signature);
        MethodVisitor mv2 = cv.visitMethod(access, name, desc, signature, exceptions);
        final MethodVisitor methodVisitor = new NeacyAdviceAdapter(Opcodes.ASM5, mv2, access, name, desc);
        return methodVisitor;
    }

    /**
     * 直接在NeacyInitMethod注解的方法中插入代码
     */
    class NeacyAdviceAdapter extends AdviceAdapter {

        /**
         * 是否有注释可以插入代码
         */
        public boolean isInject;
        private String methodName;
        private MethodVisitor mv;

        protected NeacyAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
            this.mv = mv;
            methodName = name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (Type.getDescriptor(NeacyInitMethod.class).equals(desc)) {
                isInject = true;
            }
            return super.visitAnnotation(desc, visible);
        }

        @Override
        protected void onMethodEnter() {
            if (isInject) {
                NeacyLog.log("=== 开始插入代码 === " + methodName);// initView
                NeacyLog.log("=== 开始插入代码 classOriginName === " + classOriginName);// com/rn_demo/TestActivity
                NeacyLog.log("=== 开始插入代码 插入的数量 === " + datas.size());// 1

                Set<String> sets = datas.keySet();
                for (String key : sets) {
                    LinkedList<String> list = datas.get(key);
                    String fieldName = key;
                    String fieldPath = list.get(0);
                    String fieldID = list.get(1);
                    NeacyLog.log("=== fieldName = " + fieldName);// mButton
                    NeacyLog.log("=== fieldPath = " + fieldPath);// Landroid/widget/Button;
                    NeacyLog.log("=== fieldID = " + fieldID);// 2131558499
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);

                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classOriginName, "getIntent", "()Landroid/content/Intent;", false);
                    mv.visitLdcInsn(fieldID);
                    if (fieldPath.startsWith("Ljava/lang/String")) {
                        NeacyLog.log("================== 插入字符串");
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/content/Intent", "getStringExtra", "(Ljava/lang/String;)Ljava/lang/String;", false);
                    } else if (fieldPath.startsWith("I")) {
                        NeacyLog.log("================== 插入int");
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/content/Intent", "getIntExtra", "(Ljava/lang/String;I)I", false);
                    } else if (fieldPath.startsWith("Z")) {
                        NeacyLog.log("================== 插入boolean");
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/content/Intent", "getBooleanExtra", "(Ljava/lang/String;Z)Z", false);
                    } else if (fieldPath.startsWith("J")) {
                        NeacyLog.log("================== 插入long");
                        mv.visitInsn(LCONST_0);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/content/Intent", "getLongExtra", "(Ljava/lang/String;J)J", false);
                    } else if (fieldPath.startsWith("F")) {
                        NeacyLog.log("================== 插入float");
                        mv.visitInsn(FCONST_0);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/content/Intent", "getFloatExtra", "(Ljava/lang/String;F)F", false);
                    } else if (fieldPath.startsWith("D")) {
                        NeacyLog.log("================== 插入double");
                        mv.visitInsn(DCONST_0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, "android/content/Intent", "getDoubleExtra", "(Ljava/lang/String;D)D", false);
                    } else if (fieldPath.startsWith("L")) {// 目前只支持Parcelable序列化
                        NeacyLog.log("================== 插入Parcelable ");
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/content/Intent", "getParcelableExtra", "(Ljava/lang/String;)Landroid/os/Parcelable;", false);
                        mv.visitTypeInsn(Opcodes.CHECKCAST, fieldPath.substring(1, fieldPath.length() - 1));
                    }

                    mv.visitFieldInsn(Opcodes.PUTFIELD, classOriginName, fieldName, fieldPath);
                }
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
        }
    }
}
