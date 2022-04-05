package soya.framework.albertsons.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.everit.json.schema.Schema;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Command(group = "bod", name = "json-schema", httpMethod = Command.HttpMethod.GET)
public class JsonSchemaCommand extends BusinessObjectCommand {

    public static final String JSON_SCHEMA_FILE = "source-schema.json";

    @CommandOption(option = "x", longOption = "jsonSchema")
    protected String jsonSchemaFile = JSON_SCHEMA_FILE;

    protected Map<String, Schema> schemaMap = new LinkedHashMap<>();

    @Override
    protected String execute() throws Exception {

        File file = jsonSchemaFile != null ? new File(workDir, jsonSchemaFile) : new File(workDir, JSON_SCHEMA_FILE);

        JsonObject root = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
        StringBuilder builder = new StringBuilder();
        builder.append("$=object").append("\n");

        String parent = "$";
        root.get("properties").getAsJsonObject().entrySet().forEach(p -> {
            String propName = p.getKey();
            JsonObject propValue = p.getValue().getAsJsonObject();
            print(parent, propName, propValue, builder);

        });

        return builder.toString();
    }

    private void print(String parent, String name, JsonObject value, StringBuilder builder) {
        String type = value.get("type").getAsString();
        String path = parent + "." + name;
        if ("array".equals(type)) {
            path = path + "[*]";
            JsonObject items = value.get("items").getAsJsonObject();
            String elementType = items.get("type").getAsString();
            builder.append(path).append("=").append(elementType).append("\n");


            if ("object".equals(elementType)) {
                JsonObject elp = items.get("properties").getAsJsonObject();
                final String sub = path;
                elp.entrySet().forEach(e -> {
                    print(sub, e.getKey(), e.getValue().getAsJsonObject(), builder);
                });
            }

        } else if ("object".equals(type)) {
            if (value.get("properties") == null) {
                builder.append(path).append("=???").append("\n");

            } else {

                builder.append(path).append("=").append(type).append("\n");
                JsonObject props = value.get("properties").getAsJsonObject();

                String subParent = path;
                props.entrySet().forEach(e -> {
                    print(subParent, e.getKey(), e.getValue().getAsJsonObject(), builder);

                });

            }

        } else {
            builder.append(parent).append(".").append(name).append("=").append(type).append("\n");

        }

    }


}
