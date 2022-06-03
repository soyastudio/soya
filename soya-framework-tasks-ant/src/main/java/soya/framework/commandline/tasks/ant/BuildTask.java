package soya.framework.commandline.tasks.ant;

import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Typedef;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

@Command(group = "apache-ant", name = "build", httpMethod = Command.HttpMethod.POST)
public class BuildTask extends AntTask<Ant> {

    @CommandOption(option = "f")
    protected String file = "build.xml";

    @CommandOption(option = "t")
    protected String target;

    @CommandOption(option = "s", dataForProcessing = true)
    protected String script;

    @Override
    protected ProjectSession createProject() throws Exception {
        if (script == null) {
            return super.createProject();

        } else {

            return new ProjectSession(script, workDir);
        }
    }

    @Override
    protected void prepare(Ant task) throws Exception {
        String buildFile = file != null ? file : "build.xml";
        task.setDir(workDir);
        task.setAntfile(buildFile);
        if (target != null) {
            task.setTarget(target);
        }

    }
}
