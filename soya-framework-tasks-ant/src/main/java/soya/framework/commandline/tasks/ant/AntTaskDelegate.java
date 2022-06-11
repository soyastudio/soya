package soya.framework.commandline.tasks.ant;

import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;
import soya.framework.util.CodeBuilder;

@Command(group = "apache-ant", name = "task-delegate", httpMethod = Command.HttpMethod.POST, httpRequestTypes = {Command.MediaType.APPLICATION_XML})
public class AntTaskDelegate extends AntTask {
    private static final String TARGET_NAME = "delegate";

    @CommandOption(option = "s", dataForProcessing = true, required = true)
    protected String script;


    @Override
    protected void configure(ProjectSession project) throws Exception {
        project.configure(createBuildScript());
    }

    @Override
    protected void execute(ProjectSession project) throws Exception {
        project.executeTarget(TARGET_NAME);
    }

    private String createBuildScript() {
        CodeBuilder builder = CodeBuilder.newInstance();
        builder.appendLine("<project name=\"task-delegate\" default=\"delegate\" basedir=\".\">");
        builder.append("<target name=\"", 1).append(TARGET_NAME).appendLine("\">");

        builder.appendLine(script);

        builder.appendLine("</target>", 1);
        builder.appendLine("</project>");

        return builder.toString();
    }
}
