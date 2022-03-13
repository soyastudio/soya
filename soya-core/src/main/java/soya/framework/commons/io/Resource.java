package soya.framework.commons.io;

import org.apache.commons.codec.binary.Base64;
import soya.framework.commons.util.StringCompressUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Resource {
    private static Map<ResourceType, Parser<?>> parsers;

    static {
        parsers = new ConcurrentHashMap<>();
    }

    private final String src;
    private final ResourceType type;
    private Parser<?> parser;

    public Resource(String src) {
        this.src = process(src);
        this.type = guessType(this.src);
        this.parser = parsers.get(guessType(src));
    }

    public Resource(String src, Parser<?> parser) {
        this.src = process(src);
        this.type = guessType(this.src);
        this.parser = parser;
    }

    public String extractAsString() {
        return src;
    }

    private String process(String source) {
        String token = source.trim();
        if (Base64.isBase64(source)) {
            byte[] decoded = Base64.decodeBase64(source);
            if (StringCompressUtils.isZipped(decoded)) {
                token = StringCompressUtils.unzip(decoded);
            } else {
                token = new String(decoded);
            }

        }

        return token;
    }

    private ResourceType guessType(String source) {
        if (source.startsWith("{") && source.endsWith("}")
                || source.startsWith("[") && source.endsWith("]")) {
            return ResourceType.JSON;

        } else if(source.startsWith("<") && source.endsWith(">")) {
            return ResourceType.XML;

        } else if(isURL(source)){
            return ResourceType.URL;

        } else {
            return ResourceType.PLAIN;
        }
    }

    private boolean isURL(String source) {
        try {
            new URL(source);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static enum ResourceType {
        URL, FILE, GZIPPED, BASE64, JSON, XML, PLAIN;
    }

    public interface Parser<T> {
        T parse(String src);
    }


}
