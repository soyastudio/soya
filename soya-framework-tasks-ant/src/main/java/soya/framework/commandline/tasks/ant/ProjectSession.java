package soya.framework.commandline.tasks.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.helper.AntXMLContext;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.taskdefs.Typedef;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import soya.framework.commandline.Resource;
import soya.framework.commandline.TaskResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProjectSession extends Project {

    private static final String ANTLIB = "soya/framework/commandline/tasks/ant/antlib.xml";
    private static final DefaultProjectHelper helper;

    private Listener listener = new Listener();

    static {
        ProjectHelperRepository.getInstance().registerProjectHelper(DefaultProjectHelper.class);
        helper = (DefaultProjectHelper) ProjectHelper.getProjectHelper();
    }

    public ProjectSession(File baseDir) {
        super();
        setBaseDir(baseDir);

        Typedef typedef = new Typedef();
        typedef.setProject(this);
        typedef.setResource(ANTLIB);
        typedef.execute();

        addBuildListener(listener);
    }

    public ProjectSession(String script, File baseDir) {
        super();

        Typedef typedef = new Typedef();
        typedef.setProject(this);
        typedef.setResource(ANTLIB);
        typedef.execute();
        addBuildListener(listener);

        try {
            helper.parse(this, script);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProjectSession(Resource resource, File baseDir) {
        super();

        Typedef typedef = new Typedef();
        typedef.setProject(this);
        typedef.setResource(ANTLIB);
        typedef.execute();
        addBuildListener(listener);
    }

    private Map<String, TaskResult> results = new LinkedHashMap<>();

    public void setResult(String name, TaskResult result) {
        results.put(name, result);
    }

    public TaskResult getResult(String name) {
        return results.get(name);
    }

    public static class DefaultProjectHelper extends ProjectHelper2 {
        public DefaultProjectHelper() {
        }

        @Override
        public void parse(Project project, Object source) throws BuildException {
            super.parse(project, source);
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

    static class Listener implements BuildListener {
        private List<BuildEvent> events = new ArrayList<>();

        @Override
        public void buildStarted(BuildEvent buildEvent) {
            events.add(buildEvent);
        }

        @Override
        public void buildFinished(BuildEvent buildEvent) {
            events.add(buildEvent);
        }

        @Override
        public void targetStarted(BuildEvent buildEvent) {
            buildEvent.setMessage("Target started", buildEvent.getPriority());
            events.add(buildEvent);
        }

        @Override
        public void targetFinished(BuildEvent buildEvent) {
            buildEvent.setMessage("Target finished", buildEvent.getPriority());
            events.add(buildEvent);
        }

        @Override
        public void taskStarted(BuildEvent buildEvent) {
            events.add(buildEvent);
        }

        @Override
        public void taskFinished(BuildEvent buildEvent) {
            events.add(buildEvent);
        }

        @Override
        public void messageLogged(BuildEvent buildEvent) {
            events.add(buildEvent);
        }
    }

}
