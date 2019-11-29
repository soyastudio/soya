package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.BZip2;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "bzip2", attributes = {"src", "destFile"})
public class BZip2Adapter extends PackAdapter<BZip2> {
    public BZip2Adapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }
}
