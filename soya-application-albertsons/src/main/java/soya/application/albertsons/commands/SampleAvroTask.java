package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.tasks.apache.xmlbeans.SampleJsonGenerator;

@Command(group = "business-object-analysis", name = "sample-avro", httpMethod = Command.HttpMethod.GET)
public final class SampleAvroTask extends SchemaTask<SampleJsonGenerator> {
}
