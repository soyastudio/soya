package soya.framework.action;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

public final class URIParser {
    private URIParser() {
    }

    public static URI toURI(String commandline) {
        StringBuilder builder = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(commandline);
        String uri = null;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (uri == null) {
                uri = token;
                builder.append(uri);

            } else if (token.startsWith("-")) {
                if (builder.length() == uri.length()) {
                    builder.append("?");
                } else {
                    builder.append("&");
                }

                if (token.startsWith("--")) {
                    builder.append(token.substring(2));
                } else {
                    builder.append(token.substring(1));
                }
                builder.append("=");

            } else {
                builder.append(token);
            }
        }

        return URI.create(builder.toString());

    }

    public static Map<String, List<String>> splitQuery(String query) {
        Map<String, List<String>> params = new HashMap<>();
        try {
            params =  splitQuery(query, "UTF-8");

        } catch (UnsupportedEncodingException e) {

        }
        return params;
    }

    public static Map<String, List<String>> splitQuery(String query, String encoding) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), encoding) : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }
}
