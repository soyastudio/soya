package soya.application.albertsons.commands;

import soya.framework.action.Command;

import java.util.ArrayList;
import java.util.List;

@Command(group = "business-object-edm", name = "edm-table-structure", httpMethod = Command.HttpMethod.GET)
public class EdmTableStructureAction extends EdmMappingsAction {

    @Override
    protected String render() {
        List<EdmTable> list = new ArrayList<>(tables.values());
        return GSON.toJson(list);
    }
}
