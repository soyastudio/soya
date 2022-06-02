package soya.framework.commandline.tasks.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.*;
import org.apache.tools.ant.helper.AntXMLContext;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.taskdefs.Typedef;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import soya.framework.commandline.*;
import soya.framework.util.CodeBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@CommandGroup(group = "apache-ant", title = "Apache Ant", description = "Toolkit for apache ant task and script.")
public abstract class AntTask<T extends Task> implements TaskCallable {

    public static final String ANTLIB = "soya/framework/commandline/tasks/ant/antlib.xml";

    @CommandOption(option = "h", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.ant.home")
    protected String home;

    @CommandOption(option = "b")
    protected String basedir = ".";

    protected File antHome;
    protected File workDir;

    protected ProjectSession project;
    protected Class<T> taskType;
    protected Listener listener;

    public AntTask() {
        try {
            this.taskType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            this.listener = new Listener();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskResult call() {
        try {
            antHome = new File(home);
            if (!antHome.exists()) {
                antHome.mkdirs();
            }

            if (basedir == null) {
                this.workDir = antHome;
            } else {
                this.workDir = new File(antHome, basedir);
                if (!workDir.exists()) {
                    workDir.mkdirs();
                }
            }

            this.project = createProject();
            project.executeTarget(project.getDefaultTarget());

            return TaskResult.completed(this, render(listener));

        } catch (Exception e) {
            e.printStackTrace();
            return TaskResult.failed(this, e);
        }
    }

    protected ProjectSession createProject() throws Exception {
        Command command = getClass().getAnnotation(Command.class);

        ProjectSession project = new ProjectSession();

        Typedef typedef = new Typedef();
        typedef.setProject(project);
        typedef.setResource(ANTLIB);
        typedef.execute();

        project.setName(command.group());
        project.setDefault(command.name());
        project.addBuildListener(listener);

        Target target = new Target();
        target.setName(command.name());
        target.setLocation(new Location(home));

        T task = taskType.newInstance();
        task.setProject(project);
        prepare(task);

        task.init();
        target.addTask(task);
        project.addTarget(target);

        return project;
    }

    protected abstract void prepare(T task) throws Exception;

    protected String render(Listener listener) {
        CodeBuilder builder = CodeBuilder.newInstance();
        listener.events.forEach(e -> {
            if (e.getPriority() < 4 && e.getMessage() != null) {
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

        return builder.toString();
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

}
