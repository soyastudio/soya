package soya.application.albertsons.commands;

import soya.framework.core.Command;

@Command(group = "business-object-management", name = "bod-update",
        httpMethod = Command.HttpMethod.PUT,
        httpRequestTypes = Command.MediaType.APPLICATION_JSON,
        httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectUpdateCommand extends BusinessObjectCommand{
    @Override
    protected String execute() throws Exception {
        return null;
    }
}
