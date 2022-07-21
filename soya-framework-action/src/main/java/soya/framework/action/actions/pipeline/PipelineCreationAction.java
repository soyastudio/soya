package soya.framework.action.actions.pipeline;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.action.Pipeline;
import soya.framework.action.actions.pipeline.PipelineRegistration;
import soya.framework.action.actions.reflect.ReflectionAction;

@Command(group = "pipeline", name = "create", httpMethod = Command.HttpMethod.POST, httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class PipelineCreationAction extends PipelineAction<String> {

    @CommandOption(option = "p", dataForProcessing = true)
    private String pipeline;

    @Override
    protected String execute() throws Exception {

        Pipeline ppl = Pipeline.fromJson(pipeline);
        PipelineRegistration.getInstance().register(ppl);

        return ppl.getName().toString();
    }
}
