package soya.framework.action;

import java.io.IOException;
import java.io.OutputStream;

public interface ActionResultRenderer {

    void render(ActionResult result, OutputStream outputStream) throws IOException;

    String toString(ActionResult result);
}
