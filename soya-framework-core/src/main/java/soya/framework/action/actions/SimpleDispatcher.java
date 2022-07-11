package soya.framework.action.actions;

import soya.framework.action.dispatch.ActionDispatch;
import soya.framework.action.dispatch.ActionForward;
import soya.framework.action.dispatch.ActionOptionSetting;

@ActionDispatch
public interface SimpleDispatcher {

    @ActionForward(command = "text-util://base64-encode", options = {
            @ActionOptionSetting(option = "s", index = 0)
    })
    String hello(String msg);

}
