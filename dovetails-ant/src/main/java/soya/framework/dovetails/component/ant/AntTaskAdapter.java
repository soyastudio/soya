package soya.framework.dovetails.component.ant;

import com.google.gson.JsonElement;
import org.apache.tools.ant.Task;
import soya.framework.dovetails.ProcessContext;

public abstract class AntTaskAdapter<T extends Task> {
    private final String name;
    private final String[] attributes;
    private final T antTask;

    public AntTaskAdapter(JsonElement attributes, ProcessContext context) {
        AntTaskDef def = getClass().getAnnotation(AntTaskDef.class);
        this.name = def.name();
        this.attributes = def.attributes();

        this.antTask = createAntTask(attributes, context);
    }

    public String getName() {
        return name;
    }

    protected String[] getAttributes() {
        return attributes;
    }

    public final void execute() {
        antTask.execute();
    }

    protected abstract T createAntTask(JsonElement attributes, ProcessContext context);
}
