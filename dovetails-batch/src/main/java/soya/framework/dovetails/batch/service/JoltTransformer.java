package soya.framework.dovetails.batch.service;

import com.bazaarvoice.jolt.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JoltTransformer {
    private static Map<String, JoltTransformer> cached = new ConcurrentHashMap<>();

    private final String uri;
    private JoltTransformType transformDsl = JoltTransformType.Chainr;
    private Transform transform;

    private JoltTransformer(String uri) {
        this.uri = uri;
        try {
            Object spec = JsonUtils.jsonToObject(getClass().getClassLoader().getResourceAsStream(uri));
            if (this.transformDsl == JoltTransformType.Sortr) {
                this.transform = new Sortr();

            } else {
                switch (this.transformDsl) {
                    case Shiftr:
                        this.transform = new Shiftr(spec);
                        break;
                    case Defaultr:
                        this.transform = new Defaultr(spec);
                        break;
                    case Removr:
                        this.transform = new Removr(spec);
                        break;
                    case Chainr:
                    default:
                        this.transform = Chainr.fromSpec(spec);
                        break;
                }
            }

        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Resource not found: " + uri);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);

        }
    }

    public String transform(String src)  throws TransformerException {

        try {
            if (transform != null) {
                Object inputJSON = JsonUtils.jsonToObject(src);
                Object transformedOutput = transform.transform(inputJSON);
                return JsonUtils.toPrettyJsonString(transformedOutput);

            } else {
                return src;
            }

        } catch (Exception e) {
            throw new TransformerException(e);

        }
    }

    public static JoltTransformer fromUri(String uri) {
        if(!cached.containsKey(uri)) {
            JoltTransformer transformer = new JoltTransformer(uri);
            cached.put(uri, transformer);
        }
        return cached.get(uri);
    }

    public enum JoltTransformType {
        Chainr,
        Shiftr,
        Defaultr,
        Removr,
        Sortr
    }
}

