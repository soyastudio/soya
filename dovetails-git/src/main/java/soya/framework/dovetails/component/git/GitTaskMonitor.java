package soya.framework.dovetails.component.git;

import org.eclipse.jgit.lib.ProgressMonitor;

public class GitTaskMonitor implements ProgressMonitor {

    public static final String MESSAGE_STARTING = "starting";
    public static final String MESSAGE_BEGIN = "begin";
    public static final String MESSAGE_STATUS = "status";
    public static final String MESSAGE_ENDING = "ending";
    private final GitTask task;
    private int totalTasks;

    public GitTaskMonitor(GitTask task) {
        this.task = task;
    }

    @Override
    public void start(int totalTasks) {
        this.totalTasks = totalTasks;
        task.log(String.format("[%s] %s", task.getName(), MESSAGE_STARTING));
    }

    @Override
    public void beginTask(String title, int totalWork) {
        task.log(String.format("[%s] %s", title, MESSAGE_BEGIN));
    }

    @Override
    public void update(int completed) {
        task.log(String.format("[%s] %s [%d/%d]", task.getName(), MESSAGE_STATUS, completed, totalTasks));
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void endTask() {
        task.log(String.format("[%s] %s", task.getName(), MESSAGE_ENDING));
    }

}
