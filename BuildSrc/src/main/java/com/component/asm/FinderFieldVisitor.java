package com.component.asm;

import com.component.annotation.NeacyParam;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 成员变量扫描
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/3/4
 */
public class FinderFieldVisitor extends FieldVisitor {

    public FinderFieldVisitor(int api) {
        super(api);
    }

    public FinderFieldVisitor(int api, FieldVisitor fv) {
        super(api, fv);
        NeacyLog.log("==== FinderFieldVisitor start ====");
    }

    private String fieldName;
    private String fieldDesc;
    public String viewId;
    private HashMap<String, LinkedList<String>> datas;

    public FinderFieldVisitor(int api, FieldVisitor fv, String fieldName, String fieldDesc, HashMap<String, LinkedList<String>> datas) {
        this(api, fv);
        setFieldData(fieldName, fieldDesc, datas);
    }

    public void setFieldData(String fieldName, String fieldDesc, HashMap<String, LinkedList<String>> datas) {
        this.datas = datas;
        this.fieldDesc = fieldDesc;
        this.fieldName = fieldName;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        NeacyLog.log("================= visitAnnotation == desc = " + desc);

        if (Type.getDescriptor(NeacyParam.class).equals(desc)) {
            return new AnnotationVisitor(Opcodes.ASM5) {
                @Override
                public void visit(String name, Object value) {
                    NeacyLog.log("=== AnnotationVisitor === name = " + name);
                    NeacyLog.log("=== AnnotationVisitor === value = " + value);
                    viewId = value.toString();
                    LinkedList<String> list = new LinkedList<>();
                    list.add(fieldDesc);
                    list.add(viewId);
                    datas.put(fieldName, list);
                }
            };
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        NeacyLog.log("==== FinderFieldVisitor visitEnd ====");
    }
}
