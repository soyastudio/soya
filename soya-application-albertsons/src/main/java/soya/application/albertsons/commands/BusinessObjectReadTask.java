package soya.application.albertsons.commands;

import soya.framework.core.Command;

@Command(group = "business-object-management", name = "bod-read", httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class BusinessObjectReadTask extends BusinessObjectTask {
    @Override
    protected String execute() throws Exception {
        return null;
    }
}
