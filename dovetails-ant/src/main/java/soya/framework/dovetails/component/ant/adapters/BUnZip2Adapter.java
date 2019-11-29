package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.BUnzip2;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "bunzip2", attributes = {"src", "dest"})
public class BUnZip2Adapter extends UnpackAdapter<BUnzip2> {
    public BUnZip2Adapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }
}
