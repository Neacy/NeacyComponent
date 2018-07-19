package com.component.asm;

import com.component.annotation.NeacyComponent;
import com.component.annotation.NeacyParam;
import com.component.annotation.NeacyProtocol;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 扫描路由以及模块化注解
 *
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/20
 */
public class ComponentVisitor extends ClassVisitor {

    public ComponentVisitor(int api) {
        super(api);
    }

    public ComponentVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public String componentName;
    public ComponentAnnotationVisitor annotationVisitor;
    public ComponentAnnotationVisitor routerAnnotationVisitor;
    public boolean isContainIntentData;// 是否需要解析getIntent数据

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        // com/neacy/component/MainActivity
        NeacyLog.log("===== name = " + name);
        componentName = name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {

        FieldVisitor fv = super.visitField(access, name, desc, signature, value);

        return new FieldVisitor(Opcodes.ASM5, fv) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                isContainIntentData = Type.getDescriptor(NeacyParam.class).equals(desc);
                return super.visitAnnotation(desc, visible);
            }
        };
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        NeacyLog.log("=== visitAnnotation = " + desc); // Lcom/component/annotation/NeacyComponent;

        AnnotationVisitor _annotationVisitor = super.visitAnnotation(desc, visible);

        if (Type.getDescriptor(NeacyComponent.class).equals(desc)) {// 扫描模块注解
            NeacyLog.log("===== 找到了Component组件 ====");
            annotationVisitor = new ComponentAnnotationVisitor(Opcodes.ASM5, _annotationVisitor);
            return annotationVisitor;
        }

        if (Type.getDescriptor(NeacyProtocol.class).equals(desc)) {// 扫描路由注解
            routerAnnotationVisitor = new ComponentAnnotationVisitor(Opcodes.ASM5, _annotationVisitor);
            return routerAnnotationVisitor;
        }
        return _annotationVisitor;
    }
}
