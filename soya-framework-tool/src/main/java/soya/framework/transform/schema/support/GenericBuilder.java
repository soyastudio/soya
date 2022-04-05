package soya.framework.transform.schema.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import soya.framework.transform.schema.T123W;

public abstract class GenericBuilder<T, B> {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected String name;
    protected Class<? extends T> type;
    protected T123W.Configuration configuration;

    protected GenericBuilder() {
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public B name(String name) {
        this.name = name;
        return (B) this;
    }

    protected B type(Class<? extends T> type) {
        this.type = type;
        return (B) this;
    }

    public B configure(T123W.Configuration configuration) {
        this.configuration = configuration;
        return (B) this;
    }

    public T create() {
        return null;
    }

    public T create(T123W.Configuration configuration) {
        return null;
    }

    public static T123W.AnnotatorBuilder annotatorBuilder() {
        return new GenericAnnotatorBuilder();
    }

    public static T123W.RendererBuilder rendererBuilder() {
        return new GenericRendererBuilder();
    }

    public static T123W.Configuration fromJson(String json) {
        return new GenericConfiguration();
    }

    public static class GenericConfiguration implements T123W.Configuration {

    }

    public static class GenericAnnotatorBuilder extends GenericBuilder<T123W.Annotator, T123W.AnnotatorBuilder> implements T123W.AnnotatorBuilder {
        protected GenericAnnotatorBuilder() {
        }


        @Override
        public T123W.AnnotatorBuilder annotatorType(Class type) {
            return super.type(type);
        }
    }

    public static class GenericRendererBuilder extends GenericBuilder<T123W.Renderer, T123W.RendererBuilder> implements T123W.RendererBuilder {
        protected GenericRendererBuilder() {
        }

        @Override
        public T123W.RendererBuilder rendererType(Class type) {
            return super.type(type);
        }
    }
}
