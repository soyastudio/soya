package soya.framework.commandline.tasks.ant;

import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.TaskCallable;
import soya.framework.commandline.TaskResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@CommandGroup(group = "apache-ant", title = "Apache Ant", description = "Toolkit for apache ant task and script.")
public abstract class AntTask implements TaskCallable {

    @CommandOption(option = "h", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.ant.home")
    protected String home;

    @CommandOption(option = "b")
    protected String basedir = ".";

    @CommandOption(option = "o")
    protected String resultName;

    public AntTask() {
    }

    @Override
    public TaskResult call() {
        try {
            ProjectSession project = createProjectSession();
            configure(project);
            execute(project);

            if(resultName != null) {
                return project.getResult(resultName);
            } else {
                return TaskResult.completed(this, project.printEvents());
            }

        } catch (Exception e) {
            return TaskResult.failed(this, e);

        }
    }

    protected ProjectSession createProjectSession() {
        File antHome = new File(home);
        if (!antHome.exists()) {
            throw new IllegalArgumentException("Ant home does not exist.");
        }

        File workDir = null;
        if (basedir == null) {
            workDir = antHome;

        } else {
            workDir = new File(antHome, basedir);
            if (!workDir.exists()) {
                workDir.mkdirs();
            }
        }

        return new ProjectSession(workDir);
    }

    protected abstract void configure(ProjectSession project) throws Exception;

    protected abstract void execute(ProjectSession project) throws Exception;

}
