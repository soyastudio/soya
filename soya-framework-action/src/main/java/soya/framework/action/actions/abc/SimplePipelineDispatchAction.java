package soya.framework.action.actions.abc;

import soya.framework.action.Command;
import soya.framework.action.dispatch.PipelineDispatchAction;

@Command(group = "abc", name = "pipeline")
public class SimplePipelineDispatchAction extends PipelineDispatchAction<String> {
}
