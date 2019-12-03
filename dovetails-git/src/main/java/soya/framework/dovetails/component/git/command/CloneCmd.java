package soya.framework.dovetails.component.git.command;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.util.FS;
import soya.framework.dovetails.PropertyDef;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.git.GitCmd;
import soya.framework.dovetails.support.TaskSessionUtils;
import soya.framework.util.EmptyUtils;

import java.io.File;
import java.util.Collection;

public class CloneCmd extends GitCmd<CloneCommand> {
    @PropertyDef()
    private String uri;

    @PropertyDef()
    private String directory;

    @PropertyDef
    private boolean bare;

    @PropertyDef
    private boolean cloneAllBranches;

    @PropertyDef
    private boolean cloneSubmodules;

    @PropertyDef
    private boolean noCheckout;

    @PropertyDef
    private boolean directoryExistsInitially;

    @PropertyDef
    private boolean gitDirExistsInitially;

    private File gitDir;
    private FS fs;
    private String remote = "origin";
    private String branch = "HEAD";
    private ProgressMonitor monitor;
    private Collection<String> branchesToClone;
    private CloneCommand.Callback callback;

    @Override
    protected CloneCommand create(TaskSession session) {
        CloneCommand command = Git.cloneRepository();
        command.setURI(EmptyUtils.validate(uri, "uri"));
        command.setDirectory(TaskSessionUtils.getDirectory(EmptyUtils.validate(directory, "directory"), session));
        return command;
    }

    private String getDest(String dir, String uri) {
        return dir + uri.substring(uri.lastIndexOf("/"), uri.lastIndexOf("."));

    }

}
