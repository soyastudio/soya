package soya.framework.support;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import soya.framework.ExceptionHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExceptionHandlingService implements ExceptionHandler<Exception> {

    private static Logger logger = Logger.getLogger(ExceptionHandlingService.class.getName());
    private static ExceptionHandlingService instance;
    private static ImmutableMap<Class<? extends Exception>, ExceptionHandler> handlers;

    static {
        instance = new ExceptionHandlingService();
        handlers = ImmutableMap.<Class<? extends Exception>, ExceptionHandler>builder()
                .put(Exception.class, instance)
                .build();

        try {
            ClassPath.from(ExceptionHandlingService.class.getClassLoader()).getTopLevelClassesRecursive(ExceptionHandlingService.class.getPackage().getName()).forEach(c -> {
                if(Exception.class.isAssignableFrom(c.load())) {
                    System.out.println("----------- Exception type: " + c.getName());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExceptionHandlingService getInstance() {
        return instance;
    }

    private ExceptionHandlingService() {
    }

    public void handleException(Exception e) {
        getHandler(e).onException(e);

    }

    private ExceptionHandler getHandler(Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e.getCause());

        Class<?> type = e.getClass();
        while (!handlers.containsKey(type)) {
            type = type.getSuperclass();
        }

        return handlers.get(type);
    }

    private Throwable getRoot(Exception e) {
        Throwable root = e;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        return root;
    }

    @Override
    public boolean onException(Exception e) {
        return false;
    }
}
