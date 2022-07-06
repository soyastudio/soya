package soya.framework.action.actions.ant;

import soya.framework.action.Action;
import soya.framework.action.ActionResult;
import soya.framework.action.CommandGroup;
import soya.framework.action.CommandOption;

import java.io.File;

@CommandGroup(group = "apache-ant", title = "Apache Ant", description = "Toolkit for apache ant task and script.")
public abstract class AntAction extends Action<Object> {

    @CommandOption(option = "h", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.ant.home")
    protected String home;

    @CommandOption(option = "b")
    protected String basedir = ".";

    @CommandOption(option = "o")
    protected String resultName;

    protected ProjectSession project;

    @Override
    protected Object execute() throws Exception {
        ProjectSession project = createProjectSession();
        configure(project);
        execute(project);

        if(resultName != null) {
            return project.getResult(resultName);
        } else {
            return project.printEvents();
        }
    }

    @Override
    protected void init() throws Exception {
        super.init();
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
