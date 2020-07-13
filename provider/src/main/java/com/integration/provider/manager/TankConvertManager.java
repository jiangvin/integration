package com.integration.provider.manager;

import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        String content;
        List<String> moreThan = new ArrayList<>();
        int weight = 0;
    }

    private static final String JS_FILE = ".js";

    private static final String HOME = "D:\\files\\java\\socket\\websocket\\src\\main\\resources";
    private static final String JS_HOME = HOME + "\\static\\";

    private static final String IGNORE_PATH = "\\libs\\";

    private static final String VERSION = "_40.js";

    public static void main(String[] args) throws Exception {
        String[] jsFolders = {"js\\web", "js\\share", "js\\app"};

        List<JsFile> jsFiles = new ArrayList<>();
        for (String folder : jsFolders) {
            File file = new File(JS_HOME + folder);
            convertJsFolder(file, jsFiles);
        }

        String html = HOME + "\\templates\\index.html";
        jsFiles.sort(Comparator.comparing(JsFile::getWeight));
        createMainJs(jsFiles);
        convertHtml(html);
    }

    private static void createMainJs(List<JsFile> jsFiles) throws Exception {
        //生成
        String path = String.format("%smain%s", JS_HOME, VERSION);
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
        for (JsFile jsFile : jsFiles) {
            fileOutputStream.write(jsFile.content.getBytes());
        }

        //压缩
        String command = String.format("C:\\Users\\jiangvin\\AppData\\Roaming\\npm\\uglifyjs.cmd %s -o %s -c -m", path, path);
        log.info("exec command:{}", command);
        log.info("result:{}", execCmd(command));
    }

    /**
     * 执行系统命令, 返回执行结果
     *
     * @param cmd 需要执行的命令
     */
    private static String execCmd(String cmd) throws Exception {
        StringBuilder result = new StringBuilder();


        // 执行命令, 返回一个子进程对象（命令在子进程中执行）
        Process process = Runtime.getRuntime().exec(cmd);

        // 方法阻塞, 等待命令执行完成（成功会返回0）
        process.waitFor();

        // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
        @Cleanup BufferedReader bufferIn = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        @Cleanup BufferedReader bufferError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

        // 读取输出
        String line;
        while ((line = bufferIn.readLine()) != null) {
            result.append(line).append('\n');
        }
        while ((line = bufferError.readLine()) != null) {
            result.append(line).append('\n');
        }

        process.destroy();
        // 返回执行结果
        return result.toString();
    }

    private static void convertJsFolder(File file, List<JsFile> jsFiles) throws IOException {
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

        if (file.getPath().contains(IGNORE_PATH)) {
            return;
        }

        JsFile jsFile = new JsFile();
        jsFile.path = file.getPath().replace(JS_HOME, "");
        jsFile.name = jsFile.path.substring(jsFile.path.lastIndexOf("\\") + 1);
        jsFiles.add(jsFile);

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

    private static void convertHtml(String path) {
        try {
            StringBuilder content = new StringBuilder();
            @Cleanup Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8);
            for (Object object : stream.toArray()) {
                String line = (String) object;
                if (!line.startsWith("<script type=\"module\"")) {
                    content.append(line).append("\r\n");
                    continue;
                }

                content.append(String.format("<script src=\"main%s\"></script>", VERSION)).append("\r\n");
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

    private static void convertJsFile(File file, JsFile jsFile, List<JsFile> jsFiles) throws IOException {
        int weight = 0;
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
        jsFile.content = content.toString();
        jsFile.weight = weight;
    }
}
