package com.h2tg.templates;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class Example4java2 extends PatchTemplate
{
    public Example4java2(String jarFilePath)
    {
        super(jarFilePath);
    }

    @Override
    public void patch() throws NotFoundException, CannotCompileException
    {
        patch1();
        patch2();
        System.out.println("Patched successfully.");
    }

    public void patch1() throws NotFoundException, CannotCompileException
    {
        CtClass targetClass = getPatchClass("service.BookService");
        CtMethod newMethod = CtNewMethod.make("public void newMethod() { System.out.println(\"This is a new method.\"); }", targetClass);
        targetClass.addMethod(newMethod);
        CtMethod userinfoMethod = targetClass.getDeclaredMethod("userinfo");
        userinfoMethod.setBody("{ return \"Modified userinfo\"; }");
    }

    public void patch2() throws NotFoundException, CannotCompileException
    {
        CtClass targetClass = getPatchClass("mapper.UserMapper");
        CtMethod ctMethod = targetClass.getDeclaredMethod("findByUsername");

        // 获取方法上的注解
        AnnotationsAttribute annotationAttribute = (AnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        Annotation annotation = annotationAttribute.getAnnotation("org.apache.ibatis.annotations.Select");
        ArrayMemberValue memberValue = (ArrayMemberValue) annotation.getMemberValue("value");
        StringMemberValue sqlMemberValue = (StringMemberValue) memberValue.getValue()[0];
        String sql = sqlMemberValue.getValue();
        String newSql = sql.replace("'${username}'", "#{username}");
        sqlMemberValue.setValue(newSql);
        memberValue.setValue(new MemberValue[]{sqlMemberValue});

        AnnotationsAttribute newAnnotationAttribute = new AnnotationsAttribute(ctMethod.getMethodInfo().getConstPool(), AnnotationsAttribute.visibleTag);
        Annotation newAnnotation = new Annotation("org.apache.ibatis.annotations.Select", ctMethod.getMethodInfo().getConstPool());
        newAnnotation.addMemberValue("value", memberValue);
        newAnnotationAttribute.addAnnotation(newAnnotation);
        ctMethod.getMethodInfo().addAttribute(newAnnotationAttribute);
    }

}
