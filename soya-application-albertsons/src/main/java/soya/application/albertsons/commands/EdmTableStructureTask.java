package soya.application.albertsons.commands;

import soya.framework.core.Command;

import java.util.ArrayList;
import java.util.List;

@Command(group = "business-object-edm", name = "edm-table-structure", httpMethod = Command.HttpMethod.GET)
public class EdmTableStructureTask extends EdmMappingsTask {

    @Override
    protected String render() {
        List<EdmTable> list = new ArrayList<>(tables.values());
        return GSON.toJson(list);
    }
}
