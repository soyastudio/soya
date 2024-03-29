package soya.framework.action.actions.apache.xmlbeans;

import com.google.gson.JsonParser;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import soya.framework.action.Command;
import soya.framework.action.actions.transform.avro.SampleAvroGenerator;
import soya.framework.action.actions.transform.converter.XsdToAvsc;

import java.util.Random;

@Command(group = "transform", name = "xmlbeans-sample-json", httpRequestTypes = Command.MediaType.APPLICATION_JSON)
public class SampleJsonGenerator extends XmlBeansAction {

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
