package soya.framework.tasks.apache.ant;

import org.apache.tools.ant.*;
import soya.framework.util.CodeBuilder;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.TaskCallable;
import soya.framework.commandline.TaskResult;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class AntTask<T extends Task> implements TaskCallable {

    @CommandOption(option = "h", paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "soya.ant.home")
    protected String home;

    @CommandOption(option = "b")
    protected String basedir = ".";

    protected File antHome;
    protected File workDir;

    protected Project project = new Project();
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

    protected Project createProject() throws Exception {
        Command command = getClass().getAnnotation(Command.class);
        Project project = new Project();
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

}
