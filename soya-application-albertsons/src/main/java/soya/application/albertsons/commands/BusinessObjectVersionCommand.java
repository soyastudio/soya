package soya.application.albertsons.commands;

import soya.framework.core.Command;

@Command(group = "business-object-management", name = "bod-version", httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectVersionCommand extends BusinessObjectCommand{
    @Override
    protected String execute() throws Exception {
        return null;
    }
}
