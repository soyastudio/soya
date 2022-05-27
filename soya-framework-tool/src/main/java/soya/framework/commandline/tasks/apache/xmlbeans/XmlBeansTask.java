package soya.framework.commandline.tasks.apache.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.commandline.tasks.TransformTask;
import soya.framework.commandline.tasks.apache.xmlbeans.xs.XsKnowledgeSystem;
import soya.framework.commandline.tasks.apache.xmlbeans.xs.XsNode;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.Resource;
import soya.framework.knowledge.KnowledgeTree;

import java.io.File;
import java.net.URI;

public abstract class XmlBeansTask extends TransformTask<String> {
    protected Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @CommandOption(option = "s", required = true)
    protected String source;

    protected KnowledgeTree<SchemaTypeSystem, XsNode> tree;

    @Override
    protected void init() throws Exception {
        this.tree = extract(Resource.create(source));
    }

    protected KnowledgeTree<SchemaTypeSystem, XsNode> extract(Resource resource) throws Exception {
        if (source.startsWith("file:///")) {
            String fileName = URI.create(source).toURL().getFile();
            return XsKnowledgeSystem.knowledgeTree(new File(fileName));

        } else {
            return XsKnowledgeSystem.knowledgeTree(Resource.create(source).getAsInputStream());

        }
    }

    @Override
    protected String execute() throws Exception {
        process();
        return render();
    }

    protected abstract void process() throws Exception;

    protected abstract String render();


}
