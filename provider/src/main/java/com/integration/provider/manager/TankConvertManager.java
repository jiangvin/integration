package com.integration.provider.manager;

import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/6/12
 */

@Slf4j
public class TankConvertManager {

    @Getter
    static class JsFile {
        String path;
        String name;
        List<String> moreThan = new ArrayList<>();
        int weight = 0;
    }

    private static final String JS_FILE = ".js";

    private static final String HOME = "D:\\files\\java\\socket\\websocket\\src\\main\\resources";
    private static final String JS_HOME = HOME + "\\static\\";

    private static final String JQUERY = "jquery.js";

    private static final String VERSION = "_1.js";

    public static void main(String[] args) {
        String[] jsFolders = {"js\\web", "js\\share", "js\\app"};

        List<JsFile> jsFiles = new ArrayList<>();
        for (String folder : jsFolders) {
            File file = new File(JS_HOME + folder);
            convertJsFolder(file, jsFiles);
        }

        String html = HOME + "\\templates\\index.html";
        jsFiles.sort(Comparator.comparing(JsFile::getWeight));
        convertHtml(html, jsFiles);
    }

    private static void convertJsFolder(File file, List<JsFile> jsFiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File subFile : files) {
                convertJsFolder(subFile, jsFiles);
            }
            return;
        }

        if (!file.getPath().endsWith(JS_FILE)) {
            return;
        }

        JsFile jsFile = new JsFile();
        jsFile.path = file.getPath().replace(JS_HOME, "");
        jsFile.name = jsFile.path.substring(jsFile.path.lastIndexOf("\\") + 1);
        jsFiles.add(jsFile);

        if (file.getName().equals(JQUERY)) {
            return;
        }

        convertJsFile(file, jsFile, jsFiles);

        //reset weight
        checkWeight(jsFile, jsFiles);
    }

    private static void checkWeight(JsFile target, List<JsFile> jsFiles) {
        for (JsFile jsFile : jsFiles) {
            if (jsFile.moreThan.contains(target.name) && jsFile.weight <= target.weight) {
                jsFile.weight = target.weight + 1;
                checkWeight(jsFile, jsFiles);
            }
        }
    }

    private static void convertHtml(String path, List<JsFile> jsFiles) {
        try {
            StringBuilder content = new StringBuilder();
            @Cleanup Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8);
            for (Object object : stream.toArray()) {
                String line = (String) object;
                if (!line.startsWith("<script type=\"module\"")) {
                    content.append(line).append("\r\n");
                    continue;
                }

                for (JsFile jsFile : jsFiles) {
                    String scriptPath;
                    if (jsFile.path.contains(JQUERY)) {
                        scriptPath = jsFile.path;
                    } else {
                        scriptPath = jsFile.path.replace(".js", VERSION);
                    }
                    content.append(String.format("<script src=\"%s\"></script>", scriptPath)).append("\r\n");
                }
            }

            @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            fileOutputStream.write(content.toString().getBytes());
        } catch (Exception e) {
            log.error("catch error:", e);
        }
    }

    private static Integer getWeight(List<JsFile> jsFiles, String name) {
        for (JsFile jsFile : jsFiles) {
            if (jsFile.name.equals(name)) {
                return jsFile.weight;
            }
        }
        return null;
    }

    private static void convertJsFile(File file, JsFile jsFile, List<JsFile> jsFiles) {
        int weight = 0;
        try {
            StringBuilder content = new StringBuilder();
            @Cleanup Stream<String> stream = Files.lines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
            for (Object object : stream.toArray()) {
                String line = (String) object;
                if (line.startsWith("import")) {
                    String fileName = line.replace("'", "").replace("\"", "").replace(";", "");
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    Integer w = getWeight(jsFiles, fileName);
                    if (w != null) {
                        weight = w + 1;
                    }
                    jsFile.moreThan.add(fileName);
                    continue;
                }
                content.append(line.replace("export default class ", "class ")).append("\r\n");
            }

            @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(new File(file.getPath().replace(".js", VERSION)));
            fileOutputStream.write(content.toString().getBytes());
        } catch (Exception e) {
            log.error("catch error:", e);
        }
        jsFile.weight = weight;
    }
}
