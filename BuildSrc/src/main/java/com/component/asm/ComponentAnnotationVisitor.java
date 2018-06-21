package com.component.asm;

import org.objectweb.asm.AnnotationVisitor;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/20
 */
public class ComponentAnnotationVisitor extends AnnotationVisitor {

    public ComponentAnnotationVisitor(int api) {
        super(api);
    }

    public ComponentAnnotationVisitor(int api, AnnotationVisitor av) {
        super(api, av);
    }

    public String annotationName;

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);

        NeacyLog.log("==== annotationVisitor.visit.name ==== " + name);// value
        NeacyLog.log("==== annotationVisitor.visit.value ==== " + value);// app

        annotationName = value.toString();
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
