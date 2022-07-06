package soya.application.albertsons.commands;

import soya.framework.action.Command;
import soya.framework.action.actions.apache.xmlbeans.AvroSchemaConverter;

@Command(group = "business-object-analysis", name = "avsc", httpMethod = Command.HttpMethod.GET)
public final class AvroSchemaAction extends SchemaAction<AvroSchemaConverter> {
}
