package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.GUnzip;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.component.ant.AntTaskDef;

@AntTaskDef(name = "gunzip", attributes = {"src", "dest"})
public class GUnZipAdapter extends UnpackAdapter<GUnzip> {
    public GUnZipAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }

}
