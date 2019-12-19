package soya.framework.dovetails.batch.service;

import org.springframework.stereotype.Service;

@Service
public class TransformService {

    public String jolt(String uri, String src) {
        return JoltTransformer.fromUri(uri).transform(src);
    }

    public String mustache(String uri, String data) {
        return MustacheTransformer.fromUri(uri).transform(data);
    }
}
