package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Expand;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "unwar", attributes = {"source", "dest", "overwrite", "encoding", "resourcesSpecified",
        "failOnEmptyArchive", "stripAbsolutePathSpec", "scanForUnicodeExtraFields", "allowFilesToEscapeDest"})
public class UnwarAdapter extends ExpandAdapter<Expand> {
    public UnwarAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }
}
