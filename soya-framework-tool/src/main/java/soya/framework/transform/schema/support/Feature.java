package soya.framework.transform.schema.support;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.transform.schema.Annotatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Feature<T> implements Annotatable<T> {

    private static Gson gson = new Gson();

    private final T origin;
    protected Map<String, Object> annotations = new ConcurrentHashMap<>();

    protected Feature(T origin) {
        this.origin = origin;
    }

    public T origin() {
        return origin;
    }

    @Override
    public void annotate(String namespace, Object annotation) {
        if (annotation == null) {
            annotations.remove(namespace);
        } else {
            annotations.put(namespace, annotation);
        }
    }

    @Override
    public Object getAnnotation(String namespace) {
        return annotations.get(namespace);
    }

    @Override
    public <A> A getAnnotation(String namespace, Class<A> annotationType) {
        if(!annotations.containsKey(namespace)) {
            return null;
        }

        Object value = annotations.get(namespace);

        return (A) value;
    }
}
