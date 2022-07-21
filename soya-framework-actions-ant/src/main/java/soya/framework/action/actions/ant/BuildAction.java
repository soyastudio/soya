package soya.framework.action.actions.ant;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Command(group = "apache-ant", name = "build", httpMethod = Command.HttpMethod.POST, httpRequestTypes = {Command.MediaType.APPLICATION_XML})
public class BuildAction extends AntAction {

    @CommandOption(option = "t")
    protected String target;

    @CommandOption(option = "s", dataForProcessing = true)
    protected String script;

    @CommandOption(option = "l")
    protected boolean printLog;


    @Override
    protected void configure(ProjectSession project) throws Exception {
        if(script != null) {
            project.configure(script);

        } else {
            project.configure(new File(project.getBaseDir(), "build.xml"));
        }
    }

    @Override
    protected void execute(ProjectSession project) throws Exception {
        try {
            if(target != null) {
                project.executeTarget(target);

            } else {
                project.executeTarget(project.getDefaultTarget());

            }
        } catch (Exception ex) {
            throw ex;

        } finally {

            if(printLog) {
                printLog(project);
            }
        }
    }

    protected void printLog(ProjectSession project) throws IOException {
        File logFile = new File(project.getBaseDir(), "build.log");
        if(!logFile.exists()) {
            logFile.createNewFile();
        }

        Files.write(Paths.get(logFile.toURI()), project.printEvents().getBytes(StandardCharsets.UTF_8));

    }
}
