package com.h2tg.templates;

import com.h2tg.Config;
import com.h2tg.utils.JarUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static com.h2tg.Config.*;

public abstract class PatchTemplate implements Runnable
{
    List<String> targetClassPaths = new ArrayList<>();
    ClassPool pool = ClassPool.getDefault();
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

//    public CtClass getLibPatchClass(String libName, String className) throws NotFoundException {
//        targetLibClassPaths.put(libName,className);
//        return pool.get(className);
//    }
//
//    public void addPatchLib(String libName) {
//        Path libSourcepath = sourcePath.resolve(LIB_PREFIX).resolve(libName.replace(".jar",""));
//        targetLibs.put(libName,libSourcepath);
//    }

    public void addClassPaths() throws NotFoundException
    {
//        for (String libname : targetLibs.keySet()) {
//            pool.insertClassPath(targetLibs.get(libname).toString());
//        }
        pool.insertClassPath(sourcePath.resolve(CLASSPATH_PREFIX).toString());
        pool.insertClassPath(sourcePath.toString());
    }

    public void genSource()
    {
        JarUtil.unpackJar(jarFilePath, sourcePath);

//        for (String libname : targetLibs.keySet()) {
//            JarUtil.unpackJar(sourcePath.resolve(LIB_PREFIX).resolve(libname), targetLibs.get(libname));
//        }
    }

    public void apply(Path outputJarPath) throws NotFoundException, CannotCompileException, IOException
    {
        for (String targetClassPath : targetClassPaths) {
            pool.get(targetClassPath).writeFile(patchPath.resolve(CLASSPATH_PREFIX).toString());
        }
//        for (String libname : targetLibs.keySet()) {
//            for (String targetClassPath : targetLibClassPaths.keySet()) {
//                pool.get(targetClassPath).writeFile(patchPath.resolve(LIB_PREFIX).resolve(libname).toString());
//            }
//        }
        JarUtil.copyFile(jarFilePath,outputJarPath);
        JarUtil.updateJar(outputJarPath, patchPath, Paths.get("."));
    }

    public void clear()
    {
        JarUtil.deleteAll(sourcePath.toFile());
        JarUtil.deleteAll(patchPath.toFile());
    }

    @Override
    public void run()
    {
        Path outputJarPath = outputJarDir.resolve(jarFilePath.getFileName().toString().replace(".jar", "")+PATCHED_SUFFIX);
        genSource();

        try{
            addClassPaths();
            patch();
            apply(outputJarPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        clear();
    }
}
