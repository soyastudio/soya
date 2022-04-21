package soya.application.albertsons.commands;

import com.google.gson.Gson;
import soya.framework.core.Command;

@Command(group = "business-object-edm", name = "edm-bod-tables",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON, Command.MediaType.TEXT_PLAIN})
public class EdmBusinessObjectTablesCommand extends EdmMasterMappingCommand {

    @Override
    protected String render() {
        return new Gson().toJson(tables.keySet());
    }
}
