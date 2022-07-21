package soya.framework.action.actions.pipeline;

import soya.framework.action.Command;

@Command(group = "pipeline", name = "execute", httpMethod = Command.HttpMethod.POST)
public class PipelineExecutionAction extends PipelineAction<Object>{
    @Override
    protected Object execute() throws Exception {
        return null;
    }
}
