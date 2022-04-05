package soya.framework.transform.schema.xs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.transform.schema.KnowledgeTree;
import soya.framework.transform.schema.KnowledgeTreeNode;
import soya.framework.transform.schema.T123W;

public class XsTreeRenderer implements XsKnowledgeRenderer {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> tree) throws T123W.FlowExecutionException {

        JsonObject jsonObject = new JsonObject();
        KnowledgeTreeNode<XsNode> root = tree.root();
        jsonObject.add(root.getName(), createJsonElement(root));

        return gson.toJson(jsonObject);
    }

    private JsonElement createJsonElement(KnowledgeTreeNode<XsNode> treeNode) {
        JsonElement element = null;
        XsNode node = treeNode.origin();
        if(XsNode.XsNodeType.Folder.equals(node.getNodeType())) {
            JsonObject object = new JsonObject();
            treeNode.getChildren().forEach(e -> {
                object.add(e.getName(), createJsonElement((KnowledgeTreeNode<XsNode>) e));
            });

            element = object;

        } else {
            element = gson.toJsonTree(node);
        }

        return element;
    }
}
