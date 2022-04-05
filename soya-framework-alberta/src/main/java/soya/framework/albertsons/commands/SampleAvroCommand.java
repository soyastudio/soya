package soya.framework.albertsons.commands;

import com.google.gson.JsonParser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import soya.framework.core.Command;
import soya.framework.transform.schema.avro.SampleAvroGenerator;
import soya.framework.transform.schema.converter.XsdToAvsc;

import java.util.Random;

@Command(group = "bod", name = "sample-avro", httpMethod = Command.HttpMethod.GET)
public class SampleAvroCommand extends SchemaCommand {

    @Override
    protected String render() {
        Schema schema = XsdToAvsc.fromXmlSchema(tree.origin());
        Object result = new SampleAvroGenerator(schema, new Random(), 0).generate();
        GenericRecord genericRecord = (GenericRecord) result;

        return GSON.toJson(JsonParser.parseString(genericRecord.toString()));
    }
}
