package com.h2tg.templates;

import javassist.*;

import java.net.URLDecoder;

public class Example4java1 extends PatchTemplate
{
    public Example4java1(String jarFilePath)
    {
        super(jarFilePath);
//        addPatchLib("tika-parsers-1.3.jar");
    }

    @Override
    public void patch() throws NotFoundException, CannotCompileException
    {
        xxePatch();
        jdbcPatch();
    }



    public void jdbcPatch() throws NotFoundException, CannotCompileException
    {
        CtClass targetClass = getPatchClass("config.MysqlConfiguration");
        CtMethod wafMethod = CtNewMethod.make("public boolean waf(String s,String illegalParameter)\n" +
                "{\n" +
                "    return (!s.toLowerCase().contains(illegalParameter.toLowerCase()) && !java.net.URLDecoder.decode(s).toLowerCase().contains(illegalParameter.toLowerCase()));\n" +
                "}", targetClass);
        targetClass.addMethod(wafMethod);
        CtMethod getJdbcMethod = targetClass.getDeclaredMethod("getJdbc");
        getJdbcMethod.setBody("{\n" +
                "        if ($0.extraParams.trim().isEmpty()) {\n" +
                "            return \"jdbc:mysql://HOSTNAME:PORT/DATABASE\".replace(\"HOSTNAME\", $1.trim()).replace(\"PORT\", $2.trim()).replace(\"DATABASE\", $3.trim());\n" +
                "        } else {\n" +
                "            java.util.Iterator var5 = $0.getIllegalParameters().iterator();\n" +
                "\n" +
                "            String illegalParameter;\n" +
                "            do {\n" +
                "                if (!var5.hasNext()) {\n" +
                "                    return \"jdbc:mysql://HOSTNAME:PORT/DATABASE?EXTRA_PARAMS\".replace(\"HOSTNAME\", $1.trim()).replace(\"PORT\", $2.toString().trim()).replace(\"DATABASE\", $3.trim()).replace(\"EXTRA_PARAMS\", $4.trim());\n" +
                "                }\n" +
                "\n" +
                "                illegalParameter = (String)var5.next();\n" +
                "            } while( $0.waf($1,illegalParameter) && $0.waf($2,illegalParameter) && $0.waf($3,illegalParameter) && $0.waf($4,illegalParameter) );\n" +
                "\n" +
                "            throw new RuntimeException(\"Illegal parameter: \" + illegalParameter);\n" +
                "        }\n" +
                "    }");
    }

    public void xxePatch() throws NotFoundException, CannotCompileException
    {

    }
}
