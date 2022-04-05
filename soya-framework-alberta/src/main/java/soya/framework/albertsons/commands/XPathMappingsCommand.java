package soya.framework.albertsons.commands;

import soya.framework.core.CommandOption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class XPathMappingsCommand extends SchemaCommand {

    @CommandOption(option = "m", longOption = "mappingFile")
    protected String mappingFile = XPATH_MAPPINGS_FILE;

    protected Map<String, Mapping> mappings = new LinkedHashMap<>();

    @Override
    protected void process() throws Exception {
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

        annotate();
    }

    protected abstract void annotate() throws Exception;

}
