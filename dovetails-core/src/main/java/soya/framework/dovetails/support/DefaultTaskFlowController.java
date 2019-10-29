package soya.framework.dovetails.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.DataObject;
import soya.framework.ExceptionHandler;
import soya.framework.Session;
import soya.framework.UnhandledException;
import soya.framework.util.ClasspathUtils;
import soya.framework.dovetails.*;
import soya.framework.support.DefaultExceptionHandler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DefaultTaskFlowController implements TaskFlowController, TaskTypeRegistration {
    private static DefaultTaskFlowController me;

    private final ImmutableMap<String, Class<? extends TaskBuilder>> taskBuilderTypes;

    private final ProcessContext context;
    private final ExecutorService executorService;
    private final ExceptionHandler exceptionHandler;

    private DefaultTaskFlowController(Set<Class<?>> taskDefClasses, ProcessContext context, ExecutorService executorService, ExceptionHandler exceptionHandler) {
        ImmutableMap.Builder<String, Class<? extends TaskBuilder>> builder = ImmutableMap.<String, Class<? extends TaskBuilder>>builder();
        taskDefClasses.forEach(e -> {
            TaskDef def = e.getAnnotation(TaskDef.class);
            String[] schemas = def.schema();
            for (String schema : schemas) {
                builder.put(schema, (Class<? extends TaskBuilder>) e);
            }
        });
        taskBuilderTypes = builder.build();


        this.context = context;
        this.executorService = executorService;
        this.exceptionHandler = exceptionHandler;
    }

    public static DefaultTaskFlowController getInstance() {
        return me;
    }

    public static TaskChainControllerBuilder builder() {
        return new TaskChainControllerBuilder();
    }

    public ProcessContext getContext() {
        return context;
    }

    @Override
    public TaskSession process(TaskFlow chain) {
        TaskSession session = new TaskSession(context);
        return new TaskChainProcessor(session, chain, exceptionHandler).process();
    }

    @Override
    public TaskSession process(TaskFlow chain, Session externalSession) {
        TaskSession session = new TaskSession(context, externalSession);
        return new TaskChainProcessor(session, chain, exceptionHandler).process();
    }

    @Override
    public Future<TaskSession> submit(TaskFlow chain) {
        TaskSession session = new TaskSession(context);
        return new TaskChainProcessor(session, chain, exceptionHandler).submit(executorService);
    }

    @Override
    public Future<TaskSession> submit(TaskFlow chain, Session externalSession) {
        TaskSession session = new TaskSession(context, externalSession);
        return new TaskChainProcessor(session, chain, exceptionHandler).submit(executorService);
    }

    @Override
    public Class<? extends TaskBuilder> getTaskBuilderType(String schema) {
        return taskBuilderTypes.get(schema);
    }

    public static class TaskChainControllerBuilder {
        private boolean singleton = true;

        private Set<Class<?>> taskDefClasses = new HashSet<>();

        private ProcessContext context;
        private ExecutorService executorService;
        private DefaultExceptionHandler.ExceptionHandlerBuilder exceptionHandlerBuilder = DefaultExceptionHandler.builder();

        private Set<String> scanPackages;

        private TaskChainControllerBuilder() {
            scanPackages = new HashSet<>();
            scanPackages.add(Dovetails.class.getPackage().getName());
        }

        public TaskChainControllerBuilder setProcessContext(ProcessContext context) {
            this.context = context;
            return this;
        }

        public TaskChainControllerBuilder setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public TaskChainControllerBuilder addScanPackage(String packageName) {
            scanPackages.add(packageName);
            return this;
        }

        public <E extends Throwable> TaskChainControllerBuilder addExceptionHandler(Class<E> exceptionType, ExceptionHandler<E> handler) {
            this.exceptionHandlerBuilder.addHandler(exceptionType, handler);
            return this;
        }

        public DefaultTaskFlowController create() {
            if (singleton && me != null) {
                throw new IllegalStateException("TaskChainController is already created.");
            }

            taskDefClasses = ClasspathUtils.findByAnnotation(TaskDef.class, scanPackages.toArray(new String[scanPackages.size()]));

            if (context == null) {
                context = new DefaultProcessContext();
            }

            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            DefaultTaskFlowController controller = new DefaultTaskFlowController(taskDefClasses, context, executorService, exceptionHandlerBuilder.create());
            if (singleton) {
                me = controller;
            }

            return controller;
        }
    }

    static class DefaultProcessContext extends ProcessContextSupport {

        @Override
        public File getBaseDir() {
            return null;
        }

        @Override
        public DataObject get(String name) {
            return null;
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
                    e.printStackTrace();
                    if(!exceptionHandler.onException(e)) {
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
