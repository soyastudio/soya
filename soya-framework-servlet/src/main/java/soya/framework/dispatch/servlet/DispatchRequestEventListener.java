package soya.framework.dispatch.servlet;

import java.util.EventListener;

public interface DispatchRequestEventListener extends EventListener {
        void onEvent(DispatchRequestEvent event);
}
