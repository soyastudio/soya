package soya.framework.dispatch.servlet;


import soya.framework.core.TaskCallable;
import soya.framework.core.TaskResult;

import javax.servlet.http.HttpServletRequest;
import java.util.EventObject;

public abstract class DispatchRequestEvent extends EventObject {
    
    protected DispatchRequestEvent(HttpServletRequest source) {
        super(source);
    }
    
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) source;
    }
    
    public static PreDispatchEvent preDispatch(HttpServletRequest request, TaskCallable task) {
        return new PreDispatchEvent(request, task);
    }

    public static PostDispatchEvent postDispatch(HttpServletRequest request, TaskResult result) {
        return new PostDispatchEvent(request, result);
    }
    
    public static class PreDispatchEvent extends DispatchRequestEvent {
        private TaskCallable task;

        protected PreDispatchEvent(HttpServletRequest source, TaskCallable task) {
            super(source);
            this.task = task;
        }

        public TaskCallable getTask() {
            return task;
        }
    }
    
    public static class PostDispatchEvent extends DispatchRequestEvent {
        private TaskResult result;

        protected PostDispatchEvent(HttpServletRequest source, TaskResult result) {
            super(source);
            this.result = result;
        }

        public TaskResult getResult() {
            return result;
        }
    }
}
