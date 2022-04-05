package soya.framework.core.commands.text;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import soya.framework.core.Command;
import soya.framework.core.commands.ResourceCommand;

@Command(group = "text-util", name = "json-format")
public class JsonBeautifyCommand extends ResourceCommand {
    @Override
    public String call() throws Exception {
        return new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(contents()));
    }
}
