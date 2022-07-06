package soya.application.albertsons.commands;

import soya.framework.action.Command;

@Command(group = "business-object-management", name = "bod-version", httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectVersionAction extends BusinessObjectAction {
    @Override
    protected String execute() throws Exception {
        return null;
    }
}
