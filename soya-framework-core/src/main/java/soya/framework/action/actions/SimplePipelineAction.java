package soya.framework.action.actions;

import soya.framework.action.Command;
import soya.framework.action.dispatch.ActionPipeline;
import soya.framework.action.dispatch.ActionTask;
import soya.framework.action.dispatch.PipelineAction;

@Command(group = "abc", name = "ppl")
@ActionPipeline(tasks = {
        @ActionTask(name = "extract", action = "reflect://help"),
        @ActionTask(name = "encode", action = "text-util://base64-encode?s=arg()&t=val()&u=ref()&v=res()")
})
public class SimplePipelineAction extends PipelineAction {


    private String content;
}
