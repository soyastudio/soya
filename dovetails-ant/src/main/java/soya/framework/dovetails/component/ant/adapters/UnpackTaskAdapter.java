package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.JsonElement;
import org.apache.tools.ant.taskdefs.Unpack;
import soya.framework.dovetails.ProcessContext;

public abstract class UnpackTaskAdapter<T extends Unpack> extends AntTaskAdapterSupport<T> {
    public UnpackTaskAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);
    }


}
