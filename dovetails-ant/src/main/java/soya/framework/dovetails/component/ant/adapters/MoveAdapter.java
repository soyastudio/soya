package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Move;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "move", attributes = {"file", "toFile", "fileSet", "toDir", "includeEmptyDirs", "verbose", "quiet", "overwrite", "force", "flatten"})
public class MoveAdapter extends AntTaskAdapterSupport<Move> {
    private String file;
    private String toFile;
    private String toDir;
    private FileSetModel fileSet;
    private boolean includeEmptyDirs = true;
    private boolean verbose;
    private boolean quiet;
    private boolean overwrite;
    private boolean force;
    private boolean flatten;
    private boolean failOnError = true;

    public MoveAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(Move task, TaskSession session) {
        if (file != null) {
            task.setFile(getFile(file));
        }

        if(toFile != null) {
            task.setTofile(getFile(toFile));
        }

        if(toDir != null) {
            task.setTodir(getDir(toDir));
        }

        if(fileSet != null) {
            task.addFileset(getFileSet(fileSet));
        }

        task.setIncludeEmptyDirs(includeEmptyDirs);
        task.setVerbose(true);
        task.setQuiet(quiet);
        task.setOverwrite(overwrite);
        task.setForce(force);
        task.setFlatten(flatten);
        task.setFailOnError(failOnError);
    }
}
