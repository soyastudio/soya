package soya.application.albertsons.commands;

import soya.framework.action.Command;
import soya.framework.action.ActionName;
import soya.framework.action.actions.CommandLineAction;

import java.util.HashMap;
import java.util.Map;

@Command(group = "business-object-management", name = "commandline", httpRequestTypes = Command.MediaType.TEXT_PLAIN)
public class BusinessObjectCommandLineAction extends CommandLineAction {
    private static Map<String, ActionName> mappings = new HashMap<>();

    static {
        // --------------- Analysis
        mappings.put("cmm", ActionName.fromClass(XPathSchemaAction.class));
        mappings.put("avsc", ActionName.fromClass(AvroSchemaAction.class));
        mappings.put("xml", ActionName.fromClass(SampleCmmAction.class));
        mappings.put("avro", ActionName.fromClass(SampleAvroAction.class));
        mappings.put("xlsx", ActionName.fromClass(XlsxMappingsAction.class));

        // --------------- Development
       /* mappings.put("mappings", "business-object-development://mappings");
        mappings.put("validation", "business-object-development://mappings-validation");
        mappings.put("override", "business-object-development://mappings-override");*/

        // --------------- Management
        /*mappings.put("create", "business-object-management://bod-create");
        mappings.put("read", "business-object-management://bod-read");
        mappings.put("update", "business-object-management://bod-update");
        mappings.put("version", "business-object-management://bod-version");*/

    }

    @Override
    protected void loadCommand() {
        aliasMap.putAll(mappings);
    }
}
