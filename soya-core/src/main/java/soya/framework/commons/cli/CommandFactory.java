package soya.framework.commons.cli;

public interface CommandFactory {
    CommandCallable create(String commandline, CommandExecutor.Context ctx);
}
