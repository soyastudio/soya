package soya.framework.tasks.jgit;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.reflections.Reflections;
import soya.framework.commandline.Command;
import soya.framework.commandline.TaskResult;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Command(group = "git", name = "clone", httpMethod = Command.HttpMethod.GET)
public class CloneTask extends GitTask<CloneCommand> {

    @Override
    public TaskResult call() throws Exception {
        Reflections reflections = new Reflections();

        List<String> list = new ArrayList<>();
        Set<Class<? extends GitCommand>> set = reflections.getSubTypesOf(GitCommand.class);
        set.forEach(c -> {
            if (!Modifier.isAbstract(c.getModifiers())) {
                list.add(c.getName());
            }
        });

        Collections.sort(list);
        list.forEach(e -> {
            System.out.println("============ " + e);
        });


        ProgressMonitor monitor = new ProgressLogger();
        Git git = Git.cloneRepository()
                .setURI("https://github.com/soyastudio/Calabash.git")
                .setDirectory(new File("C:/github/jgit"))
                .setProgressMonitor(monitor)
                .call();

        return TaskResult.completed(this, "");
    }
}
