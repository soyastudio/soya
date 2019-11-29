package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.War;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "war", attributes = {"destFile", "baseDir", "encoding", "includes",
        "excludes", "manifest", "fileSet", "classes", "lib", "filesOnly"})
public class WarAdapter extends BaseZipAdapter<War> {
    private String manifestFile;

    private String webxml;
    private FileSetModel[] fileSet;
    private FileSetModel[] classes;
    private FileSetModel[] lib;

    public WarAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

    @Override
    protected void init(War task, TaskSession session) {
        super.init(task, session);
        if (manifestFile != null) {
            task.setManifest(getFile(manifestFile));
        }

        if (webxml != null) {
            task.setWebxml(getFile(webxml));
        }

        if(fileSet != null && fileSet.length > 0) {
            for(FileSetModel model: fileSet) {
                task.addFileset(getFileSet(model));
            }
        }

        if(classes != null && classes.length > 0) {
            for(FileSetModel model: classes) {
                task.addClasses(getZipFileSet(model));
            }
        }

        if(lib != null && lib.length > 0) {
            for(FileSetModel model: lib) {
                task.addLib(getZipFileSet(model));
            }
        }
    }
}
