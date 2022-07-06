package soya.application.albertsons.commands;

import soya.framework.action.Command;
import soya.framework.action.actions.apache.xmlbeans.SampleJsonGenerator;

@Command(group = "business-object-analysis", name = "sample-avro", httpMethod = Command.HttpMethod.GET)
public final class SampleAvroAction extends SchemaAction<SampleJsonGenerator> {
}
