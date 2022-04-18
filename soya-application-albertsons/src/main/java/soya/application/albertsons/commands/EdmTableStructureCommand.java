package soya.application.albertsons.commands;

import soya.framework.core.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Command(group = "business-object-edm", name = "edm-table-structure", httpMethod = Command.HttpMethod.GET)
public class EdmTableStructureCommand extends EdmMappingsCommand {

    @Override
    protected String render() {
        List<EdmTable> list = new ArrayList<>(tables.values());
        return GSON.toJson(list);
    }
}
