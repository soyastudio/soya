package soya.framework.dovetails.batch.service;

import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MustacheTransformer {
    private static Map<String, MustacheTransformer> cached = new ConcurrentHashMap<>();

    private final String uri;
    private Template template;

    private MustacheTransformer(String uri) {
        this.uri = uri;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(uri);
            template = Mustache.compiler().compile(new InputStreamReader(is));

        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Resource not found: " + uri);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);

        }
    }

    public String transform(String data) throws TransformerException {
        try {
            JsonElement jsonElement = JsonParser.parseString(data);
            Object variables = JsonUtils.jsonToMap(data);

            return template.execute(variables);

        } catch (Exception e) {
            throw new TransformerException(e);

        }
    }
    
    public static MustacheTransformer fromUri(String uri) {
        if(!cached.containsKey(uri)) {
            MustacheTransformer transformer = new MustacheTransformer(uri);
            cached.put(uri, transformer);
        }
        return cached.get(uri);
    }
}
