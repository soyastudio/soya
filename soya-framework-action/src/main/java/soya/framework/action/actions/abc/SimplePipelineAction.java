package soya.framework.action.actions.abc;

import soya.framework.action.Command;
import soya.framework.action.dispatch.PipelineAction;

@Command(group = "abc", name = "pipeline")
public class SimplePipelineAction extends PipelineAction<String> {
}
