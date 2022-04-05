package soya.framework.core;

public interface CommandFactory {
    CommandCallable create(String commandline, CommandExecutor.Context ctx);
}
