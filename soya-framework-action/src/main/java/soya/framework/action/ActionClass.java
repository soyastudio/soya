package soya.framework.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ActionClass {

    private static final Map<ActionName, ActionClass> registrations = new ConcurrentHashMap<>();
    private static final Map<String, ActionClass> cache = new ConcurrentHashMap();

    private final Class<? extends ActionCallable> actionType;
    private final ActionName actionName;
    private final Field[] actionFields;

    private ActionClass(Class<? extends ActionCallable> actionType) {
        this.actionType = actionType;
        if (actionType.isInterface() || Modifier.isAbstract(actionType.getModifiers())) {
            throw new IllegalArgumentException("Action type is interface or abstract class: " + actionType.getName());
        }

        Command command = actionType.getAnnotation(Command.class);
        if (command == null) {
            throw new IllegalArgumentException("Action type is not annotated as '" + Command.class.getSimpleName() + "': " + actionType.getName());
        }
        this.actionName = ActionName.fromClass(actionType);

        List<Field> list = new ArrayList<>();
        Class superClass = actionType;
        while (!Object.class.equals(superClass)) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                if (commandOption != null) {
                    list.add(field);
                }
            }
            superClass = superClass.getSuperclass();
        }
        Collections.sort(list, new CommandFieldComparator());
        this.actionFields = list.toArray(new Field[list.size()]);
    }

    public Class<? extends ActionCallable> getActionType() {
        return actionType;
    }

    public ActionName getActionName() {
        return actionName;
    }

    public Field[] getActionFields() {
        return actionFields;
    }

    public ActionCallable newInstance() {
        try {
            Constructor constructor = actionType.getConstructor(new Class[0]);
            constructor.setAccessible(true);
            return (ActionCallable) constructor.newInstance(new Object[0]);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ActionClass get(ActionName actionName) {
        return registrations.get(actionName);
    }

    public static ActionClass get(Class<? extends ActionCallable> actionType) {
        if (!cache.containsKey(actionType.getName())) {
            ActionClass actionClass = new ActionClass(actionType);
            cache.put(actionType.getName(), actionClass);
            registrations.put(actionClass.getActionName(), actionClass);
        }
        return cache.get(actionType.getName());
    }

    static class CommandFieldComparator implements Comparator<Field> {
        @Override
        public int compare(Field o1, Field o2) {
            CommandOption commandOption1 = o1.getAnnotation(CommandOption.class);
            CommandOption commandOption2 = o2.getAnnotation(CommandOption.class);

            Class<?> cls1 = o1.getDeclaringClass();
            Class<?> cls2 = o2.getDeclaringClass();

            if (commandOption1.dataForProcessing() && !commandOption2.dataForProcessing()) {
                return 1;

            } else if (!commandOption1.dataForProcessing() && commandOption2.dataForProcessing()) {
                return -1;

            } else {
                int paramDiff = CommandOption.ParamType.indexOf(commandOption1.paramType()) - CommandOption.ParamType.indexOf(commandOption2.paramType());
                if (paramDiff != 0) {
                    return paramDiff;

                } else if (cls1.equals(cls2)) {
                    return o1.getName().compareTo(o2.getName());

                } else if (cls2.isAssignableFrom(cls1)) {
                    return 1;

                } else {
                    return -1;
                }

            }
        }
    }

}
