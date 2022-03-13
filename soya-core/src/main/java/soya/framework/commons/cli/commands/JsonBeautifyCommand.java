package soya.framework.commons.cli.commands;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import soya.framework.commons.cli.Command;

@Command(name = "json-format", uri = "resource://json-format")
public class JsonBeautifyCommand extends ResourceCommand {
    @Override
    public String call() throws Exception {
        return new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(contents()));
    }
}
