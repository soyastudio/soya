package soya.framework.commands.apache.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.core.Resources;
import soya.framework.core.commands.ResourceCommand;
import soya.framework.commands.TransformCommand;
import soya.framework.commands.apache.xmlbeans.xs.XsKnowledgeBase;
import soya.framework.commands.apache.xmlbeans.xs.XsNode;
import soya.framework.kt.KnowledgeTree;

import java.io.File;

public abstract class XmlBeansCommand extends ResourceCommand implements TransformCommand<String> {
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected KnowledgeTree<SchemaTypeSystem, XsNode> tree;

    @Override
    public String call() throws Exception {
        this.tree = extract();
        process();
        return render();
    }

    protected KnowledgeTree<SchemaTypeSystem, XsNode> extract() throws Exception {
        if(source.startsWith("file:///")) {
            String fileName = source.substring("file:///".length());
            return XsKnowledgeBase.builder()
                    .file(new File(fileName))
                    .create().knowledge();
        } else {
            return XsKnowledgeBase.builder()
                    .string(Resources.getResourceAsString(source))
                    .create().knowledge();

        }
    }

    protected abstract void process() throws Exception;

    protected abstract String render();
}
