package soya.framework.dovetails;

public abstract class Task implements TaskProcessor {

    private final DSL dsl;

    protected Task(String uri) {
        this.dsl = DSL.fromURI(uri);
    }

    public String getUri() {
        return dsl.toString();
    }

    public String getSchema() {
        return dsl.getSchema();
    }

    public String getName() {
        return dsl.getName();
    }

    public String getPath() {
        return dsl.getPath();
    }
}
