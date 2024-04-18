package com.h2tg.templates;

import com.h2tg.Config;
import com.h2tg.utils.JarUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class PatchTemplate implements Runnable
{
    List<String> targetClassPaths = new ArrayList<>();
    ClassPool pool = ClassPool.getDefault();;
    Path jarFilePath;
    Path sourcePath = Paths.get(Config.SOURCE_PATH);
    Path patchPath = Paths.get(Config.PATCH_PATH);
    Path outputJarDir = Paths.get(Config.OUTPUT_JAR_DIR);

    public abstract void patch() throws NotFoundException, CannotCompileException;

    public PatchTemplate(final String jarFilePath) {
        this.jarFilePath = Paths.get(jarFilePath);
    }

    public PatchTemplate(final String jarFilePath, final String sourcePath, final String patchPath) {
        this(jarFilePath);
        this.sourcePath = Paths.get(sourcePath);
        this.patchPath = Paths.get(patchPath);
    }

    public CtClass getPatchClass(String className) throws NotFoundException {
        targetClassPaths.add(className);
        return pool.get(className);
    }


    @Override
    public void run()
    {
        Path outputJarPath = outputJarDir.resolve(jarFilePath.getFileName().toString().replace(".jar", "")+"_patched.jar");
        JarUtil.unpackJar(jarFilePath, sourcePath);


        try{
            JarUtil.copyFile(jarFilePath,outputJarPath);
            pool.insertClassPath(sourcePath.resolve("BOOT-INF/classes").toString());
            pool.insertClassPath(sourcePath.toString());
            patch();
            for (String targetClassPath : targetClassPaths) {
                pool.get(targetClassPath).writeFile(patchPath.resolve("BOOT-INF/classes").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JarUtil.updateJar(outputJarPath, patchPath, Paths.get("."));
        JarUtil.deleteAll(sourcePath.toFile());
        JarUtil.deleteAll(patchPath.toFile());
    }
}
