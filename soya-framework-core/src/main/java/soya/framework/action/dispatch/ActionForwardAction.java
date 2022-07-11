package soya.framework.action.dispatch;

import soya.framework.action.*;

import java.lang.reflect.Field;
import java.util.Map;


public class ActionForwardAction<T> extends Action<T> {

    protected ActionExecutor actionExecutor;
    protected Object[] params;

    @Override
    protected void init() throws Exception {

        ActionForward actionForward = getClass().getAnnotation(ActionForward.class);
        ActionCommandLine acl = ActionCommandLine.builder(actionForward.command()).create();
        Map<String, String> parameterMappings = acl.parameterMappings();
        Field[] fields = ActionParser.getOptionFields(getClass());

        ActionExecutor.Builder builder = ActionExecutor.builder(actionForward.command());
        params = new Object[fields.length];
        for(int i = 0; i < fields.length; i ++) {
            Field field = fields[i];
            builder.defineParameter(parameterMappings.get(field.getName()));
            field.setAccessible(true);
            params[i] = field.get(this);
        }

        this.actionExecutor = builder.create();

    }

    @Override
    protected T execute() throws Exception {
        ActionResult result = actionExecutor.execute(params);
        if (result.successful()) {
            return (T) result.result();
        } else {
            throw (Exception) result.result();
        }
    }
}
