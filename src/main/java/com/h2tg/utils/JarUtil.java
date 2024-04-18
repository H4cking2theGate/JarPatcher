package com.h2tg.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JarUtil {
    public static void unpackJar(Path jarFilePath, Path destPath) {

        if (!destPath.toFile().exists()) {
            destPath.toFile().mkdir();
        }

        ProcessBuilder decompress = new ProcessBuilder();
        decompress.directory(destPath.toFile());
        decompress.command("jar", "xf", jarFilePath.toAbsolutePath().toString());
        try {
            Process p = decompress.start();
            p.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void packJar(Path source, Path target) {
        try {
            ProcessBuilder pb = new ProcessBuilder("jar", "cf", target.toAbsolutePath().toString(), "-C", source.toAbsolutePath().toString(), ".");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateJar(Path jarFilePath, Path patchPath, Path classPath) {
        if (!patchPath.toFile().exists()) {
            patchPath.toFile().mkdir();
        }
        List<String> commandList = new ArrayList<>();
        commandList.add("jar");
        commandList.add("uf0");
        commandList.add(jarFilePath.toString());

//        for (String classPath: classPaths) {
//            commandList.add("-C");
//            commandList.add(patchPath.toString());
//            commandList.add(classPath);
//        }
        commandList.add("-C");
        commandList.add(patchPath.toString());
        commandList.add(classPath.toString());

        ProcessBuilder updateJar = new ProcessBuilder(commandList);
        updateJar.redirectErrorStream(true);
        System.out.println(commandList);
        updateJar.directory(jarFilePath.toAbsolutePath().getParent().toFile());
        try {
            Process p = updateJar.start();
            p.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFile(Path source, Path target) throws IOException {
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void deleteAll(File file) {
        if (file.isFile() || Objects.requireNonNull(file.list()).length == 0) {
            file.delete();
        } else {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                deleteAll(f);

            }
            file.delete();
        }
    }


}