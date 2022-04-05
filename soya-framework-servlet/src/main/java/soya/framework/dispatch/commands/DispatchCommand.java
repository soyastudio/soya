package soya.framework.dispatch.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import soya.framework.commons.cli.CommandCallable;

public abstract class DispatchCommand<T> implements CommandCallable<T> {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected String toJson(Object o) {
        if (o == null) {
            return null;
        }

        return GSON.toJson(o);
    }

}
