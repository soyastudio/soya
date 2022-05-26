package soya.application.albertsons.commands;

import soya.framework.commandline.Command;
import soya.framework.tasks.apache.xmlbeans.SchemaReader;

@Command(group = "business-object-analysis", name = "schema", httpMethod = Command.HttpMethod.GET)
public class XPathSchemaTask extends SchemaTask<SchemaReader> {
}
