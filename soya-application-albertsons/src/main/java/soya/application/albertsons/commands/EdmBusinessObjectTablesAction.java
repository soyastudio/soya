package soya.application.albertsons.commands;

import com.google.gson.Gson;
import soya.framework.action.Command;

@Command(group = "business-object-edm", name = "edm-bod-tables",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON, Command.MediaType.TEXT_PLAIN})
public class EdmBusinessObjectTablesAction extends EdmMasterMappingAction {

    @Override
    protected String render() {
        return new Gson().toJson(tables.keySet());
    }
}
