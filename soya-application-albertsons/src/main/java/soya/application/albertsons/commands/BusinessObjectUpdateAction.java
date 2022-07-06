package soya.application.albertsons.commands;

import soya.framework.action.Command;

@Command(group = "business-object-management", name = "bod-update",
        httpMethod = Command.HttpMethod.PUT,
        httpRequestTypes = Command.MediaType.APPLICATION_JSON,
        httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectUpdateAction extends BusinessObjectAction {
    @Override
    protected String execute() throws Exception {
        return null;
    }
}
