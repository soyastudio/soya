package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.commands.transform.xmlbeans.SchemaReader;

@Command(group = "business-object-analysis", name = "schema", httpMethod = Command.HttpMethod.GET)
public class XPathSchemaCommand extends SchemaCommand<SchemaReader> {
}
