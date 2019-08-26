package com.integration.provider.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 蒋文龙(Vin)
 * @className PackageScannerManager
 * @description
 * @date 2019/8/22
 */
@Slf4j
public class PackageScannerManager {

    private String basePackage;
    private ClassLoader classLoader;

    public PackageScannerManager(String basePackage) {
        this.basePackage = basePackage;
        this.classLoader = getClass().getClassLoader();

    }

    public List<String> getFullyQualifiedClassNameList() {
        log.info("start scanning classes from {}", basePackage);

        return doScan(basePackage, new ArrayList<>());
    }

    private String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    private String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    private List<String> doScan(String basePackage, List<String> nameList) {
        // replace dots with splashes
        String splashPath = dotToSplash(basePackage);

        // get file path
        URL url = classLoader.getResource(splashPath);
        if (url == null) {
            return new ArrayList<>();
        }

        String filePath = getRootPath(url);

        // Get classes in that package.
        // If the web server unzips the jar file, then the classes will exist in the form of
        // normal file in the directory.
        // If the web server does not unzip the jar file, then classes will exist in jar file.
        List<String> names;
        if (isJarFile(filePath)) {
            // jar file
            log.debug("{} is a jar file", filePath);

            names = readFromJarFile(filePath, splashPath);
        } else {
            // directory
            log.debug("{}is a directory", filePath);

            names = readFromDirectory(filePath);
        }

        for (String name : names) {
            if (isClassFile(name)) {
                nameList.add(toFullyQualifiedName(name, basePackage));
            } else {
                // this is a directory
                // check this directory for more classes
                // do recursive invocation
                doScan(basePackage + "." + name, nameList);
            }
        }

        for (String n : nameList) {
            log.debug("find {}", n);
        }

        return nameList;
    }

    private String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

    /**
     * Convert short class name to fully qualified name.
     * e.g., String -> java.lang.String
     */
    private String toFullyQualifiedName(String shortName, String basePackage) {
        return basePackage + '.' + trimExtension(shortName);
    }

    private List<String> readFromJarFile(String jarPath, String splashedPackageName) {
        log.debug("get classes from JAR: {}", jarPath);
        List<String> nameList = new ArrayList<>();

        try {
            JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
            JarEntry entry = jarIn.getNextJarEntry();

            while (null != entry) {
                String name = entry.getName();
                if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                    nameList.add(name);
                }

                entry = jarIn.getNextJarEntry();
            }
        } catch (IOException e) {
            log.error("readFromJarFile failed:", e);
        }

        return nameList;
    }

    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return new ArrayList<>();
        }

        return Arrays.asList(names);
    }

    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }
}
