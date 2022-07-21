package soya.framework.action.actions.pipeline;

import soya.framework.action.Command;

@Command(group = "pipeline", name = "pipeline", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class PipelineDefinitionAction extends PipelineAction<String>{
    @Override
    protected String execute() throws Exception {
        return null;
    }
}
