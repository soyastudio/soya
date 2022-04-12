package soya.framework.core.commands.reflect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import soya.framework.core.CommandCallable;

public abstract class ReflectCommand<T> implements CommandCallable<T> {
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected String toJson(Object o) {
        if (o == null) {
            return null;
        }

        return GSON.toJson(o);
    }

}
