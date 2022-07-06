package soya.application.albertsons.commands;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.action.CommandOption;
import soya.framework.action.actions.apache.xmlbeans.xs.XsKnowledgeSystem;
import soya.framework.action.actions.apache.xmlbeans.xs.XsNode;
import soya.framework.knowledge.KnowledgeTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class XPathMappingsAction extends BusinessObjectAction {

    @CommandOption(option = "m")
    protected String mappingFile = XPATH_MAPPINGS_FILE;

    protected KnowledgeTree<SchemaTypeSystem, XsNode> tree;
    protected Map<String, Mapping> mappings = new LinkedHashMap<>();

    @Override
    protected void init() throws Exception {
        super.init();
        this.tree = XsKnowledgeSystem.knowledgeTree(new File(cmmFile));

        File file = new File(workDir, mappingFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null) {
            if (line.length() > 0 && !line.trim().startsWith("#") && line.contains("=")) {
                String key = line.substring(0, line.indexOf("=")).trim();
                String value = line.substring(line.indexOf("=") + 1).trim();

                Mapping mapping = new Mapping(toFunctions(value));
                mappings.put(key, mapping);
            }

            line = reader.readLine();
        }
    }

    @Override
    protected String execute() throws Exception {
        annotate();
        return render();
    }

    protected void annotate() throws Exception {

    }

    protected abstract String render() throws Exception;
}
