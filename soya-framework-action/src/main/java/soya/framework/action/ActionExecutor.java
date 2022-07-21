package soya.framework.action;

import soya.framework.action.actions.reflect.HelpAction;

import java.util.concurrent.Future;

public class ActionExecutor {

    private final ActionClass actionClass;
    private ActionSignature.Builder signatureBuilder;
    private ActionContext context = ActionContext.getInstance();

    private ActionExecutor(ActionClass actionClass) {
        this.actionClass = actionClass;
    }

    public ActionExecutor setOption(String option, String value) {
        signatureBuilder.set(option, value);
        return this;
    }

    public ActionResult execute() throws Exception {
        ActionSignature signature = signatureBuilder.create();
        ActionCallable action = signature.create(new Object[0]);

        Future<ActionResult> future = context.getExecutorService().submit(action);
        while (future.isDone()) {
            Thread.sleep(150l);
        }

        return future.get();
    }

    public static ActionExecutor create(Class<? extends ActionCallable> actionType) {
        ActionClass actionClass = ActionClass.get(actionType);
        ActionExecutor executor = new ActionExecutor(actionClass);
        executor.signatureBuilder = ActionSignature.builder(actionClass);
        return executor;
    }

    public static void main(String[] args) {
        try {
            ActionResult result = ActionExecutor.create(HelpAction.class)
                    .execute();

            System.out.println(result.result());

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
