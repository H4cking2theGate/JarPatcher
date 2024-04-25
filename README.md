# JarPatcher
A patch tool for jar based on javassist.

可以用于awd/awdp比赛时简单的patch

## Usage

继承com.h2tg.templates.PatchTemplate来编写patch模板，可以参考com.h2tg.templates.Example4java2

```java
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
    ...
}

```

运行main即可

```java
    public static void main(String[] args)
    {
        PatchTemplate patch = new Example4java2("example/java2.jar");
        patch.run();
    }
```

## 示例

添加类方法

```java
		CtClass targetClass = getPatchClass("service.BookService");
        CtMethod newMethod = CtNewMethod.make("public void newMethod() { System.out.println(\"This is a new method.\"); }", targetClass);
        targetClass.addMethod(newMethod);
```

修改类方法

```java
        CtClass targetClass = getPatchClass("service.BookService");
        CtMethod userinfoMethod = targetClass.getDeclaredMethod("userinfo");
        userinfoMethod.setBody("{ return \"Modified userinfo\"; }");
```

删除类方法

```
        CtMethod deleteMethod = targetClass.getDeclaredMethod("delete");
        targetClass.removeMethod(deleteMethod);
```

修改注解

```java
        CtClass targetClass = getPatchClass("mapper.UserMapper");
        CtMethod ctMethod = targetClass.getDeclaredMethod("findByUsername");
        
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
```

## TODO

- [ ] 支持class文件的patch
- [ ] 更友好的lib patch
