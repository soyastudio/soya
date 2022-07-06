package soya.application.albertsons.commands;

import soya.framework.action.Command;

@Command(group = "business-object-management", name = "bod-read", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectReadAction extends BusinessObjectAction {
    @Override
    protected String execute() throws Exception {
        return null;
    }
}
