package soya.framework.albertsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.samskivert.mustache.Mustache;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

public class ProjectCommands {

    private static String REQUIREMENT_DIR = "requirement";
    private static String WORK_DIR = "work";
    private static String TEST_DIR = "test";
    private static String HISTORY_DIR = "history";

    private static String XPATH_SCHEMA_FILE = "xpath-schema.properties";
    private static String XPATH_MAPPING_FILE = "xpath-mapping.properties";
    private static String XPATH_ADJUSTMENT_FILE = "xpath-adjustment.properties";

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Project template;

    static {
        InputStream inputStream = ProjectCommands.class.getClassLoader().getResourceAsStream("project.json");
        InputStreamReader reader = new InputStreamReader(inputStream);
        template = GSON.fromJson(reader, Project.class);
    }

    public static String create(CommandLine cmd) throws Exception {
        File base = new File(cmd.getOptionValue("w"));
        String bod = cmd.getOptionValue("b");
        File boDir = new File(base, bod);
        if (boDir.exists()) {
            return null;

        } else {
            FileUtils.forceMkdir(boDir);
            File projectFile = new File(boDir, "project.json");
            projectFile.createNewFile();

            Project project = GSON.fromJson(GSON.toJson(template), Project.class);
            project.setName(bod);

            FileUtils.write(projectFile, GSON.toJson(project), Charset.defaultCharset());

            File reqDir = new File(boDir, REQUIREMENT_DIR);
            FileUtils.forceMkdir(reqDir);

            File workDir = new File(boDir, WORK_DIR);
            FileUtils.forceMkdir(workDir);

            File testDir = new File(boDir, TEST_DIR);
            FileUtils.forceMkdir(testDir);

            File histDir = new File(boDir, HISTORY_DIR);
            FileUtils.forceMkdir(histDir);

            return GSON.toJson(project);

        }
    }

    public static String get(CommandLine cmd) throws Exception {
        File base = new File(cmd.getOptionValue("w"));
        String bod = cmd.getOptionValue("b");
        File boDir = new File(base, bod);

        File projectFile = new File(boDir, "project.json");
        Project project;
        if (!projectFile.exists()) {
            project = template;

        } else {
            project = GSON.fromJson(new FileReader(projectFile), Project.class);
        }

        return GSON.toJson(project);
    }

    public static String readme(CommandLine cmd) throws Exception {
        File base = new File(cmd.getOptionValue("w"));
        String bod = cmd.getOptionValue("b");
        File boDir = new File(base, bod);

        File readme = new File(boDir, "readme.md");

        return IOUtils.toString(new FileInputStream(readme), Charset.defaultCharset());
    }

    public static String version(CommandLine cmd) throws Exception {
        System.out.println("================ versioning...");
        return null;
    }

    public static String cutoff(CommandLine cmd) throws Exception {

        System.out.println("================ cutoff...");

        return null;
    }

    private static String mustache(String template, Object data) throws IOException {
        JsonObject json = GSON.toJsonTree(data).getAsJsonObject();
        InputStream inputStream = ProjectCommands.class.getClassLoader().getResourceAsStream(template);
        return Mustache.compiler().compile(
                new InputStreamReader(inputStream)).execute(JsonUtils.toMap(json));
    }

}
