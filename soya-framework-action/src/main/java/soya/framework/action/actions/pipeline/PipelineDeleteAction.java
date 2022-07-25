package soya.framework.action.actions.pipeline;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.action.Pipeline;

@Command(group = "pipeline", name = "delete", httpMethod = Command.HttpMethod.POST, httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class PipelineDeleteAction extends PipelineAction<String> {

    @CommandOption(option = "p", dataForProcessing = true)
    private String pipeline;

    @Override
    protected String execute() throws Exception {

        Pipeline ppl = Pipeline.fromJson(pipeline);
        PipelineRegistration.getInstance().register(ppl);

        return ppl.getName().toString();
    }
}
