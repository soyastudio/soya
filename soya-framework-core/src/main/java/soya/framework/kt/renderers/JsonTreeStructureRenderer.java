package soya.framework.kt.renderers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import soya.framework.kt.*;

public class JsonTreeStructureRenderer implements KnowledgeSystem.KnowledgeRenderer<String> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String render(KnowledgeTree knowledgeTree) throws KnowledgeProcessException {
        KnowledgeTreeNode<?> root = (KnowledgeTreeNode<?>) knowledgeTree.root();
        return GSON.toJson(fromTreeNode(root));
    }

    private JsonObject fromTreeNode(KnowledgeTreeNode<?> ktn) {
        JsonObject object = new JsonObject();
        object.addProperty("name", ktn.getName());
        object.addProperty("path", ktn.getPath());

        if(ktn.getChildren().size() > 0) {
            JsonArray arr = new JsonArray();
            ktn.getChildren().forEach(e -> {
                arr.add(fromTreeNode(e));
            });
            object.add("children", arr);
        }
        return object;
    }
}
