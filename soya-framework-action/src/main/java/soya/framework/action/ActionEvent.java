package soya.framework.action;

import java.util.EventObject;

public class ActionEvent extends EventObject {
    private ActionName actionName;

    public ActionEvent(Object source) {
        super(source);
    }
}
