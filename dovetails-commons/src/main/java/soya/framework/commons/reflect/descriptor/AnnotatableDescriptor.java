package soya.framework.commons.reflect.descriptor;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public abstract class AnnotatableDescriptor {
    private ImmutableMap<String, Object> annotations;

    protected void setAnnotations(Map<String, Object> annotations) {
        if(annotations != null && !annotations.isEmpty()) {
            this.annotations = ImmutableMap.copyOf(annotations);
        } else {
            this.annotations = null;
        }
    }

    public <T> T getAnnotation(String key, Class<T> type) {
        return (T) annotations.get(key);
    }
}
