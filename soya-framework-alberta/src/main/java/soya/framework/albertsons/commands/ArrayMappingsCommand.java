package soya.framework.albertsons.commands;

import soya.framework.core.Command;

@Command(group = "bod", name = "arrays")
public class ArrayMappingsCommand extends ConstructCommand {

    @Override
    protected String render() {
        return GSON.toJson(arrayMap.values());
    }
}
