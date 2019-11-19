package soya.framework.dovetails.component.context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskDef;
import soya.framework.dovetails.support.GenericTaskBuilder;
import soya.framework.dovetails.support.DefaultProcessContext;
import soya.framework.util.PropertiesUtils;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

@TaskDef(schema = "config")
public final class ConfigureTaskBuilder extends ContextBuildTaskBuilder<ConfigureTask> {

}
