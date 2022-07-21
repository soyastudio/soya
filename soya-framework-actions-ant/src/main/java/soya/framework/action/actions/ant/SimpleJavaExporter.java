package soya.framework.action.actions.ant;

import org.apache.tools.ant.util.FileUtils;
import soya.framework.action.ActionResult;
import soya.framework.action.TaskResultExporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleJavaExporter implements TaskResultExporter {

    @Override
    public void export(Object result, File dir) throws IOException {
        System.out.println();

        StringReader sr = new StringReader(result.toString());
        BufferedReader br = new BufferedReader(sr);
        String line = br.readLine();
        String packageName = null;
        String className = null;
        while (line != null) {
            String ln = line.trim();
            if (ln.startsWith("package ")) {
                packageName = ln.substring("package ".length(), ln.indexOf(";")).trim();
            }

            if (ln.startsWith("class ") || ln.contains(" class ")) {
                className = ln.substring(ln.indexOf("class ") + "class ".length()).trim();

            } else if (ln.startsWith("enum ") || ln.contains(" enum ")) {
                className = ln.substring(ln.indexOf("enum ") + "enum ".length()).trim();

            } else if (ln.startsWith("interface ") || ln.contains(" interface ")) {
                className = ln.substring(ln.indexOf("interface ") + "interface ".length()).trim();

            } else if (ln.startsWith("@interface ") || ln.contains(" @interface ")) {
                className = ln.substring(ln.indexOf("@interface ") + "@interface ".length()).trim();

            }

            if (className != null) {
                className = className.substring(0, className.indexOf(" "));
                break;

            } else {
                line = br.readLine();

            }
        }

        String path = packageName.replaceAll("\\.", "/");
        File pkg = new File(dir, path);
        pkg.mkdirs();
        File file = new File(pkg, className + ".java");
        if (!file.exists()) {
            FileUtils.getFileUtils().createNewFile(file);
        }

        Files.write(Paths.get(file.toURI()), result.toString().getBytes(StandardCharsets.UTF_8));
    }

}
