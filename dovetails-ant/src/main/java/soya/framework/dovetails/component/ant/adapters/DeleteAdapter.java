package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Delete;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "delete", attributes = {"file", "dir", "fileSets", "verbose", "quiet", "includeEmpty"})
public class DeleteAdapter extends AntTaskAdapterSupport<Delete> {

    private String file;
    private String dir;
    private FileSetModel[] fileSets;
    private boolean verbose = false;
    private boolean quiet = false;
    private boolean includeEmptyDirs = false;
    private boolean failOnError = true;
    private boolean deleteOnExit = false;
    private boolean removeNotFollowedSymlinks = false;
    private boolean performGcOnFailedDelete = false;

    public DeleteAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Delete task, TaskSession session) {
        if (file != null) {
            task.setFile(getFile(file));
        }

        if (dir != null) {
            task.setDir(getDir(dir));
        }

        if (fileSets != null && fileSets.length > 0) {
            for (FileSetModel model : fileSets) {
                task.addFileset(getFileSet(model));
            }
        }

        task.setVerbose(verbose);
        task.setQuiet(quiet);
        task.setIncludeEmptyDirs(includeEmptyDirs);
        task.setFailOnError(failOnError);
        task.setDeleteOnExit(deleteOnExit);
        task.setRemoveNotFollowedSymlinks(removeNotFollowedSymlinks);
        task.setPerformGcOnFailedDelete(performGcOnFailedDelete);
    }
}
