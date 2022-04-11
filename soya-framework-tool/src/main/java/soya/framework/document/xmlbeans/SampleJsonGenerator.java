package soya.framework.document.xmlbeans;

import com.google.gson.JsonParser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import soya.framework.core.Command;
import soya.framework.transform.schema.avro.SampleAvroGenerator;
import soya.framework.transform.schema.converter.XsdToAvsc;

import java.util.Random;

@Command(group = "document", name = "xmlbeans-sample-json", httpRequestTypes = Command.MediaType.APPLICATION_JSON)
public class SampleJsonGenerator extends XmlBeansCommand {

    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        Schema schema = XsdToAvsc.fromXmlSchema(tree.origin());
        Object result = new SampleAvroGenerator(schema, new Random(), 0).generate();
        GenericRecord genericRecord = (GenericRecord) result;

        return GSON.toJson(JsonParser.parseString(genericRecord.toString()));
    }
}
