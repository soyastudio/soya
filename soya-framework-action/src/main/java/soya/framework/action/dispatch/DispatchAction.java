package soya.framework.action.dispatch;

import soya.framework.action.ActionProperty;
import soya.framework.action.*;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Future;

public abstract class DispatchAction<T> extends Action<T> {

    private ActionCallable _action;
    private boolean _async;

    @Override
    protected final void init() throws Exception {
        Map<String, Object> values = new Hashtable<>();
        ActionClass actionClass = ActionClass.get(getClass());
        Field[] fields = actionClass.getActionFields();
        for (Field field : fields) {
            field.setAccessible(true);
            values.put(field.getName(), field.get(this));
        }

        ActionDispatch actionDispatch = getClass().getAnnotation(ActionDispatch.class);
        ActionSignature signature = ActionSignature.builder(actionDispatch.command()).create();

        ActionClass destClass = ActionClass.get(signature.getActionName());
        this._action = destClass.newInstance();
        this._async = actionDispatch.async();

        Field[] destFields = destClass.getActionFields();
        for (Field field : destFields) {
            field.setAccessible(true);
            ActionProperty parameter = signature.getParameter(field.getName());

            ActionPropertyType type = parameter.getType();
            switch (type) {
                case arg:
                    field.set(_action, values.get(parameter.getExpression()));
                    break;
                case prop:
                    field.set(_action, ActionContext.getInstance().getProperty(parameter.getExpression()));
                    break;
                case ref:
                    throw new IllegalArgumentException("Reference parameter is not supported for ActionForward.");
                case res:
                    field.set(_action, Resource.create(parameter.getExpression()));
                    break;
                default:
                    field.set(_action, parameter.getExpression());
            }
        }
    }

    @Override
    protected final T execute() throws Exception {
        ActionResult actionResult;
        if (_async) {
            Future<ActionResult> future = ActionContext.getInstance().getExecutorService().submit(_action);
            while (future.isDone()) {
                Thread.sleep(150);
            }

            actionResult = future.get();

        } else {
            actionResult = _action.call();
        }

        if (!actionResult.successful()) {
            throw (Exception) actionResult.result();

        } else {
            return convert(actionResult.result());
        }
    }

    private T convert(Object o) {
        // TODO
        return (T) o;
    }
}
