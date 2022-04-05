package soya.framework.albertsons.commands;

import org.everit.json.schema.*;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import soya.framework.commons.cli.Command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

@Command(group = "bod", name = "json-sample", httpMethod = Command.HttpMethod.GET)
public class JsonSampleGenCommand extends JsonSchemaCommand {

    private static String alphabet = "abcdefghijklmnopqrstuvwxyz";

    @Override
    protected String execute() throws Exception {
        Schema schema = jsonSchema();
        return generate(schema).toString();
    }

    protected Schema jsonSchema() throws FileNotFoundException {
        String file = jsonSchemaFile == null? JSON_SCHEMA_FILE : jsonSchemaFile;

        File jsonSchemaFile = new File(workDir, file);
        InputStream inputStream = new FileInputStream(jsonSchemaFile);
        return SchemaLoader.load(new JSONObject(new JSONTokener(inputStream)));
    }

    private static Object generate(Schema schema) {
        if(schema instanceof ObjectSchema) {
            return generateObject((ObjectSchema) schema);

        } else if(schema instanceof ArraySchema) {
            return generateArray((ArraySchema) schema);

        } else if(schema instanceof BooleanSchema) {
            return generateBoolean((BooleanSchema) schema);

        } else if (schema instanceof NumberSchema) {
            return generateNumber((NumberSchema) schema);

        } else if (schema instanceof StringSchema) {
            return generateString((StringSchema) schema);

        }
        return null;
    }

    private static JSONObject generateObject(ObjectSchema schema) {
        Map<String, Schema> map = schema.getPropertySchemas();
        JSONObject object = new JSONObject();
        for (Map.Entry<String, Schema> entry : map.entrySet()) {
            String key = entry.getKey();
            object.put(key, generate(entry.getValue()));
        }

        return object;
    }

    private static JSONArray generateArray(ArraySchema schema) {
        Schema itemSchema = schema.getAllItemSchema();

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(generate(itemSchema));

        return jsonArray;
    }

    private static String generateString(StringSchema schema) {
        String formatName = ((StringSchema) schema).getFormatValidator().formatName();
        if (formatName != null && formatName.equals("date-time")) {
            return "2019-09-18T22:30:01Z";
        }

        int length = 7;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(alphabet.length());
            char c = alphabet.charAt(index);
            sb.append(c);
        }
        return sb.toString();
    }

    private static Number generateNumber(NumberSchema schema) {
        return 1;
    }

    private static Boolean generateBoolean(BooleanSchema schema) {
        return false;
    }

    private static Object generateEnum(EnumSchema schema) {
        return schema.getPossibleValuesAsList().get(0);
    }

    private static Object generateReference(ReferenceSchema schema) {
        return generate(schema.getReferredSchema());
    }

}
