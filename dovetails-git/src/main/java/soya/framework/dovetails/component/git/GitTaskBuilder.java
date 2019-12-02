package soya.framework.dovetails.component.git;

import com.google.gson.JsonElement;
import org.eclipse.jgit.api.GitCommand;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.GenericTaskBuilder;

@TaskDef(schema = "git")
public class GitTaskBuilder extends GenericTaskBuilder<GitTask> {
    @Override
    protected void configure(GitTask task, JsonElement taskDefinition, ProcessContext context) throws Exception {
        String cmd = task.getPath();

        cmd = cmd.replace("-", "_").toUpperCase();
        GitCmdType type = GitCmdType.valueOf(cmd);

        GitCmd<? extends GitCommand> command = type.getType().newInstance();
        command.configure(taskDefinition, context);
        task.command = command;

    }
}
