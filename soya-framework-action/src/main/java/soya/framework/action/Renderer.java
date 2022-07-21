package soya.framework.action;

import java.io.IOException;

public class Renderer {

    public void render(ActionResult result) throws IOException {
        Command.MediaType type = ActionClass
                .get(result.name())
                .getActionType()
                .getAnnotation(Command.class)
                .httpResponseTypes()[0];


    }
}
