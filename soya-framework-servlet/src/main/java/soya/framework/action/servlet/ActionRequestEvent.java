package soya.framework.action.servlet;


import soya.framework.action.ActionCallable;
import soya.framework.action.ActionResult;

import javax.servlet.http.HttpServletRequest;
import java.util.EventObject;

public abstract class ActionRequestEvent extends EventObject {
    
    protected ActionRequestEvent(HttpServletRequest source) {
        super(source);
    }
    
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) source;
    }
    
    public static PreActionEvent preDispatch(HttpServletRequest request, ActionCallable task) {
        return new PreActionEvent(request, task);
    }

    public static PostActionEvent postDispatch(HttpServletRequest request, ActionResult result) {
        return new PostActionEvent(request, result);
    }
    
    public static class PreActionEvent extends ActionRequestEvent {
        private ActionCallable task;

        protected PreActionEvent(HttpServletRequest source, ActionCallable task) {
            super(source);
            this.task = task;
        }

        public ActionCallable getTask() {
            return task;
        }
    }
    
    public static class PostActionEvent extends ActionRequestEvent {
        private ActionResult result;

        protected PostActionEvent(HttpServletRequest source, ActionResult result) {
            super(source);
            this.result = result;
        }

        public ActionResult getResult() {
            return result;
        }
    }
}
