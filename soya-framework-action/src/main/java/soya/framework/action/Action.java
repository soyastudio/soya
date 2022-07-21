package soya.framework.action;

public abstract class Action<T> implements ActionCallable {

    protected Action() {
    }

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
        private final ActionName name;

        private final boolean successful;
        private final Object result;

        DefaultActionResult(ActionCallable action, Object result, boolean successful) {
            if (action.getClass().getAnnotation(Command.class) == null) {
                throw new IllegalArgumentException();
            }
            this.name = ActionName.fromClass(action.getClass());
            this.result = result;

            this.successful = successful;
        }

        @Override
        public ActionName name() {
            return name;
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
