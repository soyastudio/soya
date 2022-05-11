package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.tasks.apache.xmlbeans.SampleXmlGenerator;

@Command(group = "business-object-analysis", name = "sample-cmm-xml", httpMethod = Command.HttpMethod.GET)
public final class SampleCmmTask extends SchemaTask<SampleXmlGenerator> {
}
