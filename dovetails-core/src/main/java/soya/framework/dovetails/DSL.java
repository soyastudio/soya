package soya.framework.dovetails;

public final class DSL {
    public static final String URL_TOKEN = "://";
    public static final int URL_TOKEN_LENGTH = URL_TOKEN.length();

    public static final String ID_TOKEN = ":";
    public static final int ID_TOKEN_LENGTH = ID_TOKEN.length();

    private final String schema;
    private final String name;
    private final String path;

    private DSL(String schema, String name, String path) {
        this.schema = schema;
        this.name = name;
        this.path = path;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(schema);
        if(name != null && name.trim().length() > 0) {
            builder.append(ID_TOKEN).append(name);
        }
        builder.append(URL_TOKEN);
        if(path != null) {
            builder.append(path);
        }

        return builder.toString();
    }

    public static DSL fromURI(String uri) {
        if (uri == null || !uri.contains(URL_TOKEN)) {
            throw new IllegalArgumentException("Can not parse uri: " + uri);
        }

        String schema = uri;
        String name = null;
        String path;

        int index = schema.indexOf(URL_TOKEN);
        path = schema.substring(index + URL_TOKEN_LENGTH);
        schema = schema.substring(0, index);

        if(schema.contains(ID_TOKEN)) {
            int comma = schema.indexOf(ID_TOKEN);
            name = schema.substring(comma + ID_TOKEN_LENGTH);
            schema = schema.substring(0, comma);

        }

        return new DSL(schema, name, path);

    }
}
