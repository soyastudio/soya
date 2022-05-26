package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.TextProgressMonitor;
import soya.framework.commandline.Command;
import soya.framework.commandline.TaskResult;

import java.io.File;

@Command(group = "git", name = "clone", httpMethod = Command.HttpMethod.GET)
public class CloneTask extends GitTask {

    @Override
    public TaskResult call() throws Exception {
        ProgressMonitor monitor = new ProgressLogger();
        Git git = Git.cloneRepository()
                .setURI("https://github.com/soyastudio/Calabash.git")
                .setDirectory(new File("C:/github/jgit"))
                .setProgressMonitor(monitor)
                .call();

        return TaskResult.completed(this, "");
    }
}
