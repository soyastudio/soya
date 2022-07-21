package soya.framework.action.actions.pipeline;

import com.google.gson.GsonBuilder;
import soya.framework.action.ActionName;
import soya.framework.action.Command;
import soya.framework.action.actions.reflect.ReflectionAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "reflect", name = "pipeline-list", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class PipelineListAction extends ReflectionAction<String> {

    @Override
    protected String execute() throws Exception {
        List<ActionName> list = new ArrayList<>(PipelineRegistration.getInstance().pipelineNames());
        Collections.sort(list);
        return new GsonBuilder().setPrettyPrinting().create().toJson(list);
    }
}
