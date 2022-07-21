package soya.framework.action.dispatch;

import org.apache.tools.ant.util.LinkedHashtable;
import soya.framework.action.Action;
import soya.framework.action.ActionClass;
import soya.framework.action.ActionResult;
import soya.framework.action.Pipeline;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class PipelineAction<T> extends Action<T> {

    private Pipeline pipeline;
    private Map<String, Object> paramValues;

    @Override
    protected final void init() throws Exception {
        ActionPipeline actionPipeline = getClass().getAnnotation(ActionPipeline.class);
        Pipeline.Builder builder = Pipeline.builder();
        Map<String, Object> values = new LinkedHashtable<>();
        ActionClass actionClass = ActionClass.get(getClass());
        Field[] fields = actionClass.getActionFields();
        for (Field field : fields) {
            field.setAccessible(true);
            values.put(field.getName(), field.get(this));
            builder.addParameter(field.getName(), field.getType());
        }

        ActionTask[] tasks = actionPipeline.tasks();
        for (ActionTask task : tasks) {
            builder.createTask(task.command()).add(task.name(), task.stopOnFailure());
        }

        this.pipeline = builder.create();
        this.paramValues = values;

    }

    @Override
    protected final T execute() throws Exception {
        ActionResult result = pipeline.execute(paramValues.values().toArray(new Object[paramValues.size()]));

        if (result.successful()) {
            return (T) result.result();
        } else {
            throw (Exception) result.result();
        }
    }
}
