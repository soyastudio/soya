package soya.application.albertsons.commands;

import soya.framework.commandline.Command;

@Command(group = "business-object-management", name = "postman",
        httpMethod = Command.HttpMethod.GET,
        httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class PostmanTask extends BusinessObjectTask {

    @Override
    protected String execute() throws Exception {
        return null;
    }
}
