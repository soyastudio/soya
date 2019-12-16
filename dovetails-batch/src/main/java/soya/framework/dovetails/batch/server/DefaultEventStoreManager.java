package soya.framework.dovetails.batch.server;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


public class DefaultEventStoreManager implements EventStoreManager {

    @Autowired
    private CaffeineCacheManager caffeineCacheManager;

    private Cache<String, TraceableEvent> cache;

    @PostConstruct
    public void init() {
        cache = (Cache) caffeineCacheManager.getCache("Event").getNativeCache();
    }

    public List<TraceableEvent> list() {
        return new ArrayList<>(cache.asMap().values());
    }

    public TraceableEvent get(String id) {
        TraceableEvent event = cache.asMap().get(id);
        if(event != null) {
            event.close();
        }
        return event;
    }
}
