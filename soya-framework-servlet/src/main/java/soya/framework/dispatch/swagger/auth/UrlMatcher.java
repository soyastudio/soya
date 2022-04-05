package soya.framework.dispatch.swagger.auth;

import java.net.URL;

public interface UrlMatcher {
    boolean test(URL url);
}
