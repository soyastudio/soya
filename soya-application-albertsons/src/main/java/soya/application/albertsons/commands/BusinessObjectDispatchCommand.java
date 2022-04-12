package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.core.commands.DispatchCommand;

import java.util.HashMap;
import java.util.Map;

@Command(group = "business-object-management", name = "commandline", httpRequestTypes = Command.MediaType.TEXT_PLAIN)
public class BusinessObjectDispatchCommand extends DispatchCommand {
    private static Map<String, String> mappings = new HashMap<>();

    static {
        // --------------- Analysis
        mappings.put("cmm", "business-object-analysis://schema");
        mappings.put("avsc", "business-object-analysis://avsc");
        mappings.put("xml", "business-object-analysis://sample-cmm-xml");
        mappings.put("avro", "business-object-analysis://sample-avro");
        mappings.put("xlsx", "business-object-analysis://xlsx-mappings");

        // --------------- Development
        mappings.put("mappings", "business-object-development://mappings");
        mappings.put("validation", "business-object-development://mappings-validation");
        mappings.put("override", "business-object-development://mappings-override");

        // --------------- Management
        mappings.put("create", "business-object-management://bod-create");
        mappings.put("read", "business-object-management://bod-read");
        mappings.put("update", "business-object-management://bod-update");
        mappings.put("version", "business-object-management://bod-version");

    }

    @Override
    protected void loadCommand() {
        commandMappings.putAll(mappings);
    }
}
