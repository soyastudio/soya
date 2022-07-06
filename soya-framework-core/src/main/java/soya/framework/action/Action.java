package soya.framework.action;

import org.apache.commons.cli.Options;

import java.lang.reflect.Field;

public abstract class Action<T> implements ActionCallable {

    @Override
    public ActionResult call() {
        try {
            init();
            return succeeded(this, execute());

        } catch (Exception e) {
            return failed(this, e);

        } finally {
            try {
                close();
            } catch (Exception e) {
                return failed(this, e);
            }
        }
    }

    protected void init() throws Exception {

    }

    protected void close() throws Exception {

    }

    protected abstract T execute() throws Exception;

    public static ActionResult succeeded(ActionCallable action, Object result) {
        return new DefaultActionResult(action, result, true);
    }

    public static ActionResult failed(ActionCallable action, Throwable exception) {
        return new DefaultActionResult(action, exception, false);
    }

    static class DefaultActionResult implements ActionResult {
        private transient final ActionCallable action;

        private final ActionName name;
        private final Options options;

        private final boolean successful;
        private final Object result;

        DefaultActionResult(ActionCallable action, Object result, boolean successful) {
            this.action = action;
            if (action.getClass().getAnnotation(Command.class) == null) {
                throw new IllegalArgumentException();
            }
            this.name = ActionName.fromClass(action.getClass());
            this.options = ActionParser.parse(action.getClass());
            this.result = result;

            this.successful = successful;
        }

        @Override
        public ActionName name() {
            return name;
        }

        @Override
        public Object option(String option) {
            Field[] fields = ActionParser.getOptionFields(action.getClass());
            for(Field field: fields) {
                CommandOption commandOption = field.getAnnotation(CommandOption.class);
                if(field.getName().equals(option) || commandOption.option().equals(option)) {
                    field.setAccessible(true);
                    try {
                        return field.get(action);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            throw new IllegalArgumentException("Option '" + option + "' is not defined in action: " + name);
        }

        @Override
        public boolean successful() {
            return successful;
        }

        @Override
        public Object result() {
            return result;
        }
    }

}
