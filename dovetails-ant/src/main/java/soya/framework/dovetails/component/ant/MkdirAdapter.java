package soya.framework.dovetails.component.ant;

import com.google.gson.JsonElement;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Mkdir;
import soya.framework.dovetails.ProcessContext;

import java.io.File;

@AntTaskDef(name = "mkdir", attributes = {"dir"})
public class MkdirAdapter extends AntTaskAdapter<Mkdir> {
    private String dir;

    public MkdirAdapter(JsonElement attributes, ProcessContext context) {
        super(attributes, context);

        System.out.println("============ dir: " + dir);
    }

    @Override
    protected Mkdir createAntTask(ProcessContext context) {
        Mkdir task = new Mkdir();
        task.setDir(new File(context.getBaseDir(), dir));

        return task;
    }

}
