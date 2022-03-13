package soya.framework.commons.cli;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class CommandDispatcher {

    private final CommandExecutor _executor;

    public CommandDispatcher(CommandExecutor delegate) {
        _executor = delegate;
        if(delegate == null) {
            throw new IllegalArgumentException("CommandExecutor cannot be null!");
        }
    }

    protected String _help(String cmd) {
        return _executor.context().toString(cmd);
    }

    protected String _dispatch(String methodName, Object[] args) throws Exception {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (CommandExecutor.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
            }
        }

        return CommandExecutor.execute(getClass(), methodName, args, _executor);
    }

    protected String _execute(String commandline) throws Exception {
        return _executor.execute(commandline);
    }

    protected String encodeMessage(String message) {
        return Base64.getEncoder().encodeToString(message.getBytes());
    }

    private boolean dispatchable(String methodName, CommandExecutor delegate) {

        return true;
    }
}
