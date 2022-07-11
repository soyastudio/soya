package soya.framework.action.dispatch;

import soya.framework.action.Action;
import soya.framework.action.ActionParser;
import soya.framework.action.ActionResult;
import soya.framework.action.Pipeline;

import java.lang.reflect.Field;

public abstract class PipelineAction<T> extends Action<T> {

    protected Pipeline pipeline;
    protected Object[] params;

    @Override
    protected void init() throws Exception {

        ActionPipeline actionPipeline = getClass().getAnnotation(ActionPipeline.class);
        Field[] fields = ActionParser.getOptionFields(getClass());

        Pipeline.Builder builder = Pipeline.builder();
        params = new Object[fields.length];

        for(int i = 0; i < fields.length; i ++) {
            Field field = fields[i];
            builder.addParameter(field.getName(), field.getType());
            field.setAccessible(true);
            params[i] = field.get(this);
        }

        for(ActionTask task : actionPipeline.tasks()) {
            Pipeline.TaskBuilder taskBuilder = builder.builderTask(task.action());

            taskBuilder.add(task.name(), task.stopOnFailure());
        }
        this.pipeline = builder.create();

    }

    @Override
    protected T execute() throws Exception {
        ActionResult result = pipeline.execute(params);
        if (result.successful()) {
            return (T) result.result();
        } else {
            throw (Exception) result.result();
        }
    }
}
