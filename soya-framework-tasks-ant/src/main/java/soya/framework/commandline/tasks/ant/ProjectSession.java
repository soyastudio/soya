package soya.framework.commandline.tasks.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.helper.AntXMLContext;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.taskdefs.Typedef;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import soya.framework.commandline.TaskResult;
import soya.framework.util.CodeBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectSession extends Project {

    private static final String ANTLIB = "soya/framework/commandline/tasks/ant/antlib.xml";
    private static final DefaultProjectHelper helper;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private Listener listener = new Listener();

    private long createdTimestamp;
    private Map<String, TaskResult> results = new LinkedHashMap<>();

    static {
        ProjectHelperRepository.getInstance().registerProjectHelper(DefaultProjectHelper.class);
        helper = (DefaultProjectHelper) ProjectHelper.getProjectHelper();
    }

    public ProjectSession(File baseDir) {
        super();
        this.createdTimestamp = System.currentTimeMillis();
        setBaseDir(baseDir);

        Typedef typedef = new Typedef();
        typedef.setProject(this);
        typedef.setResource(ANTLIB);
        typedef.execute();

        addBuildListener(listener);
    }

    public void configure(Object source) {
        try {
            helper.parse(this, source);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setResult(String name, TaskResult result) {
        results.put(name, result);
    }

    public TaskResult getResult(String name) {
        return results.get(name);
    }

    public String printEvents() {
        CodeBuilder builder = CodeBuilder.newInstance();
        listener.events.forEach(w -> {
            BuildEvent e = w.getEvent();
            if (e.getPriority() < 4 && e.getMessage() != null) {
                builder.append(DATE_FORMAT.format(new Date(w.timestamp))).append("\t");
                Object source = e.getSource();
                if (source instanceof Project) {
                    Project project = (Project) source;
                    builder.append("Project[").append(project.getName()).append("]: ");

                } else if (source instanceof Target) {
                    Target target = (Target) source;
                    builder.append("Target[").append(target.getName()).append("]: ");

                } else if (source instanceof Task) {
                    Task task = (Task) source;
                    builder.append("Task[").append(task.getClass().getSimpleName()).append("]: ");
                }

                builder.appendLine(e.getMessage());
            }
        });
        builder.appendLine();

        return builder.toString();
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
        private List<BuildEventWrapper> events = new ArrayList<>();

        @Override
        public void buildStarted(BuildEvent buildEvent) {
            events.add(new BuildEventWrapper(buildEvent));
        }

        @Override
        public void buildFinished(BuildEvent buildEvent) {
            events.add(new BuildEventWrapper(buildEvent));
        }

        @Override
        public void targetStarted(BuildEvent buildEvent) {
            buildEvent.setMessage("Target started", buildEvent.getPriority());
            events.add(new BuildEventWrapper(buildEvent));
        }

        @Override
        public void targetFinished(BuildEvent buildEvent) {
            buildEvent.setMessage("Target finished", buildEvent.getPriority());
            events.add(new BuildEventWrapper(buildEvent));
        }

        @Override
        public void taskStarted(BuildEvent buildEvent) {
            events.add(new BuildEventWrapper(buildEvent));
        }

        @Override
        public void taskFinished(BuildEvent buildEvent) {
            events.add(new BuildEventWrapper(buildEvent));
        }

        @Override
        public void messageLogged(BuildEvent buildEvent) {
            events.add(new BuildEventWrapper(buildEvent));
        }
    }

    static class BuildEventWrapper {
        private final long timestamp;
        private final BuildEvent event;

        BuildEventWrapper(BuildEvent event) {
            this.timestamp = System.currentTimeMillis();
            this.event = event;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public BuildEvent getEvent() {
            return event;
        }
    }

}
