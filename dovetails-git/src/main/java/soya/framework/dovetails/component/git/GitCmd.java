package soya.framework.dovetails.component.git;

import com.google.gson.JsonElement;
import org.eclipse.jgit.api.GitCommand;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;

import java.lang.reflect.Field;

public abstract class GitCmd<T extends GitCommand> {

    public GitCmd() {
        Field[] fields = getClass().getDeclaredFields();
    }

    protected void configure(JsonElement settings, ProcessContext context) {

    }

    protected abstract T create(TaskSession session);
}
