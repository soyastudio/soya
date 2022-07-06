package soya.framework.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

public interface ActionResource {

    URI uri();

    String getAsString(Charset encoding) throws IOException;

    InputStream getAsInputStream() throws IOException;

}
