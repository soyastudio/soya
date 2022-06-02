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
        ProjectHelperRepository.getInstance().registerProjectHelper(DefaultProjectHelper.class);
        DefaultProjectHelper helper = (DefaultProjectHelper) ProjectHelper.getProjectHelper();
        ProjectSession project = new ProjectSession();

        if (script == null) {
            return super.createProject();

        } else {
            project.setBaseDir(workDir);
            try {
                helper.parse(project, script);
            } catch (Exception e) {
                e.printStackTrace();
            }

            project.addBuildListener(listener);

            if (target != null) {
                project.setDefault(target);
            }

            Typedef typedef = new Typedef();
            typedef.setProject(project);
            typedef.setResource(ANTLIB);
            //typedef.setURI( TASK_URI );
            typedef.execute();

            return project;
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
