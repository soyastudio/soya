package soya.framework.commandline.tasks.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.helper.AntXMLContext;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

@Command(group = "apache-ant", name = "build", httpMethod = Command.HttpMethod.POST)
public class BuildTask extends AntTask<Ant> {

    @CommandOption(option = "f")
    protected String file = "build.xml";

    @CommandOption(option = "t")
    protected String target;

    @CommandOption(option = "s", dataForProcessing = true)
    protected String script;

    @Override
    protected Project createProject() throws Exception {
        if (script == null) {
            return super.createProject();

        } else {
            ProjectHelperRepository.getInstance().registerProjectHelper(DefaultProjectHelper.class);
            DefaultProjectHelper helper = (DefaultProjectHelper) ProjectHelper.getProjectHelper();

            Project project = new Project();
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

    public static class DefaultProjectHelper extends ProjectHelper2 {
        public DefaultProjectHelper() {
        }

        @Override
        public void parse(Project project, Object source, ProjectHelper2.RootHandler handler) throws BuildException {
            if (source instanceof String) {
                String script = (String) source;
                AntXMLContext context = null;
                try {
                    Field field = RootHandler.class.getDeclaredField("context");
                    field.setAccessible(true);
                    context = (AntXMLContext) field.get(handler);

                    InputStream inputStream = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
                    InputSource inputSource = new InputSource(inputStream);

                    XMLReader parser = JAXPUtils.getNamespaceXMLReader();
                    parser.setContentHandler(handler);
                    parser.setEntityResolver(handler);
                    parser.setErrorHandler(handler);
                    parser.setDTDHandler(handler);
                    parser.parse(inputSource);

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {

                }
            } else {
                super.parse(project, source, handler);
            }
        }

    }
}
