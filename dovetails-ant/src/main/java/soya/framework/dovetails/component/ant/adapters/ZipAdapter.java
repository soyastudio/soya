package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Zip;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "zip", attributes = {"destFile", "baseDir", "encoding", "includes",
        "excludes", "filesOnly", "fileSet"})
public class ZipAdapter extends BaseZipAdapter<Zip> {
    public ZipAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }
}
