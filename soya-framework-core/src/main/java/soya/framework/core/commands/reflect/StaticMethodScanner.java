package soya.framework.core.commands.reflect;

import org.reflections.Reflections;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Command(group = "reflect", name = "static-method-scanner", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class StaticMethodScanner extends ReflectCommand<String> {

    @CommandOption(option = "p", required = true)
    private String packageName;

    @Override
    public String call() throws Exception {

        Reflections reflections = new Reflections(packageName);
        Set<Method> set = new HashSet<>();

        Set<String> results = reflections.getAllTypes();

        for (String e : results) {
            if (e.startsWith(packageName + ".")) {
                try {
                    Class<?> cls = Class.forName(e);

                    if (isStatic(cls)) {
                        List<Method> methods = staticMethods(cls);
                        if (methods != null && methods.size() > 0) {
                            set.addAll(methods);
                        }

                    }

                } catch (Throwable ex) {
                    //ex.printStackTrace();
                }
            }

        }

        List<String> list = new ArrayList<>();
        set.forEach(e -> {
            list.add(e.toGenericString());
        });

        Collections.sort(list);

        return GSON.toJson(list);
    }

    private boolean isStatic(Class<?> cls) {
        if (!Modifier.isPublic(cls.getModifiers())
                || cls.isInterface()
                || cls.isEnum()
                || cls.getDeclaringClass() != null
                || Throwable.class.isAssignableFrom(cls)
                || cls.getName().contains("$")) {

            return false;
        }

        /*Constructor[] constructors = cls.getConstructors();
        for (Constructor c : constructors) {
            if (Modifier.isPublic(c.getModifiers()) && c.getParameterCount() > 0) {
                return false;
            }
        }*/

        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                return false;
            }
        }

        System.out.println("============ static class: " + cls.getName());

        return true;
    }

    private List<Method> staticMethods(Class<?> cls) {
        if (cls.isInterface() || cls.isEnum() || cls.getDeclaringClass() != null) {
            return null;
        }

        Constructor[] constructors = cls.getConstructors();
        for (Constructor c : constructors) {
            if (Modifier.isPublic(c.getModifiers())) {
                return null;
            }
        }

        List<Method> staticMethods = new ArrayList<>();
        for (Method m : cls.getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers())) {
                if (!Modifier.isStatic(m.getModifiers())) {
                    return null;
                }

                if (callable(m)) {
                    staticMethods.add(m);

                }
            }
        }

        return staticMethods;

    }

    private boolean callable(Method method) {
        if (!Modifier.isPublic(method.getModifiers())
                || !Modifier.isStatic(method.getModifiers())
                || method.getParameterCount() == 0) {

            return false;
        }

        return serializable(method);
    }

    private boolean serializable(Method method) {

        /*if (!serializable(method.getReturnType())) {
            return false;
        }

        for (Class<?> paramType : method.getParameterTypes()) {
            if (!serializable(paramType)) {
                return false;
            }
        }*/

        return true;
    }

    private boolean serializable(Class<?> type) {
        Class<?> cls = type;
        if (type.isArray()) {
            cls = cls.getComponentType();
        }

        return true;
    }
}
