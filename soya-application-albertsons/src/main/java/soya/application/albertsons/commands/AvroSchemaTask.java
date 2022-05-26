package soya.application.albertsons.commands;

import soya.framework.commandline.Command;
import soya.framework.tasks.apache.xmlbeans.AvroSchemaConverter;

@Command(group = "business-object-analysis", name = "avsc", httpMethod = Command.HttpMethod.GET)
public final class AvroSchemaTask extends SchemaTask<AvroSchemaConverter> {
}
