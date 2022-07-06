package soya.application.albertsons.commands;

import soya.framework.action.Command;
import soya.framework.action.actions.apache.xmlbeans.SchemaReader;

@Command(group = "business-object-analysis", name = "schema", httpMethod = Command.HttpMethod.GET)
public class XPathSchemaAction extends SchemaAction<SchemaReader> {
}
