package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.commands.transform.xmlbeans.AvroSchemaConverter;

@Command(group = "business-object-analysis", name = "avsc", httpMethod = Command.HttpMethod.GET)
public final class AvroSchemaCommand extends SchemaCommand<AvroSchemaConverter> {
}
