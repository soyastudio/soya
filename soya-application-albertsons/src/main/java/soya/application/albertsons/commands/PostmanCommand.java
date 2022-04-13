package soya.application.albertsons.commands;

import soya.framework.core.Command;

@Command(group = "business-object-management", name = "postman",
        httpMethod = Command.HttpMethod.GET,
        httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class PostmanCommand extends BusinessObjectCommand {

    @Override
    protected String execute() throws Exception {
        return null;
    }
}
