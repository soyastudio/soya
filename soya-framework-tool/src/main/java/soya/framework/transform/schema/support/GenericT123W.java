package soya.framework.transform.schema.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import soya.framework.transform.schema.Annotatable;
import soya.framework.transform.schema.T123W;

import java.util.LinkedHashMap;

public class GenericT123W<O, K extends Annotatable, F extends GenericT123W> implements T123W<O, K> {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected KnowledgeBuilder<O, K> baselineBuilder;
    protected LinkedHashMap<String, AnnotatorBuilder<K>> annotatorBuilders = new LinkedHashMap<>();
    protected LinkedHashMap<String, RendererBuilder> rendererBuilders = new LinkedHashMap<>();

    @Override
    public F baseline(KnowledgeBuilder<O, K> builder) throws FlowBuilderException {
        this.baselineBuilder = builder;
        return (F) this;
    }

    @Override
    public F annotator(AnnotatorBuilder<K> builder) throws FlowBuilderException {
        this.annotatorBuilders.put(builder.getName(), builder);
        return (F) this;
    }

    @Override
    public F renderer(RendererBuilder builder) throws FlowBuilderException {
        rendererBuilders.put(builder.getName(), builder);
        return (F) this;
    }

    public String flowInstance(String format) {
        JsonObject jsonObject = new JsonObject();

        JsonArray annotators = new JsonArray();
        annotatorBuilders.entrySet().forEach(e -> {
            String name = e.getKey();
            JsonObject object = new JsonObject();

            annotators.add(object);


        });
        jsonObject.add("annotator", annotators);

        JsonArray renderers = new JsonArray();
        rendererBuilders.entrySet().forEach(e -> {
            String name = e.getKey();
            JsonObject object = new JsonObject();

            annotators.add(object);


        });

        jsonObject.add("renderer", renderers);

        return gson.toJson(jsonObject);
    }



}
