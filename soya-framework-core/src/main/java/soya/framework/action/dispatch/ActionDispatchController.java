package soya.framework.action.dispatch;

import soya.framework.action.ActionContext;
import soya.framework.action.ActionExecutor;
import soya.framework.action.ActionOption;
import soya.framework.action.ActionResult;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ActionDispatchController {

    private static ActionContext context;

    private static Map<Class<?>, Object> dispatchers = new ConcurrentHashMap<>();
    private static Map<RegisterName, DispatchMethod> dispatchMethods = new ConcurrentHashMap<>();

    static {
        context = ActionContext.getInstance();
    }

    public static Object dispatch(Class<?> dispatcher, String methodName, Object[] args) throws ActionDispatchException {
        ActionDispatch actionDispatch = dispatcher.getAnnotation(ActionDispatch.class);
        if (actionDispatch == null) {
            throw new ActionDispatchException("Class is not annotated as 'ActionDispatch': " + dispatcher.getName());
        }

        Object disp = dispatchers.get(dispatcher);
        if (disp == null) {
            disp = create(dispatcher);
            dispatchers.put(dispatcher, disp);

        }


        return null;
    }

    private static Object create(Class<?> dispatcher) {
        if (!dispatcher.isInterface()) {
            if (Modifier.isAbstract(dispatcher.getModifiers())) {
                throw new ActionDispatchException("Dispatcher class should be defined either as interface or final class: " + dispatcher.getName());
            }

            return newInstance(dispatcher);

        } else {
            return proxy(dispatcher);
        }
    }

    private static Object newInstance(Class<?> dispatcher) {
        return null;
    }

    private static Object proxy(Class<?> dispatcher) {
        return null;
    }

    private static final class RegisterName {
        private final String dispatcher;
        private final String methodName;

        private RegisterName(Class<?> dispatcher, String methodName) {
            this.dispatcher = dispatcher.getName();
            this.methodName = methodName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RegisterName)) return false;
            RegisterName that = (RegisterName) o;
            return Objects.equals(dispatcher, that.dispatcher) && Objects.equals(methodName, that.methodName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dispatcher, methodName);
        }
    }

    private static class DispatchMethod {
        private RegisterName registerName;
        private ActionExecutor actionExecutor;

        private Class<?> returnType;
        private boolean async;

        private DispatchMethod(Class<?> dispatcher, String methodName) {
            this.registerName = new RegisterName(dispatcher, methodName);
            Method[] methods = dispatcher.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getAnnotation(ActionForward.class) != null) {
                    ActionForward actionForward = method.getAnnotation(ActionForward.class);
                    ActionExecutor.Builder builder = ActionExecutor.builder(actionForward.command());
                    if(actionForward.options().length > 0) {
                        for (ActionOptionSetting setting: actionForward.options()) {
                            if(!setting.value().isEmpty()) {
                                builder.setOptionDefaultValue(setting.option(), setting.value());

                            } else if(setting.index() >= 0) {
                                builder.defineParameter(setting.option());

                            } else if(!setting.property().isEmpty()) {
                                builder.setOptionDefaultValue(setting.option(), ActionContext.getInstance().getProperty(setting.property()));

                            } else {

                            }
                        }
                    }

                    this.actionExecutor = builder.create();
                    this.returnType = method.getReturnType();
                    this.async = actionForward.async();

                    break;
                }
            }

            throw new ActionDispatchException("Cannot find dispatch method " + methodName + " for class " + dispatcher.getName());
        }

        Object execute(Object[] args) throws ActionDispatchException {

            try {
                ActionResult actionResult = actionExecutor.execute(args);
                return convert(actionResult.result(), returnType);

            } catch (Exception e) {
                throw new ActionDispatchException(e);
            }
        }

        private Object convert(Object o, Class<?> t) {
            return null;
        }
    }

}
