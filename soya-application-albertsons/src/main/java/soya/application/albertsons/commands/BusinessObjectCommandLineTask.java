package soya.application.albertsons.commands;

import soya.framework.commandline.Command;
import soya.framework.commandline.TaskName;
import soya.framework.commandline.tasks.CommandLineTask;

import java.util.HashMap;
import java.util.Map;

@Command(group = "business-object-management", name = "commandline", httpRequestTypes = Command.MediaType.TEXT_PLAIN)
public class BusinessObjectCommandLineTask extends CommandLineTask {
    private static Map<String, TaskName> mappings = new HashMap<>();

    static {
        // --------------- Analysis
        mappings.put("cmm", TaskName.fromTaskClass(XPathSchemaTask.class));
        mappings.put("avsc", TaskName.fromTaskClass(AvroSchemaTask.class));
        mappings.put("xml", TaskName.fromTaskClass(SampleCmmTask.class));
        mappings.put("avro", TaskName.fromTaskClass(SampleAvroTask.class));
        mappings.put("xlsx", TaskName.fromTaskClass(XlsxMappingsTask.class));

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
