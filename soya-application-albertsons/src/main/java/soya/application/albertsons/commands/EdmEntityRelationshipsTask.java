package soya.application.albertsons.commands;

import soya.framework.commandline.Command;

import java.util.ArrayList;
import java.util.List;

@Command(group = "business-object-edm", name = "edm-entity-relationships", httpMethod = Command.HttpMethod.GET)
public class EdmEntityRelationshipsTask extends EdmMappingsTask {

    @Override
    protected String render() {
        List<EdmTable> list = new ArrayList<>(tables.values());
        return GSON.toJson(list);
    }
}
