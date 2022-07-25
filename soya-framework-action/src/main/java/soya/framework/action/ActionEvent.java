package soya.framework.action;

import java.net.URI;
import java.util.EventObject;
import java.util.UUID;

public class ActionEvent extends EventObject {
    private final URI uri;
    private final String uuid;
    private final long timestamp;

    public ActionEvent(Object source, URI uri) {
        super(source);
        this.uri = ActionName.fromURI(uri).toURI();
        this.uuid = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public URI getUri() {
        return uri;
    }

    public String getId() {
        return uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
