package soya.framework.dovetails.support;

import soya.framework.ExceptionHandler;
import soya.framework.UnhandledException;
import soya.framework.dovetails.*;
import soya.framework.support.DefaultExceptionHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class DefaultTaskFlowController implements TaskFlowController {
    private static Logger LOGGER = Logger.getLogger("FLOW CONTROLLER");
    private static DefaultTaskFlowController me;

    private final ExecutorService executorService;
    private final ExceptionHandler exceptionHandler;

    private DefaultTaskFlowController(ExecutorService executorService, ExceptionHandler exceptionHandler) {
        this.executorService = executorService;
        this.exceptionHandler = exceptionHandler;
    }

    public static DefaultTaskFlowController getInstance() {
        return me;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TaskSession process(TaskFlow chain, ProcessContext context) {
        TaskSession session = new TaskSession(context);
        return new TaskChainProcessor(session, chain, exceptionHandler).process();
    }

    @Override
    public Future<TaskSession> submit(TaskFlow chain, ProcessContext context) {
        TaskSession session = new TaskSession(context);
        return new TaskChainProcessor(session, chain, exceptionHandler).submit(executorService);
    }

    public static class Builder {
        private boolean singleton = true;

        private ExecutorService executorService;
        private DefaultExceptionHandler.ExceptionHandlerBuilder exceptionHandlerBuilder = DefaultExceptionHandler.builder();

        private Set<String> scanPackages;

        private Builder() {
            scanPackages = new HashSet<>();
            scanPackages.add(Dovetails.class.getPackage().getName());
        }

        public Builder setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder addScanPackage(String packageName) {
            scanPackages.add(packageName);
            return this;
        }

        public <E extends Throwable> Builder addExceptionHandler(Class<E> exceptionType, ExceptionHandler<E> handler) {
            this.exceptionHandlerBuilder.addHandler(exceptionType, handler);
            return this;
        }

        public DefaultTaskFlowController create() {
            if (singleton && me != null) {
                throw new IllegalStateException("TaskChainController is already created.");
            }

            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            DefaultTaskFlowController controller = new DefaultTaskFlowController(executorService, exceptionHandlerBuilder.create());
            if (singleton) {
                me = controller;
            }

            return controller;
        }
    }

    public static class TaskChainProcessor {
        private TaskSession session;
        private TaskFlow chain;
        private ExceptionHandler exceptionHandler;

        private TaskChainProcessor(TaskSession session, TaskFlow chain, ExceptionHandler exceptionHandler) {
            this.session = session;
            this.chain = chain;
            this.exceptionHandler = exceptionHandler;
        }

        public TaskSession process() {
            for (Task task : chain.tasks()) {
                try {
                    task.process(session);

                } catch (Exception e) {
                    if (!exceptionHandler.onException(e)) {
                        throw new UnhandledException(e, session);
                    }
                }
            }

            return session;
        }

        public Future<TaskSession> submit(ExecutorService executorService) {
            return executorService.submit(() -> {
                return process();
            });
        }
    }
}
