package soya.application.albertsons.commands;

import soya.framework.action.Command;
import soya.framework.action.actions.apache.xmlbeans.SampleXmlGenerator;

@Command(group = "business-object-analysis", name = "sample-cmm-xml", httpMethod = Command.HttpMethod.GET)
public final class SampleCmmAction extends SchemaAction<SampleXmlGenerator> {
}
