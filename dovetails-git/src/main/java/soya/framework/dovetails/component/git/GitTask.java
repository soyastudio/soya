package soya.framework.dovetails.component.git;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.ProcessContextAware;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.git.command.RepositoryAware;

import java.io.File;
import java.io.IOException;

public class GitTask extends Task implements ProcessContextAware {
    private ProcessContext context;

    GitCmd command;

    protected GitTask(String uri) {
        super(uri);
    }

    @Override
    public void setProcessContext(ProcessContext context) {
        this.context = context;
    }

    @Override
    public void process(TaskSession session) throws Exception {
        if (command instanceof RepositoryAware) {
            Repository repository = getRepository(session);
            Git git = new Git(repository);


        } else {
            command.create(session).call();
        }
    }

    public void log(String message) {
        // TODO:
    }

    protected Repository getRepository(TaskSession session) throws IOException {
        Repository repository = new RepositoryBuilder().
                readEnvironment().
                findGitDir(getDirectory()).
                build();
        return repository;
    }

    private File getDirectory() {
        return null;
    }
}
