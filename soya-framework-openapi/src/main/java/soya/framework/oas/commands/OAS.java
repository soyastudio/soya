package soya.framework.oas.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandCallable;

public interface OAS<T> extends CommandCallable<T> {

    @Command(group = "openapi", name = "swagger", httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
    class SwaggerCommand implements OAS<String> {
        @Override
        public String call() throws Exception {
            return (String) dispatch(this);
        }
    }

    static Object dispatch(OAS command) {
        return null;
    }
}
