package soya.framework.dovetails.batch.server;

import java.util.List;

public interface EventStoreManager {
    List<TraceableEvent> list();

    TraceableEvent get(String id);
}
