package soya.framework.action;

import java.io.IOException;

public interface ActionResultRenderer<T> {
    void render(ActionResult result, T dest) throws IOException;
}
