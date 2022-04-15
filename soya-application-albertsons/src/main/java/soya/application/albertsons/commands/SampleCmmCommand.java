package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.commands.apache.xmlbeans.SampleXmlGenerator;

@Command(group = "business-object-analysis", name = "sample-cmm-xml", httpMethod = Command.HttpMethod.GET)
public final class SampleCmmCommand extends SchemaCommand<SampleXmlGenerator> {
}
