package soya.framework.dovetails.batch.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonPathTransformer {
    private static Map<String, JsonPathTransformer> cached = new ConcurrentHashMap<>();

    private final String uri;

    private JsonPathTransformer(String uri) {
        this.uri = uri;
    }

    public static JsonPathTransformer fromUri(String uri) {
        if(!cached. containsKey(uri)) {
            JsonPathTransformer transformer = new JsonPathTransformer(uri);
            cached.put(uri, transformer);
        }
        return cached.get(uri);
    }
}
