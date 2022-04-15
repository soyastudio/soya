package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.commands.apache.xmlbeans.SampleJsonGenerator;

@Command(group = "business-object-analysis", name = "sample-avro", httpMethod = Command.HttpMethod.GET)
public final class SampleAvroCommand extends SchemaCommand<SampleJsonGenerator> {
}
