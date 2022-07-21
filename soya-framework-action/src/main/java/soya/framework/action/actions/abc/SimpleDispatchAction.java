package soya.framework.action.actions.abc;

import soya.framework.action.Command;
import soya.framework.action.dispatch.DispatchAction;

@Command(group = "abc", name = "forward")
public class SimpleDispatchAction extends DispatchAction<String> {
}
