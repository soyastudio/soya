package soya.framework.action.servlet;

import java.util.EventListener;

public interface ActionRequestEventListener extends EventListener {
        void onEvent(ActionRequestEvent event);
}
