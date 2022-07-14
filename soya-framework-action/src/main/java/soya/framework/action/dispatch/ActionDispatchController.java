package soya.framework.action.dispatch;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.reflections.Reflections;
import soya.framework.action.ActionCallable;
import soya.framework.action.ActionContext;
import soya.framework.action.ActionResult;
import soya.framework.action.ActionSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public final class ActionDispatchController {

    private static ActionDispatchController INSTANCE;
    private static ActionContext context;

    private static Map<Class<?>, Object> dispatchers = new ConcurrentHashMap<>();
    private static Map<RegisterName, DispatchMethod> dispatchMethods = new ConcurrentHashMap<>();

    private Map<Class<?>, Object> proxies = new HashMap<>();

    static {
        context = ActionContext.getInstance();
    }

    private ActionDispatchController() {
        Reflections reflections = new Reflections();
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(ActionDispatch.class);
        set.forEach(e -> {
            proxies.put(e, proxy(e));
        });
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

        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{dispatcher});
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if (method.getAnnotation(ActionForward.class) != null) {
                DispatchMethod dispatchMethod = new DispatchMethod(method);
                return dispatchMethod.execute(args);

            } else if (method.getAnnotation(ActionPipeline.class) != null) {

            }

            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                return "Hello Tom!";
            } else {
                return proxy.invokeSuper(obj, args);
            }
        });

        return enhancer.create();
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
        private ActionSignature actionSignature;

        private Class<?> returnType;
        private boolean async;

        private DispatchMethod(Method method) {

            this.registerName = new RegisterName(method.getDeclaringClass(), method.getName());
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                String paramName = parameter.getAnnotation(ActionParameter.class).value();
            }

            if (method.getAnnotation(ActionForward.class) != null) {
                ActionForward actionForward = method.getAnnotation(ActionForward.class);
                ActionSignature.Builder builder = ActionSignature.builder(actionForward.command());
                /*if (actionForward.options().length > 0) {
                    for (ActionOptionSetting setting : actionForward.options()) {
                        if (!setting.value().isEmpty()) {
                            builder.setOptionDefaultValue(setting.option(), setting.value());

                        } else if (setting.index() >= 0) {
                            builder.defineParameter(setting.option());

                        } else if (!setting.property().isEmpty()) {
                            builder.setOptionDefaultValue(setting.option(), ActionContext.getInstance().getProperty(setting.property()));

                        } else {

                        }
                    }
                }*/

                this.actionSignature = builder.create();
                this.returnType = method.getReturnType();
                this.async = actionForward.async();
            }
        }

        Object execute(Object[] args) throws ActionDispatchException {
            ActionResult actionResult = null;
            try {
                ActionCallable action = actionSignature.create(args);
                if (async) {
                    Future<ActionResult> future =
                            ActionContext.getInstance().getExecutorService().submit(action);
                    while(future.isDone()) {
                        Thread.sleep(150l);
                    }

                    actionResult = future.get();

                } else {
                    actionResult = action.call();
                }

                return convert(actionResult.result(), returnType);

            } catch (Exception e) {
                throw new ActionDispatchException(e);
            }
        }

        private Object convert(Object o, Class<?> t) {
            if (t.isInstance(o)) {
                return o;
            }
            return null;
        }
    }

    public static ActionDispatchController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionDispatchController();
        }

        return INSTANCE;
    }

    public Class<?>[] dispatcherInterfaces() {
        return proxies.keySet().toArray(new Class[proxies.size()]);
    }

    public <T> T getProxy(Class<T> dispatcherType) {
        return (T) proxies.get(dispatcherType);
    }

}
