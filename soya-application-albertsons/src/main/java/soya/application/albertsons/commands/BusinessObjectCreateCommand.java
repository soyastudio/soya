package soya.application.albertsons.commands;

import soya.framework.core.Command;

@Command(group = "business-object-management", name = "bod-create",
        httpMethod = Command.HttpMethod.POST,
        httpRequestTypes = Command.MediaType.APPLICATION_JSON,
        httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectCreateCommand extends BusinessObjectCommand {

    @Override
    protected String execute() throws Exception {
        return null;
    }
}
