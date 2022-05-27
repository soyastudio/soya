package soya.application.albertsons.commands;

import soya.framework.commandline.Command;
import soya.framework.commandline.tasks.apache.xmlbeans.SampleJsonGenerator;

@Command(group = "business-object-analysis", name = "sample-avro", httpMethod = Command.HttpMethod.GET)
public final class SampleAvroTask extends SchemaTask<SampleJsonGenerator> {
}
