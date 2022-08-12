package soya.framework.commons.eventbus;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public final class Event extends EventObject {

    private final String id;
    private final String rootId;
    private final long timestamp;

    private final String address;
    private final Map<String, List<String>> params;
    private final Object payload;

    private Event(Object source, URI uri, Object payload) {
        super(source);
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.payload = payload;
        this.rootId = (source instanceof Event) ? ((Event) source).rootId : id;

        this.address = uri.getScheme() + "://" + uri.getHost();
        String query = uri.getRawQuery();
        if (query == null) {
            this.params = Collections.emptyMap();
        } else {
            this.params = Arrays.stream(query.split("&"))
                    .map(this::splitQueryParameter)
                    .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
        }
    }

    public AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        try {
            return new AbstractMap.SimpleImmutableEntry<>(
                    URLDecoder.decode(key, "UTF-8"),
                    URLDecoder.decode(value, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddress() {
        return address;
    }

    public String[] getPath() {
        List<String> paths = new ArrayList<>();
        paths.add(id);

        Object src = this.source;
        while(src instanceof Event) {
            Event event = (Event) src;
            paths.add(0, event.id);
            src = event.getSource();
        }

        return paths.toArray(new String[paths.size()]);
    }

    public String toURI() {
        StringBuilder builder = new StringBuilder(address);

        for (String path: getPath()) {
            builder.append("/").append(path);
        }

        builder.append("?");

        params.entrySet().forEach(e -> {
            e.getValue().forEach(v -> {
                builder.append(e.getKey()).append("=").append(v).append("&");
            });
        });

        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public Set<String> parameterNames() {
        return params.keySet();
    }

    public String getParameter(String name) {
        try {
            return params.get(name).get(0);

        } catch (Exception e) {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    public static Builder builder(URI uri, Object source) {
        return new Builder(uri, source);
    }

    public static class Builder {
        private Object source;
        private StringBuilder uriBuilder = new StringBuilder();
        private Object payload;

        private Builder(URI baseURI, Object source) {
            this.source = source;
            this.uriBuilder.append(baseURI.getScheme()).append("://").append(baseURI.getHost()).append("?");
        }

        public Builder addParameter(String name, String value) {
            uriBuilder.append(name).append("=").append(value).append("&");
            return this;
        }

        public Builder setPayload(Object payload) {
            this.payload = payload;
            return this;
        }

        public Event create() {
            Event event = new Event(source, URI.create(uriBuilder.deleteCharAt(uriBuilder.length() - 1).toString()), payload);
            EventBus.publish(event);
            return event;
        }
    }
}
