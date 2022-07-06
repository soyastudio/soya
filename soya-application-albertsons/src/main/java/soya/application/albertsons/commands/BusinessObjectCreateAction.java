package soya.application.albertsons.commands;

import soya.framework.action.Command;

@Command(group = "business-object-management", name = "bod-create",
        httpMethod = Command.HttpMethod.POST,
        httpRequestTypes = Command.MediaType.APPLICATION_JSON,
        httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectCreateAction extends BusinessObjectAction {

    @Override
    protected String execute() throws Exception {
        return null;
    }
}
