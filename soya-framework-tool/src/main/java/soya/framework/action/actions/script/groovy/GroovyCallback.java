package soya.framework.action.actions.script.groovy;

import soya.framework.action.Flow;
import soya.framework.action.Resources;

public class GroovyCallback implements Flow.Callback {

    private String source;

    private GroovyCallback(String resource) {
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void onSuccess(Flow.Session session) throws Exception {
        String script = Resources.getResourceAsString(source);
    }

    public static class Builder {
        private String script;

        private Builder() {
        }

        public Builder script(String script) {
            return this;
        }

        public GroovyCallback create() {
            return new GroovyCallback(script);
        }

    }
}
