package soya.framework.dovetails.component.ant.adapters.git;

import com.google.gson.JsonElement;
import com.rimerosolutions.ant.git.tasks.CloneTask;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AntTaskDef(name = "git-clone", attributes = {"uri", "dir", "branchToTrack", "bare", "cloneSubModule",
        "cloneAllBranches", "noCheckout"})
public class GitCloneAdapter extends GitTaskAdapter<CloneTask> {

    private String branchToTrack;
    private boolean bare = false;
    private boolean cloneSubModules = true;
    private boolean cloneAllBranches = false;
    private boolean noCheckout = false;
    private List<String> branchNames = new ArrayList<String>();

    public GitCloneAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(CloneTask task, TaskSession session) {
        task.setUri(uri);

        int startIndex = uri.lastIndexOf('/');
        int endIndex = uri.lastIndexOf('.');
        String repository = uri.substring(startIndex + 1, endIndex);

        File d = new File(getDir(dir), repository);
        task.setDirectory(d);

        if (branchToTrack != null) {
            task.setBranchToTrack(branchToTrack);
        }

        task.setBare(bare);
        task.setCloneSubModules(cloneSubModules);
        task.setNoCheckout(noCheckout);

    }
}
