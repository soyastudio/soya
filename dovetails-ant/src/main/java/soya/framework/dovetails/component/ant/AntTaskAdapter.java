package soya.framework.dovetails.component.ant;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.tools.ant.Task;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.util.ParameterizedText;

import java.lang.reflect.Field;

public abstract class AntTaskAdapter<T extends Task> {
    private final String name;
    private final T antTask;

    public AntTaskAdapter(JsonElement attributes, ProcessContext context) {
        AntTaskDef def = getClass().getAnnotation(AntTaskDef.class);
        this.name = def.name();
        if (attributes != null) {
            JsonObject json = attributes.getAsJsonObject();
            for (String attr : def.attributes()) {
                if (json.get(attr) != null) {
                    try {
                        Field field = getClass().getDeclaredField(attr);
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        field.set(this, evaluate(json.get(attr), fieldType, context));

                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


        this.antTask = createAntTask(context);
    }

    public String getName() {
        return name;
    }

    public final void execute() {
        antTask.execute();
    }

    protected void preExecute(TaskSession session) {

    }

    protected void postExecute(TaskSession session) {

    }

    protected abstract T createAntTask(ProcessContext context);


    private Object evaluate(JsonElement exp, Class<?> type, ProcessContext context) {
        Object result = null;
        if (exp.isJsonPrimitive() && exp.getAsString().contains("${")) {
            String value = exp.getAsString();
            ParameterizedText pt = ParameterizedText.create(value);
            for (String param : pt.getParameters()) {
                pt = pt.evaluate(param, context.getProperty(param));
            }
            result = pt.toString();

        } else {
            result = new Gson().fromJson(exp, type);

        }

        return result;
    }
}
